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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MailAttachment")
@Schema(name = "MailAttachment", description = "")
public class MailAttachmentDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Enable")
	private Boolean enable;

	@Schema(description = "The enableForAll gives the choice to apply this attachment for all languages or not")
	private Boolean enableForAll;

	@Schema(description = "The choosen language for which the mail attachment will be applied")
	private Language language;

	@Schema(description = "Description")
	private String description;

	@Schema(description = "Name")
	private String name;

	@Schema(description = "The choosen mailConfig")
	private GenericLightEntity mailConfig;

	@Schema(description = "Content id of the mail attachment")
	private String cid;

	public MailAttachmentDto() {
		super();
	}

	public MailAttachmentDto(String uuid, Boolean enable, Boolean enableForAll, Language language,
			String description, String name, GenericLightEntity mailConfig, String cid) {
		super();
		this.uuid = uuid;
		this.enable = enable;
		this.enableForAll = enableForAll;
		this.language = language;
		this.description = description;
		this.name = name;
		this.mailConfig = mailConfig;
		this.cid = cid;
	}

	public MailAttachmentDto(MailAttachment attachment) {
		super();
		this.uuid = attachment.getUuid();
		this.enable = attachment.getEnable();
		this.enableForAll = attachment.getEnableForAll();
		this.language = attachment.getLanguage();
		this.description = attachment.getDescription();
		this.name = attachment.getName();
		this.cid = attachment.getCid();
	}

	public MailAttachment toObject() {
		MailAttachment mattachment = new MailAttachment();
		mattachment.setUuid(getUuid());
		mattachment.setEnable(getEnable());
		mattachment.setEnableForAll(getEnableForAll());
		mattachment.setLanguage(getLanguage());
		mattachment.setDescription(getDescription());
		mattachment.setName(getName());
		mattachment.setCid(getCid());
		return mattachment;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Boolean getEnableForAll() {
		return enableForAll;
	}

	public void setEnableForAll(Boolean enableForAll) {
		this.enableForAll = enableForAll;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GenericLightEntity getMailConfig() {
		return mailConfig;
	}

	public void setMailConfig(GenericLightEntity mailConfig) {
		this.mailConfig = mailConfig;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	/*
	 * Transformers
	 */
	public static Function<MailAttachment, MailAttachmentDto> toDto() {
		return mailAttachment -> new MailAttachmentDto(mailAttachment);
	}
}
