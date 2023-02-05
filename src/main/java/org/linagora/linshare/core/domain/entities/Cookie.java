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
