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
package org.linagora.linShare.core.domain.vo;

import java.util.Calendar;

import org.linagora.linShare.core.domain.LogAction;

public abstract class LogEntryVo {
	private final Calendar actionDate;
	
	private final String actorMail;

	private final String actorFirstname;
	
	private final String actorLastname;
	
	private final String actorDomain;
	
	private final LogAction logAction;
	
	private String description;

	public LogEntryVo(Calendar actionDate, String actorMail,
			String actorFirstname, String actorLastname, String actorDomain,
			LogAction logAction, String description) {
		super();
		this.actionDate = (Calendar)actionDate.clone();
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
	
	
	
	
}
