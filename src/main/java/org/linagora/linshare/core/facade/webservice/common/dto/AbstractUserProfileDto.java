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

import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.UserLanguage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "UserProfile",
	description = "A UserProfile",
	discriminatorProperty = "accountType",
	discriminatorMapping = {
		@DiscriminatorMapping(value = "INTERNAL", schema = UserProfileDto.class),
		@DiscriminatorMapping(value = "GUEST", schema = GuestProfileDto.class)
	}
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "accountType", visible = true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = UserProfileDto.class, name="INTERNAL"),
	@JsonSubTypes.Type(value = GuestProfileDto.class, name="GUEST")
})
public abstract class AbstractUserProfileDto<T extends AbstractUserProfileDto> {

	@Schema(description = "User's uuid", required = true)
	protected String uuid;

	@Schema(description = "User's first name", required = true)
	protected String firstName;

	@Schema(description = "User's last name", required = true)
	protected String lastName;

	@Schema(description = "User's mail", required = true)
	protected String mail;

	@Schema(description = "User's creation date")
	protected Date creationDate;

	@Schema(description = "User's modification date")
	protected Date modificationDate;

	@Schema(description = "User's language used for mail notification", required = true)
	protected UserLanguage mailLocale;

	@Schema(description = "User's language used for external mail notification", required = true)
	protected UserLanguage externalMailLocale;

	@Schema(description = "User personal space is enable", required = true)
	protected Boolean personalSpaceEnabled;

	@Schema(description = "User's type", required = true)
	protected AccountType accountType;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public UserLanguage getMailLocale() {
		return mailLocale;
	}

	public void setMailLocale(UserLanguage mailLocale) {
		this.mailLocale = mailLocale;
	}

	public UserLanguage getExternalMailLocale() {
		return externalMailLocale;
	}

	public void setExternalMailLocale(UserLanguage externalMailLocale) {
		this.externalMailLocale = externalMailLocale;
	}

	public boolean isPersonalSpaceEnabled() {
		return personalSpaceEnabled;
	}

	public void setPersonalSpaceEnabled(boolean personalSpaceEnabled) {
		this.personalSpaceEnabled = personalSpaceEnabled;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public MoreObjects.ToStringHelper abstractToString() {
		return MoreObjects.toStringHelper(this)
			.add("firstName", firstName)
			.add("lastName", lastName)
			.add("mail", mail)
			.add("creationDate", creationDate)
			.add("modificationDate", modificationDate)
			.add("locale", mailLocale)
			.add("externalMailLocale", externalMailLocale)
			.add("personalSpaceEnabled", personalSpaceEnabled)
			.add("accountType", accountType);
	}

	public void validation() {
		Validate.notBlank(uuid, "'uuid' must be set.");
		Validate.notBlank(firstName, "'firstName' must be set.");
		Validate.notBlank(lastName, "'lastName' must be set.");
		Validate.notBlank(mail, "'mail' must be set.");
		Validate.notNull(mailLocale, "'locale' must be set.");
		Validate.notNull(externalMailLocale, "'externalMailLocale' must be set.");
		Validate.notNull(personalSpaceEnabled, "'personalSpaceEnabled' must be set.");
		Validate.notNull(accountType, "'accountType' must be set.");
	}

	public abstract boolean equalsElseLocale(T dto);

	public boolean commonEqualsElseLocale(AbstractUserProfileDto dto) {
		if (!dto.getFirstName().equals(getFirstName())) {
			return false;
		}
		if (!dto.getLastName().equals(getLastName())) {
			return false;
		}
		if (!dto.getMail().equals(getMail())) {
			return false;
		}
		if (dto.isPersonalSpaceEnabled() != isPersonalSpaceEnabled()) {
			return false;
		}
		if (!dto.getAccountType().equals(getAccountType())) {
			return false;
		}
		return true;
	}
}
