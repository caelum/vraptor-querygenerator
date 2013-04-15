package br.com.vraptor.querygenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.hibernate.Session;

public class RepositoryHibernateProxyFactory {

	private final Session session;
	private final ParameterNameExtractors extractors;

	public RepositoryHibernateProxyFactory(Session session, ParameterNameExtractors extractors) {
		this.session = session;
		this.extractors = extractors;
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> clazz) {
		InvocationHandler handler = new RepositoryHibernateInvocationHandler(session, extractors);

		return (T) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class<?>[] { clazz },
				handler);
	}
}