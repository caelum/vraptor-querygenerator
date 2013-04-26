package br.com.vraptor.querygenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import br.com.vraptor.querygenerator.query.FindAllQuery;
import br.com.vraptor.querygenerator.query.FindByIdQuery;
import br.com.vraptor.querygenerator.query.QueryParser;

public class QueryExecutor {
	
	private final List<QueryParser> queries = new ArrayList<QueryParser>();
	private final Session session;
	
	public QueryExecutor(Session session) {
		queries.add(new FindByIdQuery());
		queries.add(new FindAllQuery());
		queries.add(new FindByQuery(new ParameterNameExtractors()));
		this.session = session;
	}

	public Object execute(Method method, Object[] args) {
		for (QueryParser query : queries) {
			if(query.canHandle(method.getName())){
				return query.execute(session, method, args);
			}
		}
		throw new RuntimeException("method not supported: " + method.getName());
	}
}
