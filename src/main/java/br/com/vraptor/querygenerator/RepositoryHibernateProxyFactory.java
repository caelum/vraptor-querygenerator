package br.com.vraptor.querygenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class RepositoryHibernateProxyFactory {

	private final QueryExecutor executor;

	public RepositoryHibernateProxyFactory(QueryExecutor executor) {
		this.executor = executor;
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> clazz) {
		InvocationHandler handler = new RepositoryHibernateInvocationHandler(executor);

		return (T) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class<?>[] { clazz },
				handler);
	}
}