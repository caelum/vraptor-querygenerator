package br.com.vraptor.querygenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaBuilder {

	private List<String> parameterNames = new ArrayList<>();

	private Method method;

	public CriteriaBuilder(Method method) {
		this.method = method;
		parameterNames = getParameterNames();
	}

	public Map<String, Object> create(Object[] args) {
		Map<String, Object> map = new HashMap<>();

		for (int i = 0; i < parameterNames.size(); i++) {
			map.put(parameterNames.get(i), args[i]);
		}

		return map;
	}

	public List<String> getParameterNames() {
		String name = method.getName();
		String nameWithoutPrefix = name.replaceFirst("find(All)?By", "");

		String[] parameters = nameWithoutPrefix.split("And");

		return toLowerCase(parameters);
	}

	private List<String> toLowerCase(String[] parameters) {
		List<String> names = new ArrayList<>();

		for (String parameter : parameters) {
			parameter = toLowerCase(parameter);
			names.add(parameter);
		}

		return names;
	}

	public static String toLowerCase(String parameter) {
		if (parameter.length() == 1) {
			return (parameter.toLowerCase());
		}
		return (Character.toLowerCase(parameter.charAt(0)) + parameter.substring(1));
	}
}