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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailContent;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MailContent")
@Schema(name = "MailContent", description = "")
public class MailContentDto {

	@Schema(description = "Name")
	private String description;

	@Schema(description = "Domain")
	private String domain;
	@Schema(description = "DomainLabel")
	private String domainLabel;

	@Schema(description = "Visible")
	private boolean visible;

	@Schema(description = "MailContentType")
	private String mailContentType;

	@Schema(description = "Subject")
	private String subject;

	@Schema(description = "Body")
	private String body;

	@Schema(description = "CreationDate")
	private Date creationDate;

	@Schema(description = "ModificationDate")
	private Date modificationDate;

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "readonly")
	private boolean readonly;

	@Schema(description = "messagesFrench")
	private String messagesFrench;

	@Schema(description = "messagesEnglish")
	private String messagesEnglish;

	@Schema(description = "messagesRussian")
	private String messagesRussian;

	@Schema(description = "messagesVietnamese")
	private String messagesVietnamese;

	public MailContentDto() {
	}

	public MailContentDto(MailContent cont) {
		this(cont, false);
	}

	public MailContentDto(MailContent cont, boolean overrideReadonly) {
		this.uuid = cont.getUuid();
		this.domain = cont.getDomain().getUuid();
		this.domainLabel = cont.getDomain().getLabel();
		this.description = cont.getDescription();
		this.body = cont.getBody();
		this.subject = cont.getSubject();
		this.visible = cont.isVisible();
		this.mailContentType = MailContentType.fromInt(
				cont.getMailContentType()).toString();
		this.creationDate = new Date(cont.getCreationDate().getTime());
		this.modificationDate = new Date(cont.getModificationDate().getTime());
		this.readonly = cont.isReadonly();
		if (overrideReadonly) {
			readonly = false;
		}
		this.messagesFrench = cont.getMessagesFrench();
		this.messagesEnglish = cont.getMessagesEnglish();
		this.messagesRussian = cont.getMessagesRussian();
		this.messagesVietnamese = cont.getMessagesVietnamese();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomainLabel() {
		return domainLabel;
	}

	public void setDomainLabel(String domainLabel) {
		this.domainLabel = domainLabel;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getMailContentType() {
		return mailContentType;
	}

	public void setMailContentType(String mailContentType) {
		this.mailContentType = mailContentType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getMessagesFrench() {
		return messagesFrench;
	}

	public void setMessagesFrench(String messagesFrench) {
		this.messagesFrench = messagesFrench;
	}

	public String getMessagesEnglish() {
		return messagesEnglish;
	}

	public void setMessagesEnglish(String messagesEnglish) {
		this.messagesEnglish = messagesEnglish;
	}

	public String getMessagesRussian() {
		return messagesRussian;
	}

	public void setMessagesRussian(String messagesRussian) {
		this.messagesRussian = messagesRussian;
	}

	public String getMessagesVietnamese() {
		return messagesVietnamese;
	}

	public void setMessagesVietnamese(String messagesVietnamese) {
		this.messagesVietnamese = messagesVietnamese;
	}
}
