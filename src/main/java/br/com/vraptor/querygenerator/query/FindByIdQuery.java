package br.com.vraptor.querygenerator.query;

import static br.com.vraptor.querygenerator.query.QueryReturnTypes.getReturnTypeFor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.hibernate.Session;

public class FindByIdQuery implements QueryParser {

	@Override
	public Object execute(Session session, Method method, Object[] args) {
		return session.load(getReturnTypeFor(method), (Serializable) args[0]);
	}

	@Override
	public boolean canHandle(String method) {
		return "findById".equals(method);
	}

}
