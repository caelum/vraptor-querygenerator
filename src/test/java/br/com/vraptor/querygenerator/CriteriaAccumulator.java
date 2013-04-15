package br.com.vraptor.querygenerator;

import org.hibernate.Criteria;

public interface CriteriaAccumulator {

	public abstract void applyTo(Criteria sub);

}
