/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UserV5", description = "A linshare user")
public class UserDto {

	@Schema(name = "UserAuthor", description = "The author of a guest user.")
	public static class Author {

		@Schema(description = "Uuid")
		private String uuid;

		@Schema(description = "User's name")
		private String name;

		@Schema(description = "User's mail")
		private String email;

		@Schema(description = "User's domain")
		protected DomainLightDto domain;

		public Author() {
			super();
		}

		public Author(AccountMto accountMto) {
			super();
			this.uuid = accountMto.getUuid();
			this.name = accountMto.getName();
			this.email = accountMto.getMail();
			this.domain = new DomainLightDto(accountMto.getDomain());
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public DomainLightDto getDomain() {
			return domain;
		}

		public void setDomain(DomainLightDto domain) {
			this.domain = domain;
		}
	}

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "User's firstName")
	private String firstName;

	@Schema(description = "User's lastName")
	private String lastName;

	@Schema(description = "User's mail")
	private String mail;

	@Schema(description = "User's creationDate")
	protected Date creationDate;

	@Schema(description = "User's modificationDate")
	protected Date modificationDate;

	@Schema(description = "User's expiration date")
	private Date expirationDate;

	@Schema(description = "User's role")
	private Role role;

	@Schema(description = "CanUpload field shows if the user has the right to upload files in his personal space")
	private Boolean canUpload;

	@Schema(description = "CanCreateGuest field shows if the user has the right to create guest")
	private Boolean canCreateGuest;

	@Schema(description = "User's accountType")
	private AccountType accountType;

	@Schema(description = "Restricted field shows that the user is able to share files just with a restricted list of users")
	private Boolean restricted;

	@Schema(description = "Comment")
	private String comment;

	@Schema(description = "User's quota uuid (not available on findAll users)")
	private String quotaUuid;

	@Schema(description = "User's domain")
	protected DomainLightDto domain;

	@Schema(description = "The externalMailLocale field shows the language used for emails")
	protected Language externalMailLocale;

	@Schema(description = "2FA uuid")
	private String secondFAUuid;

	@Schema(description = "If defined, it informs if current user is using Second Factor Authentication (2FA)(ReadOnly).")
	private Boolean secondFAEnabled;

	@Schema(description = "Show if user's access is locked")
	private Boolean locked;

	@Schema(description = "Author of the guest.")
	private Author author;

	public UserDto() {
		super();
	}

	protected UserDto(User user) {
		this.uuid = user.getLsUuid();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.mail = user.getMail();
		this.role = user.getRole();
		this.accountType = user.getAccountType();
		this.creationDate = user.getCreationDate();
		this.modificationDate = user.getModificationDate();
		this.domain = new DomainLightDto(user.getDomain());
		this.locked = user.isLocked();
		if (this.isGuest()) {
			Guest g = (Guest) user;
			this.restricted = g.isRestricted();
			this.comment = g.getComment();
			this.expirationDate = g.getExpirationDate();
		} else {
			this.restricted = false;
		}
		this.canUpload = user.isCanUpload();
		this.canCreateGuest = user.isCanCreateGuest();
		this.externalMailLocale = user.getMailLocale();
		this.secondFAEnabled = user.isUsing2FA();
		this.secondFAUuid = user.getLsUuid();
	}

	public User toUserObject(boolean isGuest) {
		if (isGuest) {
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
			return guest;
		} else {
			Internal internal = new Internal();
			internal.setLsUuid(getUuid());
			internal.setCanUpload(isCanUpload());
			internal.setCanCreateGuest(isCanCreateGuest());
			internal.setMailLocale(getExternalMailLocale());
			internal.setFirstName(getFirstName());
			internal.setLastName(getLastName());
			internal.setRole(getRole());
			internal.setMail(getMail());
			return internal;
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getQuotaUuid() {
		return quotaUuid;
	}

	public void setQuotaUuid(String quotaUuid) {
		this.quotaUuid = quotaUuid;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	public Language getExternalMailLocale() {
		return externalMailLocale;
	}

	public void setExternalMailLocale(Language externalMailLocale) {
		this.externalMailLocale = externalMailLocale;
	}

	public String getSecondFAUuid() {
		return secondFAUuid;
	}

	public void setSecondFAUuid(String secondFAUuid) {
		this.secondFAUuid = secondFAUuid;
	}

	public Boolean isSecondFAEnabled() {
		return secondFAEnabled;
	}

	public void setSecondFAEnabled(Boolean secondFAEnabled) {
		this.secondFAEnabled = secondFAEnabled;
	}

	public Boolean isLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Boolean isCanUpload() {
		return canUpload;
	}

	public void setCanUpload(Boolean canUpload) {
		this.canUpload = canUpload;
	}

	public Boolean isCanCreateGuest() {
		return canCreateGuest;
	}

	public void setCanCreateGuest(Boolean canCreateGuest) {
		this.canCreateGuest = canCreateGuest;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
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

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	@JsonIgnore
	public boolean isGuest() {
		return this.accountType == AccountType.GUEST;
	}

	/*
	 * Transformers
	 */
	public static Function<User, UserDto> toDto() {
		return new Function<User, UserDto>() {
			@Override
			public UserDto apply(User arg0) {
				return new UserDto(arg0);
			}
		};
	}

	public static UserDto toDtoV5(User arg0, AccountMto accountMto) {
		UserDto dto = new UserDto(arg0);
		if (accountMto != null) {
			dto.setAuthor(new Author(accountMto));
		}
		return dto;
	}
}
