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

package org.linagora.linshare.core.domain.objects;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailingListContact;
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
		this.locale = user.getExternalMailLocale();
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

	public Recipient(MailingListContact contact) {
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
