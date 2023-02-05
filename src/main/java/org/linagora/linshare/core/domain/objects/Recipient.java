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
package org.linagora.linshare.core.domain.objects;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;

public class Recipient {

	protected String uuid;

	protected String mail;

	protected String firstName;

	protected String lastName;

	protected AbstractDomain domain;

	protected String domainIdentifier;

	protected Language locale;

	public Recipient(String mail) {
		super();
		this.mail = mail;
	}

	public Recipient(String uuid, String mail, String firstName,
			String lastName, AbstractDomain domain, Language locale) {
		super();
		this.uuid = uuid;
		this.mail = mail;
		this.firstName = firstName;
		this.lastName = lastName;
		this.domain = domain;
		this.locale = locale;
	}

	public Recipient(User user) {
		super();
		this.uuid = user.getLsUuid();
		this.mail = user.getMail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.domain = user.getDomain();
		this.locale = user.getMailLocale();
	}

	public Recipient(UserDto userDto) {
		super();
		this.uuid = userDto.getUuid();
		this.mail = userDto.getMail();
		this.firstName = userDto.getFirstName();
		this.lastName = userDto.getLastName();
		this.domainIdentifier = userDto.getDomain();
	}

	public Recipient(GenericUserDto userDto) {
		super();
		this.uuid = userDto.getUuid();
		this.mail = userDto.getMail();
		this.firstName = userDto.getFirstName();
		this.lastName = userDto.getLastName();
		this.domainIdentifier = userDto.getDomain();
	}

	public Recipient(ContactListContact contact) {
		super();
		this.mail = contact.getMail();
		this.lastName = contact.getLastName();
		this.firstName = contact.getFirstName();
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

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public Language getLocale() {
		return locale;
	}

	public void setLocale(Language locale) {
		this.locale = locale;
	}


	@Override
	public String toString() {
		return "Recipient [uuid=" + uuid + ", mail=" + mail
				+ ", domainIdentifier=" + domainIdentifier + "]";
	}

}
