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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "GenericUser")
@Schema(name = "GenericUser", description = "This class is a container to display or identify a user."
		+ "If the uuid is set, it will be used as the key for retrieve the user profile from the database."
		+ "If not, we will use the mail. The domain could be use to restrict the search to a particular domain."
		+ "This is usefull for a multi-domain LinShare instance.")
public class GenericUserDto {

	@Schema(description = "User uuid")
	protected String uuid = null;

	@Schema(description = "Domain")
	protected String domain = null;

	@Schema(description = "FirstName")
	private String firstName;

	@Schema(description = "LastName")
	private String lastName;

	@Schema(description = "Mail")
	private String mail = null;

	@Schema(description = "AccountType")
	private AccountType accountType = null;

	@Schema(description = "External")
	private Boolean external = null;

	public GenericUserDto() {
		super();
	}

	public GenericUserDto(Contact contact) {
		super();
		this.mail = contact.getMail();
		this.external = true;
	}

	public GenericUserDto(User u) {
		super();
		setUuid(u.getLsUuid());
		setMail(u.getMail());
		setFirstName(u.getFirstName());
		setLastName(u.getLastName());
		this.external = false;
		this.accountType = u.getAccountType();
		this.domain = u.getDomain().getUuid();
	}

	public GenericUserDto(AccountMto account) {
		this.uuid = account.getUuid();
		this.mail = account.getMail();
		this.firstName = account.getFirstName();
		this.lastName = account.getLastName();
		this.external = false;
		this.accountType = account.getAccountType();
		this.domain = account.getDomain().getUuid();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Boolean getExternal() {
		return external;
	}

	public void setExternal(Boolean external) {
		this.external = external;
	}
}
