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
import org.linagora.linshare.core.domain.entities.MailFooterLang;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MailFooterLang")
@Schema(name = "MailFooterLang", description = "")
public class MailFooterLangDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Language")
	private Language language;

	@Schema(description = "Readonly")
	private boolean readonly;

	@Schema(description = "MailFooter")
	private String mailFooter;

	@Schema(description = "MailConfig")
	private String mailConfig;

	@Schema(description = "MailFooterName")
	private String mailFooterName;

	public MailFooterLangDto() {
	}

	public MailFooterLangDto(MailFooterLang footerLang) {
		this(footerLang, false);
	}

	public MailFooterLangDto(MailFooterLang footerLang, boolean overrideReadonly) {
		mailConfig = footerLang.getMailConfig().getUuid();
		language = Language.fromInt(footerLang.getLanguage());
		readonly = footerLang.isReadonly();
		if (overrideReadonly) {
			readonly = false;
		}
		uuid = footerLang.getUuid();
		mailFooter = footerLang.getMailFooter().getUuid();
		setMailFooterName(footerLang.getMailFooter().getDescription());
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

	public String getMailFooter() {
		return mailFooter;
	}

	public void setMailFooter(String mailFooter) {
		this.mailFooter = mailFooter;
	}

	public String getMailConfig() {
		return mailConfig;
	}

	public void setMailConfig(String mailConfig) {
		this.mailConfig = mailConfig;
	}

	public String getMailFooterName() {
		return mailFooterName;
	}

	public void setMailFooterName(String mailFooterName) {
		this.mailFooterName = mailFooterName;
	}
}