/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.domain.vo;

import java.io.Serializable;
import java.util.Date;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;

import com.google.common.base.Predicate;

/**
 * @author ncharles
 * 
 */
public class UserVo implements Serializable, Comparable<UserVo> {

	private static final long serialVersionUID = 3087781771112041575L;

	private final String login;
	private final String firstName;
	private final String lastName;
	private final String mail;
	private final AccountType userType;
	private Role role;
	private final boolean upload;
	private final boolean createGuest;
	private String ownerLogin = null;
	private Date expirationDate = null;
	private String comment;
	private String locale;
	private String externalMailLocale;
	private boolean restricted;
	private String domainIdentifier;

	public UserVo(String mail, String firstName, String lastName) {
		this.login = null;
		this.mail = mail;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userType = null;
		this.role = Role.SIMPLE;
		this.upload = true;
		this.createGuest = true;
		this.ownerLogin = "";
		this.restricted = false;
		this.domainIdentifier = null;
	}

	public UserVo(Account account) {
		this.login = account.getLsUuid();
		this.userType = account.getAccountType();
		this.role = account.getRole();
		this.locale = account.getLocale();
		this.externalMailLocale = account.getExternalMailLocale();
		this.restricted = false;

		if (userType.equals(AccountType.GUEST)
				|| userType.equals(AccountType.INTERNAL)
				|| userType.equals(AccountType.ROOT)) {
			User user = (User) account;
			this.firstName = user.getFirstName();
			this.lastName = user.getLastName();
			this.mail = user.getMail();
			this.upload = user.getCanUpload();
			this.createGuest = user.getCanCreateGuest();
			this.expirationDate = null;
			if (userType.equals(AccountType.GUEST)) {
				Guest guest = (Guest)user;
				this.expirationDate = guest.getExpirationDate();
				this.restricted = guest.isRestricted();
				this.comment = guest.getComment();
			}
		} else {
			this.firstName = null;
			this.lastName = null;
			this.mail = null;
			this.upload = false;
			this.createGuest = false;
		}
		if (account.getDomain() != null) {
			this.domainIdentifier = account.getDomain().getIdentifier();
		}
	}

	public UserVo(User user) {
		this.login = user.getLsUuid();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.mail = user.getMail();
		this.userType = user.getAccountType();
		this.role = user.getRole();
		this.upload = user.getCanUpload();
		this.createGuest = user.getCanCreateGuest();
		this.restricted = false;

		this.locale = user.getLocale();
		this.externalMailLocale = user.getExternalMailLocale();
		if (user instanceof Guest) {
			Guest guest = (Guest) user;
			ownerLogin = ((User) guest.getOwner()).getMail();
			expirationDate = (Date) guest.getExpirationDate().clone();
			this.comment = guest.getComment();
			this.restricted = guest.isRestricted();
		}
		if (user.getDomain() != null) {
			this.domainIdentifier = user.getDomain().getIdentifier();
		}
	}

	public UserVo(Guest user) {
		this.login = user.getLsUuid();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.mail = user.getMail();
		this.userType = user.getAccountType();
		this.role = user.getRole();
		this.upload = user.getCanUpload();
		this.createGuest = user.getCanCreateGuest();
		this.ownerLogin = ((User) user.getOwner()).getMail();
		this.expirationDate = (Date) user.getExpirationDate().clone();
		this.comment = user.getComment();
		this.locale = user.getLocale();
		this.externalMailLocale = user.getExternalMailLocale();
		this.restricted = user.isRestricted();
		if (user.getDomain() != null) {
			this.domainIdentifier = user.getDomain().getIdentifier();
		}
	}

	public UserVo(String login, String firstName, String lastName, String mail,
			AccountType userType) {
		super();
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.userType = userType;
		this.role = Role.SIMPLE;
		this.upload = true;
		this.createGuest = true;
		this.ownerLogin = "";
		this.restricted = false;
		this.domainIdentifier = null;
	}

	public UserVo(String login, String firstName, String lastName, String mail,
			Role role, AccountType userType) {
		super();
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.userType = userType;
		this.role = role;
		this.upload = true;
		this.createGuest = true;
		this.restricted = false;
		this.domainIdentifier = null;
	}

	public UserVo(String login, String firstName, String lastName, String mail,
			Role role, AccountType userType, String locale) {
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.userType = userType;
		this.role = role;
		this.upload = true;
		this.createGuest = true;
		this.locale = locale;
		this.restricted = false;
		this.domainIdentifier = null;
	}

	// We keep login for compatibility
	public String getLogin() {
		return login;
	}

	// the getter represent the real content of this variable.
	public String getLsUuid() {
		return login;
	}

	public String getOwnerLogin() {
		return ownerLogin;
	}

	public String getCompleteName() {
		return firstName + " " + lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMail() {
		return mail;
	}

	public AccountType getUserType() {
		return userType;
	}

	public Role getRole() {
		return role;
	}

	public boolean hasDelegationRole() {
		return Role.DELEGATION.equals(role);
	}

	public boolean hasUploadPropositionRole() {
		return Role.UPLOAD_PROPOSITION.equals(role);
	}

	public boolean isAdministrator() {
		return Role.ADMIN.equals(role) || isSuperAdmin();
	}

	public boolean isSuperAdmin() {
		return Role.SUPERADMIN.equals(role);
	}

	public boolean isGuest() {
		return AccountType.GUEST.equals(userType);
	}


	public boolean isUpload() {
		return upload;
	}

	public boolean isCreateGuest() {
		return createGuest;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getExternalMailLocale() {
		return externalMailLocale;
	}

	public void setExternalMailLocale(String externalMailLocale) {
		this.externalMailLocale = externalMailLocale;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public boolean isRestricted() {
		return restricted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((domainIdentifier == null) ? 0 : domainIdentifier.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result
				+ ((userType == null) ? 0 : userType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserVo))
			return false;
		UserVo other = (UserVo) obj;
		if (domainIdentifier == null) {
			if (other.domainIdentifier != null)
				return false;
		} else if (!domainIdentifier.equals(other.domainIdentifier))
			return false;
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
		if (role != other.role)
			return false;
		if (userType != other.userType)
			return false;
		return true;
	}

	public boolean businessEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserVo))
			return false;
		UserVo other = (UserVo) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}

	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("\nlogin : ").append(login);
		stringBuffer.append("\nfirst name : ").append(firstName);
		stringBuffer.append("\nlast name : ").append(lastName);
		stringBuffer.append("\nmail : ").append(mail);
		stringBuffer.append("\nuser type : ").append(userType);
		stringBuffer.append("\nrole : ").append(role);
		stringBuffer.append("\ndomain : ").append(domainIdentifier);
		return stringBuffer.toString();
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	public String getDomainIdentifier() {
		return domainIdentifier;
	}

	public void setDomainIdentifier(String domainIdentifier) {
		this.domainIdentifier = domainIdentifier;
	}

	@Override
	public int compareTo(UserVo o) {
		int res = this.lastName.compareToIgnoreCase(o.lastName);
		return res != 0 ? res : this.firstName.compareToIgnoreCase(o.firstName);
	}

	/*
	 * Filters
	 */
	public static Predicate<UserVo> equalTo(final String uuid) {
		return new Predicate<UserVo>() {
			@Override
			public boolean apply(UserVo input) {
				return input.getLsUuid().equals(uuid);
			}
		};
	}
}
