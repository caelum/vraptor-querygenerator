package br.com.vraptor.querygenerator.query;

import java.lang.reflect.Method;

import org.hibernate.Session;

public interface QueryParser {
	public Object execute(Session session, Method method, Object[] args);
	public boolean canHandle(String method);
}
