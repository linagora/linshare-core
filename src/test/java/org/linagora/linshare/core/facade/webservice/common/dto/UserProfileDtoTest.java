/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2022 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
	public void validationThrowWhenLocaleIsNull() {
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
		userProfileDto.setLocale(UserLanguage.FRENCH);
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
		userProfileDto.setLocale(UserLanguage.FRENCH);
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
		userProfileDto.setLocale(UserLanguage.FRENCH);
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
		userProfileDto.setLocale(UserLanguage.FRENCH);
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
		dto.setLocale(locale);
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
		assertThat(dto.getLocale()).isEqualTo(locale);
		assertThat(dto.getExternalMailLocale()).isEqualTo(externalMailLocale);
		assertThat(dto.isPersonalSpaceEnabled()).isEqualTo(canUpload);
		assertThat(dto.getAccountType()).isEqualTo(accountType);
		assertThat(dto.isCanCreateGuest()).isEqualTo(canCreateGuest);
	}
}
