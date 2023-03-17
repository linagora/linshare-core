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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Account;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Account")
@Schema(name = "AccountV5Light", description = "")
public class AccountLightDto {

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "User's name")
	private String name;

	@Schema(description = "User's mail")
	private String email;

	@Schema(description = "Domain")
	protected DomainLightDto domain;

	public AccountLightDto() {
		super();
	}

	public AccountLightDto(Account a) {
		this.uuid = a.getLsUuid();
		this.name = a.getFullName();
		this.email = a.getMail();
		this.domain = new DomainLightDto(a.getDomain());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AccountLightDto that = (AccountLightDto) o;
		return Objects.equals(uuid, that.uuid)
						&& Objects.equals(name, that.name)
						&& Objects.equals(email, that.email)
						&& Objects.equals(domain, that.domain);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, name, email, domain);
	}

	@Override
	public String toString() {
		return "AccountLightDto{" +
				"uuid='" + uuid + '\'' +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", domain=" + domain +
				'}';
	}
}
