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
package org.linagora.linshare.mongo.entities.mto;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.Recipient;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountMto {

	protected String firstName;

	protected String lastName;

	protected String name;

	protected String mail;

	protected String uuid;

	protected Role role;

	protected AccountType accountType;

	protected DomainMto domain;

	public AccountMto() {
	}

	public AccountMto(Account account) {
		this(account, false);
	}

	public AccountMto(Contact contact) {
		this.mail = contact.getMail();
	}

	public AccountMto(Account account, boolean light) {
		this.name = account.getFullName();
		this.mail = account.getMail();
		this.uuid = account.getLsUuid();
		if (account.isUser()) {
			this.firstName = ((User)account).getFirstName();
			this.lastName = ((User)account).getLastName();
		}
		if (!light) {
			this.domain = new DomainMto(account.getDomain());
			this.role = account.getRole();
		}
		this.accountType = account.getAccountType();
	}

	public AccountMto(User user) {
		this(user, false);
	}

	public AccountMto(User user, boolean light) {
		this.uuid = user.getLsUuid();
		this.mail = user.getMail();
		this.name = user.getFullName();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		if (!light) {
			this.domain = new DomainMto(user.getDomain());
			this.role = user.getRole();
		}
		this.accountType = user.getAccountType();
	}

	public AccountMto(Recipient recipient) {
		this.uuid = recipient.getUuid();
		this.mail = recipient.getMail();
		this.name = recipient.getMail();
		this.domain = new DomainMto(recipient.getDomain());
	}

	public AccountMto(Guest guest) {
		this.uuid = guest.getLsUuid();
		this.domain = new DomainMto(guest.getDomain());
		this.mail = guest.getMail();
		this.name = guest.getFullName();
		this.firstName = guest.getFirstName();
		this.lastName = guest.getLastName();
		this.role = guest.getRole();
		this.accountType = guest.getAccountType();
	}

	public String getName() {
		return name;
	}

	public void setName(String lastName) {
		this.name = lastName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public DomainMto getDomain() {
		return domain;
	}

	public void setDomain(DomainMto domain) {
		this.domain = domain;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	@Override
	public String toString() {
		return "AccountMto [firstName=" + firstName + ", lastName=" + lastName + ", name=" + name + ", mail=" + mail
				+ ", uuid=" + uuid + ", role=" + role + ", accountType=" + accountType + ", domain=" + domain + "]";
	}
}
