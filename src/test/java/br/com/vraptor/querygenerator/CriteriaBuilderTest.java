package br.com.vraptor.querygenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CriteriaBuilderTest {

	@Test
	public void shoulgGetAllParameterNamesFromMethodName() throws Exception {
		Method method = PersonRepositoryHibernate.class.getMethod("findByNameAndAge", new Class[] { String.class,
				int.class });

		CriteriaBuilder builder = new CriteriaBuilder(method);
		List<String> parameterNames = builder.getParameterNames();

		List<String> expectedNames = Arrays.asList("Name", "Age");
		assertTrue(parameterNames.containsAll(expectedNames));
	}

	@Test
	public void test() throws Exception {
		Method method = PersonRepositoryHibernate.class.getMethod("findByNameAndAge", new Class[] { String.class,
				int.class });

		Object[] args = new Object[] { "renan", 26 };

		CriteriaBuilder builder = new CriteriaBuilder(method);
		Map<String, Object> map = builder.create(args);

		assertEquals("renan", map.get("name"));
		assertEquals(26, map.get("age"));
	}
}
