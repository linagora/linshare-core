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

import org.linagora.linshare.core.domain.entities.MailFooter;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MailFooter")
@Schema(name = "MailFooter", description = "")
public class MailFooterDto {

	@Schema(description = "Name")
	private String description;

	@Schema(description = "Domain")
	private String domain;

	@Schema(description = "DomainName")
	private String domainName;

	@Schema(description = "Visible")
	private boolean visible;

	@Schema(description = "Footer")
	private String footer;

	@Schema(description = "CreationDate")
	private Date creationDate;

	@Schema(description = "ModificationDate")
	private Date modificationDate;

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Plaintext")
	private boolean plaintext;

	@Schema(description = "readonly")
	private boolean readonly;

	@Schema(description = "messagesFrench")
	private String messagesFrench;

	@Schema(description = "messagesEnglish")
	private String messagesEnglish;

	@Schema(description = "messagesRussian")
	private String messagesRussian;

	public MailFooterDto() {
	}

	public MailFooterDto(MailFooter footer) {
		this(footer, false);
	}

	public MailFooterDto(MailFooter footer, boolean overrideReadonly) {
		this.uuid = footer.getUuid();
		this.domain = footer.getDomain().getUuid();
		this.domainName = footer.getDomain().getLabel();
		this.description = footer.getDescription();
		this.footer = footer.getFooter();
		this.visible = footer.getVisible();
		this.creationDate = new Date(footer.getCreationDate().getTime());
		this.modificationDate = new Date(footer.getModificationDate().getTime());
		this.readonly = footer.isReadonly();
		if (overrideReadonly) {
			readonly = false;
		}
		this.messagesFrench = footer.getMessagesFrench();
		this.messagesEnglish = footer.getMessagesEnglish();
		this.messagesRussian = footer.getMessagesRussian();
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public String getDescription() {
		return description;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setVisible(boolean value) {
		this.visible = value;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setFooter(String value) {
		this.footer = value;
	}

	public String getFooter() {
		return footer;
	}

	public void setCreationDate(Date value) {
		this.creationDate = value;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setModificationDate(Date value) {
		this.modificationDate = value;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setUuid(String value) {
		this.uuid = value;
	}

	public String getUuid() {
		return uuid;
	}

	public void setPlaintext(boolean value) {
		this.plaintext = value;
	}

	public boolean isPlaintext() {
		return plaintext;
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
}
