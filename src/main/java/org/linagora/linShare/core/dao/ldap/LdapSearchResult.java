/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.dao.ldap;

import java.util.List;

import org.springframework.ldap.control.PagedResult;
import org.springframework.ldap.control.PagedResultsCookie;


/**
 * paged result for ldap
 * if result > ldap.pageSize (see linshare.properties) result is truncated
 * 
 * @author slevesque
 * @param <T> type of one entry in the result
 * @deprecated not used anymore since domain implementation
 */
public class LdapSearchResult<T> extends PagedResult{

	
	private boolean isTruncated;
	
	public LdapSearchResult(List<T> resultList, PagedResultsCookie cookie) {
		super(resultList, cookie);
		isTruncated = false;
	}

	@SuppressWarnings("unchecked")
	public List<T> getResultList() {
		return (List<T>) super.getResultList();
	}
	
	public boolean isTruncated() {
		return isTruncated;
	}
	
    public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}
}
