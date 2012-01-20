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

import java.util.Calendar;

import org.linagora.linShare.core.domain.constants.LogAction;

/**
 * log entry for the users
 * @author ncharles
 *
 */
public class UserLogEntry extends LogEntry {
	
	private static final long serialVersionUID = -8388034589530890111L;

	private final String targetMail;

	private final String targetFirstname;
	
	private final String targetLastname;
	
	private final String targetDomain;
	
	private final Calendar expirationDate;

	protected UserLogEntry() {
		super();
		this.targetMail = null;
		this.targetFirstname = null;
		this.targetLastname = null;
		this.targetDomain = null;
		this.expirationDate = null;
	}
	public UserLogEntry(Calendar actionDate, String actorMail,
			String actorFirstname, String actorLastname, String actorDomain,
			LogAction logAction, String description,
			String targetMail, String targetFirstname,
			String targetLastname, String targetDomain,
			Calendar expirationDate) {
		super(actionDate, actorMail,
				actorFirstname, actorLastname, actorDomain,
				logAction, description);
		this.targetMail = targetMail;
		this.targetFirstname = targetFirstname;
		this.targetLastname = targetLastname;
		this.targetDomain = targetDomain;
		this.expirationDate = expirationDate;
	}
	
	public UserLogEntry(String actorMail,
			String actorFirstname, String actorLastname, String actorDomain,
			LogAction logAction, String description,
			String targetMail, String targetFirstname,
			String targetLastname, String targetDomain,
			Calendar expirationDate) {
		super(actorMail,
				actorFirstname, actorLastname, actorDomain,
				logAction, description);
		this.targetMail = targetMail;
		this.targetFirstname = targetFirstname;
		this.targetLastname = targetLastname;
		this.targetDomain = targetDomain;
		this.expirationDate = expirationDate;
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
