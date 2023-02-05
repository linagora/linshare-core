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
package org.linagora.linshare.mongo.entities;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@XmlRootElement(name = "SharedSpaceAccount")
public class SharedSpaceAccount {

	protected String uuid;

	protected String name;

	protected String firstName;

	protected String lastName;

	protected String mail;

	protected AccountType accountType;

	@Schema(description = "The domain's uuid of the current account.", accessMode = AccessMode.READ_ONLY)
	protected String domainUuid;

	public SharedSpaceAccount() {
		super();
	}

	public SharedSpaceAccount(User user) {
		super();
		this.uuid = user.getLsUuid();
		this.name = user.getFullName();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.mail = user.getMail();
		this.accountType = user.getAccountType();
		this.domainUuid = user.getDomainId();
	}

	public SharedSpaceAccount(Account user) {
		super();
		this.uuid = user.getLsUuid();
		this.name = user.getFullName();
		this.mail = user.getMail();
		this.accountType = user.getAccountType();
		this.domainUuid = user.getDomainId();
	}

	/**
	 * Only used by FakeEmail builder.
	 * @param uuid
	 * @param name
	 * @param firstName
	 * @param lastName
	 * @param mail
	 */
	public SharedSpaceAccount(String uuid, String name, String firstName, String lastName, String mail) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
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

	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	@Override
	public String toString() {
		return "SharedSpaceAccount [uuid=" + uuid + ", name=" + name + ", firstName=" + firstName + ", lastName="
				+ lastName + ", mail=" + mail + ", accountType=" + accountType + ", domainUuid=" + domainUuid + "]";
	}
}
