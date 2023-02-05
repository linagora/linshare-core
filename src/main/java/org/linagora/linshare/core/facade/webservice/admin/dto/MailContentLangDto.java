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
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailContentLang;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MailContentLang")
@Schema(name = "MailContentLang", description = "")
public class MailContentLangDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Language")
	private Language language;

	@Schema(description = "Readonly")
	private boolean readonly;

	@Schema(description = "MailContent")
	private String mailContent;

	@Schema(description = "MailConfig")
	private String mailConfig;

	@Schema(description = "MailContentType")
	private String mailContentType;

	@Schema(description = "MailContentName")
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
