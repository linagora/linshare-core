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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;
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

	@Schema(description = "DomainName")
	protected String domainName;

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

	@Schema(description = "ContactLists")
	protected List<ContactListDto> contactLists = Lists.newArrayList();

	public AccountDto() {
		super();
	}

	public AccountDto(Account a, boolean full) {
		this.uuid = a.getLsUuid();
		this.domain = a.getDomainId();
		this.domainName = a.getDomain().getLabel();
		this.creationDate = a.getCreationDate();
		this.modificationDate = a.getModificationDate();
		if (full) {
			this.locale = SupportedLanguage.fromLanguage(a.getMailLocale());
			this.externalMailLocale = a.getMailLocale();
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

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
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
