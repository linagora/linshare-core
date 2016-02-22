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
package org.linagora.linshare.core.domain.entities;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;

import com.google.common.collect.Sets;

/** Guest is a user that is not registered in LDAP server.
 */
public class Guest extends User {

	private boolean restricted;

	private String comment;

	private Date expirationDate;

	private Set<AllowedContact> contacts = Sets.newHashSet();

	/** Default constructor for hibernate. */
	public Guest() {
		super();
		this.comment = "";
		this.canUpload = false;
		this.restricted = false;
		this.canCreateGuest = false;
		this.role = Role.SIMPLE;
	}

	/** Constructor for tests*/
	public Guest(String firstName, String lastName, String mail,
			String password, Boolean canUpload, String comment) {
		super(firstName, lastName, mail);
		this.canUpload = canUpload;
		this.password = password;
		this.comment = comment;
		this.restricted = false;
		this.canCreateGuest = false;
		this.role = Role.SIMPLE;
	}

	public Guest(String firstName, String lastName, String mail) {
		super(firstName, lastName, mail);
		this.restricted = false;
		this.comment = "";
		this.canCreateGuest = false;
		this.role = Role.SIMPLE;
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.GUEST;
	}

	@Override
	public String getAccountRepresentation() {
		return this.firstName + " " + this.lastName + "(" + lsUuid + ")";
	}

	public void setComment(String value) {
		this.comment = value;
	}

	public String getComment() {
		return comment;
	}

	public void setRestricted(boolean value) {
		this.restricted = value;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setExpirationDate(Date value) {
		this.expirationDate = value;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public Set<AllowedContact> getRestrictedContacts() {
		return contacts;
	}

	public void addContacts(Collection<? extends AllowedContact> c) {
		this.contacts.addAll(c);
	}
}
