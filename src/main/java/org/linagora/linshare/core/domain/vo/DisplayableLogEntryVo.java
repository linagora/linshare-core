/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
