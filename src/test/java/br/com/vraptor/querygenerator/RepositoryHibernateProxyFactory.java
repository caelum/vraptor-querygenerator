package br.com.vraptor.querygenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.hibernate.Session;

public class RepositoryHibernateProxyFactory {

	private final Session session;

	public RepositoryHibernateProxyFactory(Session session) {
		this.session = session;
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> clazz) {
		InvocationHandler handler = new RepositoryHibernateInvocationHandler(session);

		return (T) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class<?>[] { clazz },
				handler);
	}
}