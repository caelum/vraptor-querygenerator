package br.com.vraptor.querygenerator.query;

import static br.com.vraptor.querygenerator.query.QueryReturnTypes.getReturnTypeForMultipleResults;

import java.lang.reflect.Method;

import org.hibernate.Session;

public class FindAllQuery implements QueryParser{

	@Override
	public Object execute(Session session, Method method, Object[] args) {
		return session.createCriteria(getReturnTypeForMultipleResults(method)).list();
	}

	@Override
	public boolean canHandle(String method) {
		return "findAll".equals(method);
	}

}
