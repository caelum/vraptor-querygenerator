package br.com.vraptor.querygenerator;

import org.hibernate.criterion.Criterion;

public interface NameAndConditionExtractor {

	boolean canHandle(String parameterKey);

	String getName(String parameterKey);

	Criterion getRestriction(String parameterKey, Object value);

}
