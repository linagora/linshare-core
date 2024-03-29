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

public class GuestProfileDtoTest {

	AuthorDto authorDto = AuthorDto.builder()
		.uuid("authorUuid")
		.firstName("authorFirstName")
		.lastName("authorLastName")
		.mail("authorMail")
		.build();

	@Test
	public void validationShouldThrowWhenUuidIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'uuid' must be set.");
	}

	@Test
	public void validationShouldThrowWhenFirstNameIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'firstName' must be set.");
	}

	@Test
	public void validationShouldThrowWhenFirstNameIsEmpty() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("");
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("'firstName' must be set.");
	}

	@Test
	public void validationShouldThrowWhenLastNameIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'lastName' must be set.");
	}

	@Test
	public void validationShouldThrowWhenLastNameIsEmpty() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("");
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("'lastName' must be set.");
	}

	@Test
	public void validationShouldThrowWhenMailIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("last name");
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'mail' must be set.");
	}

	@Test
	public void validationShouldThrowWhenMailIsEmpty() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("last name");
		guestProfileDto.setMail("");
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("'mail' must be set.");
	}

	@Test
	public void validationShouldThrowWhenMailLocaleIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("last name");
		guestProfileDto.setMail("mail");
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'locale' must be set.");
	}

	@Test
	public void validationShouldThrowWhenExternalMailLocaleIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("last name");
		guestProfileDto.setMail("mail");
		guestProfileDto.setMailLocale(UserLanguage.FRENCH);
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'externalMailLocale' must be set.");
	}

	@Test
	public void validationShouldThrowWhenPersonalSpaceEnabledIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("last name");
		guestProfileDto.setMail("mail");
		guestProfileDto.setMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setExternalMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setAccountType(AccountType.GUEST);
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'personalSpaceEnabled' must be set.");
	}

	@Test
	public void validationShouldThrowWhenAccountTypeIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("last name");
		guestProfileDto.setMail("mail");
		guestProfileDto.setMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setExternalMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setPersonalSpaceEnabled(true);
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'accountType' must be set.");
	}

	@Test
	public void validationShouldThrowWhenExpirationDateIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setRestricted(true);
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("last name");
		guestProfileDto.setMail("mail");
		guestProfileDto.setMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setExternalMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setPersonalSpaceEnabled(true);
		guestProfileDto.setAccountType(AccountType.GUEST);
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'expirationDate' must be set.");
	}

	@Test
	public void validationShouldThrowWhenRestrictedIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setAuthor(authorDto);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("last name");
		guestProfileDto.setMail("mail");
		guestProfileDto.setMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setExternalMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setPersonalSpaceEnabled(true);
		guestProfileDto.setAccountType(AccountType.GUEST);
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'restricted' must be set.");
	}

	@Test
	public void validationShouldThrowWhenAuthorIsNull() {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setExpirationDate(Date.from(Instant.now()));
		guestProfileDto.setRestricted(true);
		guestProfileDto.setUuid("uuid");
		guestProfileDto.setFirstName("first name");
		guestProfileDto.setLastName("last name");
		guestProfileDto.setMail("mail");
		guestProfileDto.setMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setExternalMailLocale(UserLanguage.FRENCH);
		guestProfileDto.setPersonalSpaceEnabled(true);
		guestProfileDto.setAccountType(AccountType.GUEST);
		assertThatThrownBy(() -> guestProfileDto.validation())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'author' must be set.");
	}

	@Test
	public void validationShouldWork() {
		Date expirationDate = Date.from(Instant.now());
		boolean restricted = true;
		String uuid = "uuid";
		String firstName = "first name";
		String lastName = "last name";
		String mail = "mail";
		boolean canUpload = true;
		Date creationDate = Date.from(Instant.now());
		Date modificationDate = Date.from(Instant.now());
		UserLanguage locale = UserLanguage.FRENCH;
		UserLanguage externalMailLocale = UserLanguage.FRENCH;
		AccountType accountType = AccountType.GUEST;
		GuestProfileDto dto = new GuestProfileDto();
		dto.setExpirationDate(expirationDate);
		dto.setRestricted(restricted);
		dto.setAuthor(authorDto);
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

		assertThat(dto.getExpirationDate()).isEqualTo(expirationDate);
		assertThat(dto.isRestricted()).isEqualTo(restricted);
		assertThat(dto.getAuthor()).isEqualTo(authorDto);
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
	}
}
