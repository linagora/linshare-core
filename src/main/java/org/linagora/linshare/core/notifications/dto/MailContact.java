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
package org.linagora.linshare.core.notifications.dto;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.Recipient;

public class MailContact {

	protected String mail;

	protected String firstName;

	protected String lastName;

	public MailContact(String mail, String firstName, String lastName) {
		super();
		this.mail = mail;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public MailContact(String mail) {
		super();
		this.mail = mail;
	}

	public MailContact(Contact c) {
		this.mail = StringUtils.trimToNull(c.getMail());
		this.firstName = null;
		this.lastName = null;
	}

	public MailContact(User user) {
		this.mail = StringUtils.trimToNull(user.getMail());
		this.firstName = StringUtils.trimToNull(user.getFirstName());
		this.lastName = StringUtils.trimToNull(user.getLastName());
	}

	public MailContact(Moderator moderator) {
		User user = (User)moderator.getAccount();
		this.mail = StringUtils.trimToNull(user.getMail());
		this.firstName = StringUtils.trimToNull(user.getFirstName());
		this.lastName = StringUtils.trimToNull(user.getLastName());
	}

	public MailContact(Recipient recipient) {
		this.mail = recipient.getMail();
		this.firstName = recipient.getFirstName();
		this.lastName = recipient.getLastName();
	}

	public MailContact(Account account) {
		if (account instanceof User) {
			User user = (User) account;
			this.mail = StringUtils.trimToNull(user.getMail());
			this.firstName = StringUtils.trimToNull(user.getFirstName());
			this.lastName = StringUtils.trimToNull(user.getLastName());
		} else {
			this.mail = StringUtils.trimToNull(account.getMail());
		}
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
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

	@Override
	public String toString() {
		return "MailContact [mail=" + mail + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}
}
