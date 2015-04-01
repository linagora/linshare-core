/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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
