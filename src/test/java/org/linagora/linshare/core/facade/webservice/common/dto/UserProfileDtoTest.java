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
	public void buildShouldThrowWhenUuidIsNull() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(true)
				.build())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'uuid' must be set.");
	}

	@Test
	public void buildShouldThrowWhenFirstNameIsNull() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(true)
				.uuid("uuid")
				.build())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'firstName' must be set.");
	}

	@Test
	public void buildShouldThrowWhenFirstNameIsEmpty() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(true)
				.uuid("uuid")
				.firstName("")
				.build())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("'firstName' must be set.");
	}

	@Test
	public void buildShouldThrowWhenLastNameIsNull() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(true)
				.uuid("uuid")
				.firstName("first name")
				.build())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'lastName' must be set.");
	}

	@Test
	public void buildShouldThrowWhenLastNameIsEmpty() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(true)
				.uuid("uuid")
				.firstName("first name")
				.lastName("")
				.build())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("'lastName' must be set.");
	}

	@Test
	public void buildShouldThrowWhenMailIsNull() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(true)
				.uuid("uuid")
				.firstName("first name")
				.lastName("last name")
				.build())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'mail' must be set.");
	}

	@Test
	public void buildShouldThrowWhenMailIsEmpty() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(true)
				.uuid("uuid")
				.firstName("first name")
				.lastName("last name")
				.mail("")
				.build())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("'mail' must be set.");
	}

	@Test
	public void buildShouldThrowWhenLocaleIsNull() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(true)
				.uuid("uuid")
				.firstName("first name")
				.lastName("last name")
				.mail("mail")
				.build())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'locale' must be set.");
	}

	@Test
	public void buildShouldThrowWhenPersonalSpaceEnabledIsNull() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(true)
				.uuid("uuid")
				.firstName("first name")
				.lastName("last name")
				.mail("mail")
				.locale(UserLanguage.FRENCH)
				.build())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'personalSpaceEnabled' must be set.");
	}

	@Test
	public void buildShouldThrowWhenAccountTypeIsNull() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(null)
				.uuid("uuid")
				.firstName("first name")
				.lastName("last name")
				.mail("mail")
				.locale(UserLanguage.FRENCH)
				.personalSpaceEnabled(true)
				.build())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'accountType' must be set.");
	}

	@Test
	public void buildShouldThrowWhenCanCreateGuestIsNull() {
		assertThatThrownBy(() -> UserProfileDto.builder()
				.canCreateGuest(null)
				.uuid("uuid")
				.firstName("first name")
				.lastName("last name")
				.mail("mail")
				.locale(UserLanguage.FRENCH)
				.personalSpaceEnabled(true)
				.accountType(AccountType.INTERNAL)
				.build())
			.isInstanceOf(NullPointerException.class)
			.hasMessage("'canCreateGuest' must be set.");
	}

	@Test
	public void buildShouldWork() {
		boolean canCreateGuest = true;
		String uuid = "uuid";
		String firstName = "first name";
		String lastName = "last name";
		String mail = "mail";
		boolean canUpload = true;
		Date creationDate = Date.from(Instant.now());
		Date modificationDate = Date.from(Instant.now());
		UserLanguage locale = UserLanguage.FRENCH;
		AccountType accountType = AccountType.INTERNAL;
		UserProfileDto dto = UserProfileDto.builder()
			.canCreateGuest(canCreateGuest)
			.uuid(uuid)
			.firstName(firstName)
			.lastName(lastName)
			.mail(mail)
			.creationDate(creationDate)
			.modificationDate(modificationDate)
			.locale(locale)
			.personalSpaceEnabled(canUpload)
			.accountType(accountType)
			.build();

		assertThat(dto.getUuid()).isEqualTo(uuid);
		assertThat(dto.getFirstName()).isEqualTo(firstName);
		assertThat(dto.getLastName()).isEqualTo(lastName);
		assertThat(dto.getMail()).isEqualTo(mail);
		assertThat(dto.getCreationDate()).isEqualTo(creationDate);
		assertThat(dto.getModificationDate()).isEqualTo(modificationDate);
		assertThat(dto.getLocale()).isEqualTo(locale);
		assertThat(dto.isPersonalSpaceEnabled()).isEqualTo(canUpload);
		assertThat(dto.getAccountType()).isEqualTo(accountType);
		assertThat(dto.isCanCreateGuest()).isEqualTo(canCreateGuest);
	}
}
