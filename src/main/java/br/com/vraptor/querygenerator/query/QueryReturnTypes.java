package br.com.vraptor.querygenerator.query;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class QueryReturnTypes{
	public static Class<?> getReturnTypeFor(Method method) {
		return method.getName().startsWith("findAllBy") ? getReturnTypeForMultipleResults(method)
				: getReturnTypeForUniqueResult(method);
	}

	public static Class<?> getReturnTypeForMultipleResults(Method method) {
		return (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
	}

	public static Class<?> getReturnTypeForUniqueResult(Method method) {
		return method.getReturnType();
	}

}
