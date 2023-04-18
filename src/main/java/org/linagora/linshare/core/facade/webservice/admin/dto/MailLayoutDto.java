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

import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainLightDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MailLayout")
@Schema(name = "MailLayout", description = "")
public class MailLayoutDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Domain")
	private DomainLightDto domain;

	@Schema(description = "Name")
	private String description;

	@Schema(description = "Layout")
	private String layout;

	@Schema(description = "Visible")
	private boolean visible;

	@Schema(description = "CreationDate")
	private Date creationDate;

	@Schema(description = "ModificationDate")
	private Date modificationDate;

	@Schema(description = "readonly")
	private boolean readonly;

	@Schema(description = "messagesFrench")
	private String messagesFrench;

	@Schema(description = "messagesEnglish")
	private String messagesEnglish;

	@Schema(description = "messagesRussian")
	private String messagesRussian;

	public MailLayoutDto() {
	}

	public MailLayoutDto(MailLayout ml) {
		this(ml, false);
	}

	public MailLayoutDto(MailLayout ml, boolean overrideReadonly) {
		this.uuid = ml.getUuid();
		this.domain = new DomainLightDto(ml.getDomain());
		this.description = ml.getDescription();
		this.layout = ml.getLayout();
		this.visible = ml.isVisible();
		this.creationDate = new Date(ml.getCreationDate().getTime());
		this.modificationDate = new Date(ml.getModificationDate().getTime());
		this.readonly = ml.isReadonly();
		if (overrideReadonly) {
			readonly = false;
		}
		this.messagesFrench = ml.getMessagesFrench();
		this.messagesEnglish = ml.getMessagesEnglish();
		this.messagesRussian = ml.getMessagesRussian();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
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
