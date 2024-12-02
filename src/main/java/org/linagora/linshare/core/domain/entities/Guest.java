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

	private Set<AccountContactLists> contactLists = Sets.newHashSet();

	private Set<Moderator> moderators = Sets.newHashSet();

	protected AbstractDomain guestSourceDomain;

	/** Default constructor for hibernate. */
	public Guest() {
		super();
		this.comment = "";
		this.canUpload = false;
		this.restricted = false;
		this.canCreateGuest = false;
		this.role = Role.SIMPLE;
	}

	/** Constructor for tests */
	public Guest(String firstName, String lastName, String mail, String password, Boolean canUpload, String comment) {
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

	public Guest(final String firstName, final String lastName, final String mail, final String lsUuid) {
		super(firstName, lastName, mail, lsUuid);
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

	public void addContactList(Collection<? extends AccountContactLists> c) {
		this.contactLists.addAll(c);
	}

	public Set<Moderator> getModerators() {
		return moderators;
	}

	public void setModerators(Set<Moderator> moderators) {
		this.moderators = moderators;
	}

	public void addModerator(Moderator moderator) {
		this.moderators.add(moderator);
	}

	public void removeModerator(Moderator moderator) {
		this.moderators.remove(moderator);
	}

	public AbstractDomain getGuestSourceDomain() {
		return guestSourceDomain;
	}

	public void setGuestSourceDomain(AbstractDomain guestSourceDomain) {
		this.guestSourceDomain = guestSourceDomain;
	}

	public Set<AccountContactLists> getRestrictedContactLists() {
		return contactLists;
	}

	public void setContactLists(Set<AccountContactLists> contactLists) {
		this.contactLists = contactLists;
	}

}
