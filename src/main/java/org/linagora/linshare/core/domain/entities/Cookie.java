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
package org.linagora.linshare.core.domain.entities;

import java.util.Calendar;

public class Cookie {
	/**
	 * Database persistence identifier
	 */
	private long persistenceId;
	
	/**
	 * Unique identifier -> token series
	 */
	private String identifier;
	
	/**
	 * Last update of cookie
	 */
	private Calendar lastUse;
	
	/**
	 * User login
	 */
	private String userName;
	
	/**
	 * Value of cookie -> token value
	 */
	private String value;

	public Cookie() {
		super();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long id) {
		if(null == id) this.persistenceId = 0;
		else this.persistenceId = id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setLastUse(Calendar lastUse) {
		this.lastUse = lastUse;
	}

	public Calendar getLastUse() {
		return lastUse;
	}
}
