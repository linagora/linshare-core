package org.linagora.linshare.webservice.dto;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.UserLogEntry;

import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Log")
@ApiModel(value = "Log", description = "Criteria used to search the history")
public class LogDto {

	@XmlElement(name = "actionDate")
	@ApiModelProperty(value = "Date of the action")
	private final Date actionDate;

	@XmlElement(name = "actorMail")
	@ApiModelProperty(value = "Mail of the actor")
	private final String actorMail;

	@XmlElement(name = "actorFirstname")
	@ApiModelProperty(value = "First name of the actor")
	private final String actorFirstname;

	@XmlElement(name = "actorLastname")
	@ApiModelProperty(value = "Last name of the actor")
	private final String actorLastname;

	@XmlElement(name = "logAction")
	@ApiModelProperty(value = "The action")
	private final LogAction logAction;

	@XmlElement(name = "description")
	@ApiModelProperty(value = "A description of the action")
	private final String description;

	@XmlElement(name = "targetMail")
	@ApiModelProperty(value = "Mail of the target")
	private final String targetMail;

	@XmlElement(name = "targetFirstname")
	@ApiModelProperty(value = "First name of the target")
	private final String targetFirstname;

	@XmlElement(name = "targetLastname")
	@ApiModelProperty(value = "Last name of the target")
	private final String targetLastname;

	@XmlElement(name = "fileName")
	@ApiModelProperty(value = "The file name")
	private final String fileName;

	@XmlElement(name = "fileSize")
	@ApiModelProperty(value = "The file size")
	private final Long fileSize;

	@XmlElement(name = "fileType")
	@ApiModelProperty(value = "The file MIME type")
	private final String fileType;

	@XmlElement(name = "expirationDate")
	@ApiModelProperty(value = "The expiration date")
	private final Date expirationDate;

	public LogDto(final Date actionDate, String actorMail,
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
