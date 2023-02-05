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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.UserLanguage;

public class UserProfileDtoTest {

	@Test
	public void validationThrowWhenUuidIsNull() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'uuid' must be set.");
	}

	@Test
	public void validationThrowWhenFirstNameIsNull() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'firstName' must be set.");
	}

	@Test
	public void validationThrowWhenFirstNameIsEmpty() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("");
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("'firstName' must be set.");
	}

	@Test
	public void validationThrowWhenLastNameIsNull() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("first name");
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'lastName' must be set.");
	}

	@Test
	public void validationThrowWhenLastNameIsEmpty() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("first name");
		userProfileDto.setLastName("");
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("'lastName' must be set.");
	}

	@Test
	public void validationThrowWhenMailIsNull() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("first name");
		userProfileDto.setLastName("last name");
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'mail' must be set.");
	}

	@Test
	public void validationThrowWhenMailIsEmpty() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("first name");
		userProfileDto.setLastName("last name");
		userProfileDto.setMail("");
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("'mail' must be set.");
	}

	@Test
	public void validationThrowWhenMailLocaleIsNull() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("first name");
		userProfileDto.setLastName("last name");
		userProfileDto.setMail("mail");
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'locale' must be set.");
	}

	@Test
	public void validationThrowWhenExternalMailLocaleIsNull() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("first name");
		userProfileDto.setLastName("last name");
		userProfileDto.setMail("mail");
		userProfileDto.setMailLocale(UserLanguage.FRENCH);
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'externalMailLocale' must be set.");
	}

	@Test
	public void validationThrowWhenPersonalSpaceEnabledIsNull() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("first name");
		userProfileDto.setLastName("last name");
		userProfileDto.setMail("mail");
		userProfileDto.setMailLocale(UserLanguage.FRENCH);
		userProfileDto.setExternalMailLocale(UserLanguage.FRENCH);
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'personalSpaceEnabled' must be set.");
	}

	@Test
	public void validationThrowWhenAccountTypeIsNull() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setCanCreateGuest(true);
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("first name");
		userProfileDto.setLastName("last name");
		userProfileDto.setMail("mail");
		userProfileDto.setMailLocale(UserLanguage.FRENCH);
		userProfileDto.setExternalMailLocale(UserLanguage.FRENCH);
		userProfileDto.setPersonalSpaceEnabled(true);
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'accountType' must be set.");
	}

	@Test
	public void validationThrowWhenCanCreateGuestIsNull() {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setUuid("uuid");
		userProfileDto.setFirstName("first name");
		userProfileDto.setLastName("last name");
		userProfileDto.setMail("mail");
		userProfileDto.setMailLocale(UserLanguage.FRENCH);
		userProfileDto.setExternalMailLocale(UserLanguage.FRENCH);
		userProfileDto.setPersonalSpaceEnabled(true);
		userProfileDto.setAccountType(AccountType.INTERNAL);
		assertThatThrownBy(() -> userProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'canCreateGuest' must be set.");
	}

	@Test
	public void validationWork() {
		boolean canCreateGuest = true;
		String uuid = "uuid";
		String firstName = "first name";
		String lastName = "last name";
		String mail = "mail";
		boolean canUpload = true;
		Date creationDate = Date.from(Instant.now());
		Date modificationDate = Date.from(Instant.now());
		UserLanguage locale = UserLanguage.FRENCH;
		UserLanguage externalMailLocale = UserLanguage.FRENCH;
		AccountType accountType = AccountType.INTERNAL;
		UserProfileDto dto = new UserProfileDto();
		dto.setCanCreateGuest(canCreateGuest);
		dto.setUuid(uuid);
		dto.setFirstName(firstName);
		dto.setLastName(lastName);
		dto.setMail(mail);
		dto.setMailLocale(locale);
		dto.setExternalMailLocale(externalMailLocale);
		dto.setPersonalSpaceEnabled(canUpload);
		dto.setAccountType(accountType);
		dto.setCreationDate(creationDate);
		dto.setModificationDate(modificationDate);

		assertThat(dto.getUuid()).isEqualTo(uuid);
		assertThat(dto.getFirstName()).isEqualTo(firstName);
		assertThat(dto.getLastName()).isEqualTo(lastName);
		assertThat(dto.getMail()).isEqualTo(mail);
		assertThat(dto.getCreationDate()).isEqualTo(creationDate);
		assertThat(dto.getModificationDate()).isEqualTo(modificationDate);
		assertThat(dto.getMailLocale()).isEqualTo(locale);
		assertThat(dto.getExternalMailLocale()).isEqualTo(externalMailLocale);
		assertThat(dto.isPersonalSpaceEnabled()).isEqualTo(canUpload);
		assertThat(dto.getAccountType()).isEqualTo(accountType);
		assertThat(dto.isCanCreateGuest()).isEqualTo(canCreateGuest);
	}
}
