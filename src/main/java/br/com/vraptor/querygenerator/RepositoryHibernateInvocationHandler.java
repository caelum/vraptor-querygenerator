package br.com.vraptor.querygenerator;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;

public class RepositoryHibernateInvocationHandler implements InvocationHandler {

	private final Session session;
	private final ParameterNameExtractors extractors;

	public RepositoryHibernateInvocationHandler(Session session, ParameterNameExtractors extractors) {
		this.session = session;
		this.extractors = extractors;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();

		if (name.equals("findById")) {
			return session.load(getReturnTypeForUniqueResult(method), (Serializable) args[0]);
		} else if (name.equals("findAll")) {
			return session.createCriteria(
					getReturnTypeForMultipleResults(method)).list();
		} else if (name.startsWith("findBy") || name.startsWith("findAllBy")) {

			Map<String, Object> parameters = new CriteriaBuilder(method).create(args);

			Class<?> returnType = getReturnTypeFor(method);
			Criteria criteria = session.createCriteria(returnType);

			for (String parameterKey : parameters.keySet()) {

				Object value = parameters.get(parameterKey);

				NameAndConditionExtractor extractor = extractors.getExtractorFor(parameterKey);
				String parameter = extractor.getName(parameterKey);

				if (value instanceof CriteriaAccumulator) {
					CriteriaAccumulator subQueryParameters = (CriteriaAccumulator) value;
					Criteria subQuery = criteria.createCriteria(parameter);
					subQueryParameters.applyTo(subQuery, extractors);
				} else {
					criteria.add(extractor.getRestriction(parameterKey, value));
				}
			}

			return finalize(method, criteria);
		}

		throw new RuntimeException("method not supported: " + method.getName());
	}

	private Object finalize(Method method, Criteria c) {
		return method.getName().startsWith("findAllBy") ? c.list() : c.uniqueResult();
	}

	static Class<?> getReturnTypeFor(Method method) {
		return method.getName().startsWith("findAllBy") ? getReturnTypeForMultipleResults(method)
				: getReturnTypeForUniqueResult(method);
	}

	static Class<?> getReturnTypeForMultipleResults(Method method) {
		return (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
	}

	static Class<?> getReturnTypeForUniqueResult(Method method) {
		return method.getReturnType();
	}
}