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

import java.io.Serializable;
import java.util.Calendar;

import org.linagora.linshare.core.domain.constants.LogActionV1;

/**
 * Abstract class for the log entry. Extended as FileLogEntry, UserLogEntry,
 * ShareLogEntry
 * 
 * @author ncharles
 * 
 */
public abstract class LogEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7185688904383875993L;

	private Long persistenceId;

	protected Calendar actionDate;

	protected String actorMail;

	protected String actorFirstname;

	protected String actorLastname;

	protected String actorDomain;

	protected LogActionV1 logAction;

	protected String description;

	/**
	 * This field must be protected because of hibernate which need absolutly a
	 * default constructor
	 */
	protected LogEntry() {
		this.persistenceId = null;
		this.actionDate = null;
		this.actorMail = null;
		this.actorDomain = null;
		this.actorFirstname = null;
		this.actorLastname = null;
		this.logAction = null;
		this.description = null;
	}

	public static boolean isUser(Account actor) {
		return actor.getAccountType().isUser();
	}

	public Calendar getActionDate() {
		return actionDate;
	}

	public String getActorMail() {
		return actorMail;
	}

	public String getActorFirstname() {
		return actorFirstname;
	}

	public String getActorLastname() {
		return actorLastname;
	}

	public String getActorDomain() {
		return actorDomain;
	}

	public LogActionV1 getLogAction() {
		return logAction;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long persistenceId) {
		this.persistenceId = persistenceId;
	}

	/**
	 * Format:
	 * 	USER_ACTIVITY:logAction:actorDomain:actorMail:description
	 */
	@Override
	public String toString() {
		return "USER_ACTIVITY:" + logAction + ":"
				+ (actorDomain == null ? "null domain" : actorDomain) + ":"
				+ actorMail + ":" + description;
	}

}
