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
package org.linagora.linshare.core.domain.vo;

import java.util.Calendar;
import java.util.Date;

import org.linagora.linshare.core.domain.constants.LogAction;

/**
 * Class to easily display a LogEntry, whichever type it is
 * 
 * @author ncharles
 *
 */
public class DisplayableLogEntryVo {

	private final Date actionDate;
	
	private final String actorMail;

	private final String actorFirstname;
	
	private final String actorLastname;
	
	private final LogAction logAction;
	
	private final String description;
	
	private final String targetMail;

	private final String targetFirstname;
	
	private final String targetLastname;
	
	private final String fileName;
	
	private final Long fileSize;
	
	private final String fileType;

	private final Date expirationDate;
	
	public DisplayableLogEntryVo(final Date actionDate, String actorMail,
			String actorFirstname, String actorLastname, LogAction logAction,
			String description, String targetMail, String targetFirstname,
			String targetLastname, String fileName, Long fileSize,
			String fileType, final Calendar expirationDate) {
		this.actionDate = actionDate;
		this.actorMail = actorMail;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.logAction = logAction;
		this.description = description;
		this.targetMail = targetMail;
		this.targetFirstname = targetFirstname;
		this.targetLastname = targetLastname;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileType = fileType;
		if (expirationDate != null)
			this.expirationDate = ((Calendar)expirationDate.clone()).getTime();
		else
			this.expirationDate = null;
	}

	public DisplayableLogEntryVo(final Date actionDate, String actorMail,
			String actorFirstname, String actorLastname, LogAction logAction,
			String description) {
		this.actionDate = actionDate;
		this.actorMail = actorMail;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.logAction = logAction;
		this.description = description;
		this.targetMail = "";
		this.targetFirstname = "";
		this.targetLastname = "";
		this.fileName = "";
		this.fileSize = null;
		this.fileType = "";
		this.expirationDate = null;
	}
	
	public DisplayableLogEntryVo(final Date actionDate, String actorMail,
			String actorFirstname, String actorLastname, LogAction logAction,
			String description, String targetMail, String targetFirstname,
			String targetLastname, final Calendar expirationDate) {
		this.actionDate = actionDate;
		this.actorMail = actorMail;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.logAction = logAction;
		this.description = description;
		this.targetMail = targetMail;
		this.targetFirstname = targetFirstname;
		this.targetLastname = targetLastname;
		this.fileName = "";
		this.fileSize = null;
		this.fileType = "";
		if (expirationDate != null)
			this.expirationDate = ((Calendar)expirationDate.clone()).getTime();
		else
			this.expirationDate = null;
	}
	
	public DisplayableLogEntryVo(final Date actionDate, String actorMail,
			String actorFirstname, String actorLastname, LogAction logAction,
			String description, String fileName, Long fileSize,
			String fileType) {
		this.actionDate = actionDate;
		this.actorMail = actorMail;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.logAction = logAction;
		this.description = description;
		this.targetMail = "";
		this.targetFirstname = "";
		this.targetLastname = "";
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileType = fileType;
		this.expirationDate = null;
	}
	
	public Date getActionDate() {
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

	public LogAction getLogAction() {
		return logAction;
	}

	public String getDescription() {
		return description;
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

	public String getFileName() {
		return fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}
	
	
	
	
	
	
}
