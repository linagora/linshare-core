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
package org.linagora.linShare.core.domain.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.linagora.linShare.core.domain.LogAction;

/**
 * Abstract class for the log entry.
 * Extended as FileLogEntry, UserLogEntry, ShareLogEntry 
 * @author ncharles
 *
 */
public abstract class LogEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7185688904383875993L;

	private Long persistenceId;

	private final Calendar actionDate;
	
	private final String actorMail;

	private final String actorFirstname;
	
	private final String actorLastname;
	
	private final String actorDomain;
	
	private final LogAction logAction;
	
	private String description;
	
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
	public LogEntry(Calendar actionDate, String actorMail,
			String actorFirstname, String actorLastname, String actorDomain,
			LogAction logAction, String description) {
		this.actionDate = (Calendar)actionDate.clone();
		this.actorMail = actorMail;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.actorDomain = actorDomain;
		this.logAction = logAction;
		this.description = description;
	}
	
	public LogEntry(String actorMail,
			String actorFirstname, String actorLastname, String actorDomain, 
			LogAction logAction, String description) {
		this.actionDate = new GregorianCalendar();
		this.actorMail = actorMail;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.actorDomain = actorDomain;
		this.logAction = logAction;
		this.description = description;
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
	public LogAction getLogAction() {
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
	
	
}
