package br.com.vraptor.querygenerator;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class RepositoryHibernateInvocationHandler implements InvocationHandler {

	private final Session session;

	public RepositoryHibernateInvocationHandler(Session session) {
		this.session = session;
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

			for (String parameter : parameters.keySet()) {
				Object value = parameters.get(parameter);
				if (value instanceof CriteriaAccumulator) {
					CriteriaAccumulator subQueryParameters = (CriteriaAccumulator) value;
					Criteria subQuery = criteria.createCriteria(parameter);
					subQueryParameters.applyTo(subQuery);
				} else {
					criteria.add(Restrictions.eq(parameter, value));
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