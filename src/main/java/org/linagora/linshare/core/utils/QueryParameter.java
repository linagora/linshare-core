/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
