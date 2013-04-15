package br.com.vraptor.querygenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class And {

	@SuppressWarnings("unchecked")
	public static <T> T and(Class<T> type) {
		InvocationHandler handler = new SubQueryCreator();

		return (T) Proxy.newProxyInstance(
				RepositoryTest.class.getClassLoader(),
				new Class<?>[] { type },
				handler);
	}
}
