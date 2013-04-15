package br.com.vraptor.querygenerator;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.hibernate.criterion.SimpleExpression;

class RestrictionEq extends TypeSafeMatcher<SimpleExpression> {

	private String name;
	private Object value;

	public RestrictionEq(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void describeTo(Description d) {
		d.appendText(name + "=" + value);
	}

	@Override
	protected boolean matchesSafely(SimpleExpression expr) {
		return expr.getPropertyName().equals(name) && expr.getValue().equals(value);
	}

}
