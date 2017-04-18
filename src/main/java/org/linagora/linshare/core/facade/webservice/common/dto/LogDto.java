/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.LogActionV1;
import org.linagora.linshare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.ThreadLogEntry;
import org.linagora.linshare.core.domain.entities.UserLogEntry;

import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Log")
@ApiModel(value = "Log", description = "Criteria used to search the history")
public class LogDto {

	@ApiModelProperty(value = "Date of the action")
	private Date actionDate;

	@ApiModelProperty(value = "Mail of the actor")
	private String actorMail;

	@ApiModelProperty(value = "First name of the actor")
	private String actorFirstname;

	@ApiModelProperty(value = "Last name of the actor")
	private String actorLastname;

	@ApiModelProperty(value = "The action")
	private LogActionV1 logAction;

	@ApiModelProperty(value = "A description of the action")
	private String description;

	@ApiModelProperty(value = "Mail of the target")
	private String targetMail;

	@ApiModelProperty(value = "First name of the target")
	private String targetFirstname;

	@ApiModelProperty(value = "Last name of the target")
	private String targetLastname;

	@ApiModelProperty(value = "The file name")
	private String fileName;

	@ApiModelProperty(value = "The file size")
	private Long fileSize;

	@ApiModelProperty(value = "The file MIME type")
	private String fileType;

	@ApiModelProperty(value = "The expiration date")
	private Date expirationDate;

	public LogDto() {
	}

	public LogDto(final Date actionDate, String actorMail,
				  String actorFirstname, String actorLastname, LogActionV1 logAction,
				  String description, String targetMail, String targetFirstname,
				  String targetLastname, String fileName, Long fileSize,
				  String fileType, final Calendar expirationDate) {
		this.actionDate = actionDate;
		this.actorMail = actorMail;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.logAction = logAction;
		this.description = description;
		this.targetMail = Strings.nullToEmpty(targetMail);
		this.targetFirstname = Strings.nullToEmpty(targetFirstname);
		this.targetLastname = Strings.nullToEmpty(targetLastname);
		this.fileName = Strings.nullToEmpty(fileName);
		this.fileSize = fileSize;
		this.fileType = Strings.nullToEmpty(fileType);
		if (expirationDate != null)
			this.expirationDate = ((Calendar) expirationDate.clone()).getTime();
		else
			this.expirationDate = null;
	}

	public LogDto(final ShareLogEntry log) {
		this(log.getActionDate().getTime(), log.getActorMail(), log
			 .getActorFirstname(), log.getActorLastname(), log
			 .getLogAction(), log.getDescription(), log.getTargetMail(), log
			 .getTargetFirstname(), log.getTargetLastname(), log
			 .getFileName(), log.getFileSize(), log.getFileType(), log
			 .getExpirationDate());
	}

	public LogDto(final FileLogEntry log) {
		this(log.getActionDate().getTime(), log.getActorMail(), log
			 .getActorFirstname(), log.getActorLastname(), log
			 .getLogAction(), log.getDescription(), null, null, null, log
			 .getFileName(), log.getFileSize(), log.getFileType(), null);
	}

	public LogDto(final UserLogEntry log) {
		this(log.getActionDate().getTime(), log.getActorMail(), log
			 .getActorFirstname(), log.getActorLastname(), log
			 .getLogAction(), log.getDescription(), log.getTargetMail(), log
			 .getTargetFirstname(), log.getTargetLastname(), null, null,
			 null, log.getExpirationDate());
	}

	public LogDto(final AntivirusLogEntry log) {
		this(log.getActionDate().getTime(), log.getActorMail(), log
			 .getActorFirstname(), log.getActorLastname(), log
			 .getLogAction(), log.getDescription(), null, null, null, null,
			 null, null, null);
	}

	public LogDto(final ThreadLogEntry log) {
		this(log.getActionDate().getTime(), log.getActorMail(), log
			 .getActorFirstname(), log.getActorLastname(), log
			 .getLogAction(), log.getDescription(), null, log
			 .getThreadName(), log.getUuid(), log
			 .getFileName(), log.getFileSize(), log.getFileType(), null);
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

	public LogActionV1 getLogAction() {
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
