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
package org.linagora.linshare.core.facade.webservice.delegation.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.User;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Account")
@Schema(name = "Account", description = "")
public class AccountDto {

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "Mail")
	private String mail;

	@Schema(description = "Name")
	private String name;

	@Schema(description = "Domain")
	protected String domain;

	@Schema(description = "Locale")
	protected SupportedLanguage locale;

	public AccountDto() {
		super();
	}

	public AccountDto(String uuid, String mail, String name, String domain,
			SupportedLanguage locale) {
		super();
		this.uuid = uuid;
		this.mail = mail;
		this.name = name;
		this.domain = domain;
		this.locale = locale;
	}

	public AccountDto(User u) {
		super();
		this.uuid = u.getLsUuid();
		this.mail = u.getMail();
		this.name = u.getLastName();
		this.domain = u.getDomainId();
		this.locale = SupportedLanguage.fromLanguage(u.getMailLocale());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public SupportedLanguage getLocale() {
		return locale;
	}

	public void setLocale(SupportedLanguage locale) {
		this.locale = locale;
	}

}
