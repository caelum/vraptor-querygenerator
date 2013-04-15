package br.com.vraptor.querygenerator.condition;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import br.com.vraptor.querygenerator.CriteriaBuilder;
import br.com.vraptor.querygenerator.NameAndConditionExtractor;

public class LikeCondition implements NameAndConditionExtractor {

	@Override
	public boolean canHandle(String parameterKey) {
		return parameterKey.startsWith("similar");
	}

	@Override
	public String getName(String parameterKey) {
		return CriteriaBuilder.toLowerCase(parameterKey.substring(7));
	}

	@Override
	public Criterion getRestriction(String parameterKey, Object value) {
		return Restrictions.like(getName(parameterKey), value);
	}

}
