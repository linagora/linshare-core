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

import java.util.Date;

import org.linagora.linshare.core.domain.constants.UserLanguage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonDeserialize(builder = AbstractUserProfileDtoBuilder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "UserProfile",
	description = "User profile"
)
public abstract class AbstractUserProfileDto {

	@Schema(description = "User's uuid", required = true)
	protected final String uuid;

	@Schema(description = "User's first name", required = true)
	protected final String firstName;

	@Schema(description = "User's last name", required = true)
	protected final String lastName;

	@Schema(description = "User's mail", required = true)
	protected final String mail;

	@Schema(description = "User's creation date")
	protected final Date creationDate;

	@Schema(description = "User's modification date")
	protected final Date modificationDate;

	@Schema(description = "User's language", required = true)
	protected final UserLanguage locale;

	@Schema(description = "User personal space is enable", required = true)
	protected final boolean personalSpaceEnabled;

	protected AbstractUserProfileDto(String uuid, String firstName, String lastName, String mail, Date creationDate, Date modificationDate, UserLanguage locale, boolean personalSpaceEnabled) {
		this.uuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.locale = locale;
		this.personalSpaceEnabled = personalSpaceEnabled;
	}

	public String getUuid() {
		return uuid;
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

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public UserLanguage getLocale() {
		return locale;
	}

	public boolean isPersonalSpaceEnabled() {
		return personalSpaceEnabled;
	}

	public MoreObjects.ToStringHelper abstractToString() {
		return MoreObjects.toStringHelper(this)
			.add("firstName", firstName)
			.add("lastName", lastName)
			.add("mail", mail)
			.add("creationDate", creationDate)
			.add("modificationDate", modificationDate)
			.add("locale", locale)
			.add("personalSpaceEnabled", personalSpaceEnabled);
	}
}
