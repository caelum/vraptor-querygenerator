package br.com.vraptor.querygenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RepositoryHibernateInvocationHandler implements InvocationHandler {

	private final QueryExecutor executor;

	public RepositoryHibernateInvocationHandler(QueryExecutor executor) {
		this.executor = executor;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return executor.execute(method, args);
	}
}