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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.Account;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Account")
@Schema(name = "Account", description = "")
public class AccountDto {

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "CreationDate")
	protected Date creationDate;

	@Schema(description = "ModificationDate")
	protected Date modificationDate;

	@Schema(description = "Locale")
	protected SupportedLanguage locale;

	@Schema(description = "ExternalMailLocale")
	protected Language externalMailLocale;

	@Schema(description = "Domain")
	protected String domain;

	@Schema(description = " 2FA uuid")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String secondFAUuid;

	@Schema(description = "If defined, it informs if current user is using Second Factor Authentication (2FA).")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected Boolean secondFAEnabled = null;

	@Schema(description = "If defined, it means that the current user must enable Second Factor Authentication (2FA) before using any api.")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected Boolean secondFARequired = null;

	@Schema(description = "Show if user access is locked")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected Boolean locked = null;

	public AccountDto() {
		super();
	}

	public AccountDto(Account a, boolean full) {
		this.uuid = a.getLsUuid();
		this.domain = a.getDomainId();
		this.creationDate = a.getCreationDate();
		this.modificationDate = a.getModificationDate();
		if (full) {
			this.locale = a.getLocale();
			this.externalMailLocale = a.getExternalMailLocale();
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public SupportedLanguage getLocale() {
		return locale;
	}

	public void setLocale(SupportedLanguage locale) {
		this.locale = locale;
	}

	public Language getExternalMailLocale() {
		return externalMailLocale;
	}

	public void setExternalMailLocale(Language extertalMailLocale) {
		this.externalMailLocale = extertalMailLocale;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Boolean getSecondFAEnabled() {
		return secondFAEnabled;
	}

	public void setSecondFAEnabled(Boolean secondFAEnabled) {
		this.secondFAEnabled = secondFAEnabled;
	}

	public Boolean getSecondFARequired() {
		return secondFARequired;
	}

	public void setSecondFARequired(Boolean secondFARequired) {
		this.secondFARequired = secondFARequired;
	}

	public String getSecondFAUuid() {
		return secondFAUuid;
	}

	public void setSecondFAUuid(String secondFAUuid) {
		this.secondFAUuid = secondFAUuid;
	}

	public Boolean isLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

}
