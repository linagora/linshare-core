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
import org.linagora.linshare.core.domain.entities.MailFooterLang;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "MailFooterLang")
@ApiModel(value = "MailFooterLang", description = "")
public class MailFooterLangDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Language")
	private Language language;

	@ApiModelProperty(value = "Readonly")
	private boolean readonly;

	@ApiModelProperty(value = "MailFooter")
	private String mailFooter;

	@ApiModelProperty(value = "MailConfig")
	private String mailConfig;

	@ApiModelProperty(value = "MailFooterName")
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