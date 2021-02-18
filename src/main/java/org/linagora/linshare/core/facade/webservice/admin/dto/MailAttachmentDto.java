/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2021 LINAGORA
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
