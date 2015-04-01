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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Guest")
@ApiModel(value = "Guest", description = "")
public class GuestDto extends AccountDto {

	@ApiModelProperty(value = "FirstName")
	protected String firstName;

	@ApiModelProperty(value = "LastName")
	protected String lastName;

	@ApiModelProperty(value = "Mail")
	protected String mail;

	@ApiModelProperty(value = "CanUpload")
	protected boolean canUpload;

	@ApiModelProperty(value = "Restricted")
	protected boolean restricted;

	@ApiModelProperty(value = "Comment")
	protected String comment;

	@ApiModelProperty(value = "Expiration date")
	protected Date expirationDate;

	@ApiModelProperty(value = "RestrictedContacts")
	protected List<GenericUserDto> restrictedContacts = Lists.newArrayList();

	@ApiModelProperty(value = "Owner")
	protected GenericUserDto owner;

	public GuestDto() {
		super();
	}

	protected GuestDto(Guest guest, boolean full) {
		super(guest, full);
		this.firstName = guest.getFirstName();
		this.lastName = guest.getLastName();
		this.mail = guest.getMail();
		this.restricted = guest.isRestricted();
		this.comment = guest.getComment();
		this.expirationDate = guest.getExpirationDate();
		this.canUpload = guest.getCanUpload();
		this.owner = new GenericUserDto((User) guest.getOwner());
		if (this.restricted) {
			for (AllowedContact contact : guest.getRestrictedContacts()) {
				this.restrictedContacts.add(new GenericUserDto(contact
						.getContact()));
			}
		}
	}

	public Guest toUserObject() {
		Guest guest = new Guest();
		guest.setLsUuid(getUuid());
		guest.setCanUpload(isCanUpload());
		guest.setComment(getComment());
		guest.setLocale(getLocale());
		guest.setExternalMailLocale(SupportedLanguage.toLanguage(getLocale()));
		guest.setExpirationDate(getExpirationDate());
		guest.setFirstName(getFirstName());
		guest.setLastName(getLastName());
		guest.setMail(getMail());
		guest.setRestricted(isRestricted());
		return guest;
	}

	public static GuestDto getSimple(Guest user) {
		return new GuestDto(user, false);
	}

	public static UserDto getSimpleUserDto(User user) {
		return new UserDto(user, false);
	}

	public static GuestDto getFull(Guest user) {
		return new GuestDto(user, true);
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

	/*
	 * Transformers
	 */
	public static Function<Guest, GuestDto> toDto() {
		return new Function<Guest, GuestDto>() {
			@Override
			public GuestDto apply(Guest arg0) {
				return new GuestDto(arg0, false);
			}
		};
	}
}
