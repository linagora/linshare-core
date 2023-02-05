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

/**
 * log entry for the users
 * 
 * @author ncharles
 *
 */
public class UserLogEntry extends LogEntry {

	private static final long serialVersionUID = -8388034589530890111L;

	protected final String targetMail;

	protected final String targetFirstname;

	protected final String targetLastname;

	protected final String targetDomain;

	protected final Calendar expirationDate;

	protected UserLogEntry() {
		super();
		this.targetMail = null;
		this.targetFirstname = null;
		this.targetLastname = null;
		this.targetDomain = null;
		this.expirationDate = null;
	}

	public String getTargetMail() {
		return targetMail;
	}

	public String getTargetFirstname() {
		return targetFirstname;
	}

	public String getTargetLastname() {
		return targetLastname;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public String getTargetDomain() {
		return targetDomain;
	}

}
