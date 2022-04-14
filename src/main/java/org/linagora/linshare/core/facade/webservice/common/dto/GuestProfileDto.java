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

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.UserLanguage;
import org.linagora.linshare.core.domain.entities.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "GuestProfile")
@Schema(name = "GuestProfile", description = "Guest profile")
public class GuestProfileDto extends AbstractUserProfileDto<GuestProfileDto> {

	public static AbstractUserProfileDto from(User user, User author) {
		GuestProfileDto guestProfileDto = new GuestProfileDto();
		guestProfileDto.setUuid(user.getLsUuid());
		guestProfileDto.setFirstName(user.getFirstName());
		guestProfileDto.setLastName(user.getLastName());
		guestProfileDto.setMail(user.getMail());
		guestProfileDto.setCreationDate(user.getCreationDate());
		guestProfileDto.setModificationDate(user.getModificationDate());
		guestProfileDto.setLocale(UserLanguage.from(user.getLocale()));
		guestProfileDto.setExternalMailLocale(UserLanguage.from(user.getExternalMailLocale()));
		guestProfileDto.setPersonalSpaceEnabled(user.isCanUpload());
		guestProfileDto.setAccountType(user.getAccountType());
		guestProfileDto.setExpirationDate(user.getCreationDate());
		guestProfileDto.setRestricted(user.isRestricted());
		guestProfileDto.setAuthor(AuthorDto.from(author));
		guestProfileDto.validation();
		return guestProfileDto;
	}

	@Schema(description = "User's expiration date")
	private Date expirationDate;

	@Schema(description = "User is restricted", required = true)
	private Boolean restricted;

	@Schema(description = "User's author", required = true)
	private AuthorDto author;

	public GuestProfileDto() {
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public AuthorDto getAuthor() {
		return author;
	}

	public void setAuthor(AuthorDto author) {
		this.author = author;
	}

	@Override
	public void validation() {
		super.validation();
		Validate.notNull(expirationDate, "'expirationDate' must be set.");
		Validate.notNull(restricted, "'restricted' must be set.");
		Validate.notNull(author, "'author' must be set.");
	}

	@Override
	public String toString() {
		return abstractToString()
			.add("restricted", restricted)
			.add("author", author)
			.add("expirationDate", expirationDate)
			.toString();
	}

	@Override
	public boolean equalsElseLocale(GuestProfileDto dto) {
		if (dto.isRestricted() != isRestricted()) {
			return false;
		}
		if (!dto.getAuthor().getUuid().equals(getAuthor().getUuid())) {
			return false;
		}
		return commonEqualsElseLocale(dto);
	}
}
