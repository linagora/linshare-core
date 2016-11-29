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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "User")
public class UserDto extends AccountDto {

	@ApiModelProperty(value = "FirstName")
	private String firstName;

	@ApiModelProperty(value = "LastName")
	private String lastName;

	@ApiModelProperty(value = "Mail")
	private String mail;

	@ApiModelProperty(value = "Role")
	private String role;

	@ApiModelProperty(value = "CanUpload")
	private Boolean canUpload;

	@ApiModelProperty(value = "CanCreateGuest")
	private Boolean canCreateGuest;

	@ApiModelProperty(value = "AccountType")
	private String accountType;

	@ApiModelProperty(value = "Restricted")
	private Boolean restricted;

	@ApiModelProperty(value = "Comment")
	private String comment;

	@ApiModelProperty(value = "Expiration date")
	private Date expirationDate;

	@ApiModelProperty(value = "RestrictedContacts")
	private List<UserDto> restrictedContacts;

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	@ApiModelProperty(value = "user's quota uuid, only available in v2.")
	private String quotaUuid;

	public UserDto() {
		super();
	}

	protected UserDto(User u, boolean full) {
		super(u, full);
		this.firstName = u.getFirstName();
		this.lastName = u.getLastName();
		this.mail = u.getMail();
		this.role = u.getRole().toString();
		this.accountType = u.getAccountType().toString();
		if (full) {
			if (this.isGuest()) {
				Guest g = (Guest) u;
				this.restricted = g.isRestricted();
				this.comment = g.getComment();
				this.expirationDate = g.getExpirationDate();
				if (g.isRestricted()) {
					restrictedContacts = Lists.newArrayList();
					for (AllowedContact contact : g.getRestrictedContacts()) {
						this.restrictedContacts.add(getSimple(contact
								.getContact()));
					}
				}
			}
			this.canUpload = u.getCanUpload();
			this.canCreateGuest = u.getCanCreateGuest();
		}
	}

	protected UserDto(User u) {
		super(u, false);
		this.firstName = u.getFirstName();
		this.lastName = u.getLastName();
		this.mail = u.getMail();
	}

	public User toUserObject(boolean isGuest) {
		if (isGuest) {
			Guest guest = new Guest();
			guest.setLsUuid(getUuid());
			guest.setCanUpload(getCanUpload());
			guest.setComment(getComment());
			guest.setLocale(getLocale());
			guest.setExternalMailLocale(getExternalMailLocale());
			guest.setExpirationDate(getExpirationDate());
			guest.setFirstName(getFirstName());
			guest.setLastName(getLastName());
			guest.setMail(getMail());
			guest.setRestricted(isRestricted());
			return guest;
		} else {
			Internal internal = new Internal();
			internal.setLsUuid(getUuid());
			internal.setCanUpload(getCanUpload());
			internal.setLocale(getLocale());
			internal.setExternalMailLocale(getExternalMailLocale());
			internal.setFirstName(getFirstName());
			internal.setLastName(getLastName());
			internal.setRole(Role.valueOf(getRole()));
//			internal.setMail(getMail());
			return internal;
		}
	}

	public static UserDto getSimple(User user) {
		return new UserDto(user, false);
	}

	public static UserDto getFull(User user) {
		return new UserDto(user, true);
	}

	public static UserDto getCompletionUser(User user) {
		return new UserDto(user);
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getCanUpload() {
		return canUpload;
	}

	public void setCanUpload(Boolean canUpload) {
		this.canUpload = canUpload;
	}

	public Boolean getCanCreateGuest() {
		return canCreateGuest;
	}

	public void setCanCreateGuest(Boolean canCreateGuest) {
		this.canCreateGuest = canCreateGuest;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	@JsonIgnore
	public boolean isGuest() {
		return AccountType.valueOf(this.accountType) == AccountType.GUEST;
	}

	public Boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(Boolean restricted) {
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

	public List<UserDto> getRestrictedContacts() {
		return restrictedContacts;
	}

	public void setRestrictedContacts(List<UserDto> restrictedContacts) {
		this.restrictedContacts = restrictedContacts;
	}

	public String getQuotaUuid() {
		return quotaUuid;
	}

	public void setQuotaUuid(String quotaUuid) {
		this.quotaUuid = quotaUuid;
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
		UserDto other = (UserDto) obj;
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
}
