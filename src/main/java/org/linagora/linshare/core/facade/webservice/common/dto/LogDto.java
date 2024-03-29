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
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Log")
@Schema(name = "Log", description = "Criteria used to search the history")
public class LogDto {

	@Schema(description = "Date of the action")
	private Date actionDate;

	@Schema(description = "Mail of the actor")
	private String actorMail;

	@Schema(description = "First name of the actor")
	private String actorFirstname;

	@Schema(description = "Last name of the actor")
	private String actorLastname;

	@Schema(description = "The action")
	private LogActionV1 logAction;

	@Schema(description = "A description of the action")
	private String description;

	@Schema(description = "Mail of the target")
	private String targetMail;

	@Schema(description = "First name of the target")
	private String targetFirstname;

	@Schema(description = "Last name of the target")
	private String targetLastname;

	@Schema(description = "The file name")
	private String fileName;

	@Schema(description = "The file size")
	private Long fileSize;

	@Schema(description = "The file MIME type")
	private String fileType;

	@Schema(description = "The expiration date")
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
