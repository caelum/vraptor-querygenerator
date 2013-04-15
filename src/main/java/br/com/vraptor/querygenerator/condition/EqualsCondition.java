package br.com.vraptor.querygenerator.condition;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import br.com.vraptor.querygenerator.NameAndConditionExtractor;

public class EqualsCondition implements NameAndConditionExtractor {

	@Override
	public boolean canHandle(String parameterKey) {
		return true;
	}

	@Override
	public String getName(String parameterKey) {
		return parameterKey;
	}

	@Override
	public Criterion getRestriction(String parameterKey, Object value) {
		return Restrictions.eq(getName(parameterKey), value);
	}

}
