package org.linagora.linshare.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryParameter {

	private StringBuilder query; // the query
	
	private Map<String, Object> mapParam; // the parameters
	
	private String operator;
	
	private boolean where;
	

	public QueryParameter() {
		query = new StringBuilder();
		mapParam = new HashMap<String, Object>();
		operator = " where ";
		where = false;
	}

	
	public void appendToQuery(String aQuery) {
		addAndOperator();
		addQuery(aQuery);
	}
	
	
	public void addAndOperator() {
		addQuery(operator);
		if (!where) {
			where = true;
			operator = " and ";
		}
	}
	
	public void addQuery(String aQuery) {
		query.append(aQuery);
	}

	public StringBuilder getQuery() {
		return query;
	}

	public Object getParameter(String aKey) {
		return mapParam.get(aKey);
	}

	public Set<String> getKey() {
		return mapParam.keySet();
	}

	public void addParameter(String aKey, Object theParameter) {
		mapParam.put(aKey, theParameter);
	}
	
	@Override
	public String toString() {
		return query.toString();
	}
}
