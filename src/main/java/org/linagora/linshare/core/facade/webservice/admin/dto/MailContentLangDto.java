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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailContentLang;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "MailContentLang")
@ApiModel(value = "MailContentLang", description = "")
public class MailContentLangDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Language")
	private Language language;

	@ApiModelProperty(value = "Readonly")
	private boolean readonly;

	@ApiModelProperty(value = "MailContent")
	private String mailContent;

	@ApiModelProperty(value = "MailConfig")
	private String mailConfig;

	@ApiModelProperty(value = "MailContentType")
	private String mailContentType;

	@ApiModelProperty(value = "MailContentName")
	private String mailContentName;

	public MailContentLangDto() {
	}

	public MailContentLangDto(MailContentLang contentLang) {
		this(contentLang, false);
	}

	public MailContentLangDto(MailContentLang contentLang, boolean overrideReadonly) {
		mailConfig = contentLang.getMailConfig().getUuid();
		language = Language.fromInt(contentLang.getLanguage());
		readonly = contentLang.isReadonly();
		if (overrideReadonly) {
			readonly = false;
		}
		uuid = contentLang.getUuid();
		mailContent = contentLang.getMailContent().getUuid();
		mailContentType = MailContentType.fromInt(
				contentLang.getMailContentType()).toString();
		mailContentName = contentLang.getMailContent().getDescription();
	}

	public String getMailContentType() {
		return mailContentType;
	}

	public void setMailContentType(String mailContentType) {
		this.mailContentType = mailContentType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getMailConfig() {
		return mailConfig;
	}

	public void setMailConfig(String mailConfig) {
		this.mailConfig = mailConfig;
	}

	public String getMailContentName() {
		return mailContentName;
	}

	public void setMailContentName(String mailContentName) {
		this.mailContentName = mailContentName;
	}
}
