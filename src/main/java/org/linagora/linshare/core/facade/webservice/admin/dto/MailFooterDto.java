/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
