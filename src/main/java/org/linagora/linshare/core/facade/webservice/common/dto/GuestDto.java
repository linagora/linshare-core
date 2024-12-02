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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDto.Author;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@XmlRootElement(name = "Guest")
@XmlAccessorType(XmlAccessType.FIELD)
@Schema(name = "Guest", description = "")
public class GuestDto extends AccountDto {

	@Schema(description = "FirstName")
	protected String firstName;

	@Schema(description = "LastName")
	protected String lastName;

	@Schema(description = "Mail")
	protected String mail;

	@Schema(description = "CanUpload")
	protected boolean canUpload;

	@Schema(description = "Restricted")
	protected boolean restricted;

	@Schema(description = "Comment")
	protected String comment;

	@Schema(description = "Expiration date")
	protected Date expirationDate;

	@Schema(description = "RestrictedContacts")
	protected List<GenericUserDto> restrictedContacts = Lists.newArrayList();

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "Owner")
	protected GenericUserDto owner;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "Author of the guest. Since v5.1")
	protected Author author;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "My moderator role for this guest.", accessMode = AccessMode.READ_ONLY)
	protected GuestModeratorRole myRole;
	private Set<AccountContactLists> accountContactLists;

	public GuestDto() {
		super();
	}

	protected GuestDto(Guest guest) {
		this.firstName = guest.getFirstName();
		this.lastName = guest.getLastName();
		this.mail = guest.getMail();
		this.restricted = guest.isRestricted();
		this.comment = guest.getComment();
		this.expirationDate = guest.getExpirationDate();
		this.canUpload = guest.isCanUpload();
		}

	protected GuestDto(Guest guest, boolean full, AccountMto author) {
		super(guest, full);
		this.firstName = guest.getFirstName();
		this.lastName = guest.getLastName();
		this.mail = guest.getMail();
		this.restricted = guest.isRestricted();
		this.comment = guest.getComment();
		this.expirationDate = guest.getExpirationDate();
		this.canUpload = guest.isCanUpload();
		if (author != null) {
			this.owner = new GenericUserDto(author);
			this.author = new Author(author);
		}
		if (this.restricted) {
			for (AllowedContact contact : guest.getRestrictedContacts()) {
				this.restrictedContacts.add(new GenericUserDto(contact
						.getContact()));
			}
		}
			for (AccountContactLists contact : guest.getRestrictedContactLists()) {
				this.contactLists.add(new ContactListDto(contact.getContactList()));
			}
	}

	public Guest toUserObject() {
		Guest guest = new Guest();
		guest.setLsUuid(getUuid());
		guest.setCanUpload(isCanUpload());
		guest.setComment(getComment());
		guest.setMailLocale(getExternalMailLocale());
		guest.setExpirationDate(getExpirationDate());
		guest.setFirstName(getFirstName());
		guest.setLastName(getLastName());
		guest.setMail(getMail());
		guest.setRestricted(isRestricted());
		Set<AccountContactLists> accountContactLists = (contactLists == null) ? Collections.emptySet() :
				contactLists.stream().map(contactListDto -> {
			AccountContactLists accountContactList = new AccountContactLists();
			accountContactList.setAccount(guest);
			accountContactList.setContactList(contactListDto.toObject());
			return accountContactList;
		})
				.collect(Collectors.toSet());
		return guest;
	}

	public static GuestDto getSimple(Guest user) {
		return new GuestDto(user, false, null);
	}

	public static UserDto getSimpleUserDto(User user) {
		return new UserDto(user, false);
	}

	public static GuestDto getFull(Guest user, AccountMto author) {
		return new GuestDto(user, true, author);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GuestDto other = (GuestDto) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		return true;
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

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public List<GenericUserDto> getRestrictedContacts() {
		return restrictedContacts;
	}

	public void setRestrictedContacts(List<GenericUserDto> restrictedContacts) {
		this.restrictedContacts = restrictedContacts;
	}

	public List<ContactListDto> getRestrictedContactList() {
		return contactLists;
	}

	public void setRestrictedContactList(List<ContactListDto> restrictedContactList) {
		this.contactLists = restrictedContactList;
	}

	public boolean isCanUpload() {
		return canUpload;
	}

	public void setCanUpload(boolean canUpload) {
		this.canUpload = canUpload;
	}

	public GenericUserDto getOwner() {
		return owner;
	}

	public void setOwner(GenericUserDto owner) {
		this.owner = owner;
	}

	public GuestModeratorRole getMyRole() {
		return myRole;
	}

	public void setMyRole(GuestModeratorRole myRole) {
		this.myRole = myRole;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	/*
	 * Transformers
	 */
	public static Function<Guest, GuestDto> toDto() {
		return new Function<Guest, GuestDto>() {
			@Override
			public GuestDto apply(Guest arg0) {
				return new GuestDto(arg0, false, null);
			}
		};
	}

	/**
	 * Helper
	 * 
	 * @param role
	 */
	public void setMyModeratorRole(ModeratorRole role) {
		switch (role) {
		case ADMIN:
			this.myRole = GuestModeratorRole.ADMIN;
			break;
		case SIMPLE:
			this.myRole = GuestModeratorRole.SIMPLE;
			break;
		default:
			throw new IllegalArgumentException("Doesn't match an existing role.");
		}
	}
}
