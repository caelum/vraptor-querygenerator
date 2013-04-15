package br.com.vraptor.querygenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;

// proxifiador de repositorios, para fazer subquery

public class SubQueryCreator implements InvocationHandler, CriteriaAccumulator {

	private final List<NameAndValue> criterionsToAddLater = new ArrayList<>();
	private final Map<String, CriteriaAccumulator> accumulatorsToAddLater = new HashMap<>();

	class NameAndValue {
		String name;
		Object value;

		public NameAndValue(String name, Object value) {
			this.name = name;
			this.value = value;
		}

	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();

		if (name.startsWith("findBy") || name.startsWith("findAllBy")) {

			Map<String, Object> parameters = new CriteriaBuilder(method).create(args);

			for (String parameter : parameters.keySet()) {
				Object value = parameters.get(parameter);
				if (value instanceof CriteriaAccumulator) {
					CriteriaAccumulator subQueryParameters = (CriteriaAccumulator) value;
					accumulatorsToAddLater.put(parameter, subQueryParameters);
				} else {
					criterionsToAddLater.add(new NameAndValue(parameter, value));
				}
			}

			return proxyThis(RepositoryHibernateInvocationHandler.getReturnTypeFor(method));
		}

		throw new RuntimeException("method not supported: " + method.getName());
	}

	private <T> T proxyThis(Class<T> type) {
		InvocationHandler handler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.getDeclaringClass().equals(CriteriaAccumulator.class)) {
					return method.invoke(SubQueryCreator.this, args);
				}
				if (method.getDeclaringClass().equals(Object.class)) {
					return method.invoke(SubQueryCreator.this, args);
				}
				throw new RuntimeException("You should not be messing with proxies: " + method.getName());
			}

		};

		return (T) Proxy.newProxyInstance(
				RepositoryTest.class.getClassLoader(),
				new Class<?>[] { type, CriteriaAccumulator.class },
				handler);

	}

	@Override
	public void applyTo(Criteria criteria, ParameterNameExtractors extractors) {
		for (NameAndValue nameAndValue : criterionsToAddLater) {
			Criterion criterion = extractors.getExtractorFor(nameAndValue.name).getRestriction(nameAndValue.name,
					nameAndValue.value);
			criteria.add(criterion);
		}
		for (String name : accumulatorsToAddLater.keySet()) {
			Criteria subquery = criteria.createCriteria(name);
			accumulatorsToAddLater.get(name).applyTo(subquery, extractors);
		}
	}
}
