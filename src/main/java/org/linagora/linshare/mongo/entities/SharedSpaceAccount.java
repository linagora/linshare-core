/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
