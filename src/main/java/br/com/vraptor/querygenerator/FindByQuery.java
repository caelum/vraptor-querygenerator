package br.com.vraptor.querygenerator;

import static br.com.vraptor.querygenerator.query.QueryReturnTypes.getReturnTypeFor;

import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;

import br.com.vraptor.querygenerator.query.QueryParser;

public class FindByQuery implements QueryParser {

	private final ParameterNameExtractors extractors;

	public FindByQuery(ParameterNameExtractors extractors) {
		this.extractors = extractors;
	}
	
	@Override
	public Object execute(Session session, Method method, Object[] args) {
		Map<String, Object> parameters = new CriteriaBuilder(method).create(args);

		Class<?> returnType = getReturnTypeFor(method);
		Criteria criteria = session.createCriteria(returnType);

		for (String parameterKey : parameters.keySet()) {

			Object value = parameters.get(parameterKey);

			NameAndConditionExtractor extractor = extractors.getExtractorFor(parameterKey);
			String parameter = extractor.getName(parameterKey);

			if (value instanceof CriteriaAccumulator) {
				CriteriaAccumulator subQueryParameters = (CriteriaAccumulator) value;
				Criteria subQuery = criteria.createCriteria(parameter);
				subQueryParameters.applyTo(subQuery, extractors);
			} else {
				criteria.add(extractor.getRestriction(parameterKey, value));
			}
		}

		return finalize(method, criteria);
	}

	@Override
	public boolean canHandle(String method) {
		return method.startsWith("findBy") || method.startsWith("findAllBy");
	}
	
	private Object finalize(Method method, Criteria c) {
		return method.getName().startsWith("findAllBy") ? c.list() : c.uniqueResult();
	}


}
