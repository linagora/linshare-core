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
		guestProfileDto.setMailLocale(UserLanguage.from(user.getMailLocale()));
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

	@Schema(description = "User is restricted contact list", required = true)
	private Boolean restrictedContact;

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

	public Boolean isRestrictedContact() {
		return restrictedContact;
	}

	public void setRestrictedContact(Boolean restrictedContact) {
		this.restrictedContact = restrictedContact;
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
			.add("restrictedContact", restrictedContact)
			.add("author", author)
			.add("expirationDate", expirationDate)
			.toString();
	}

	@Override
	public boolean equalsElseLocale(GuestProfileDto dto) {
		if (dto.isRestricted() != isRestricted()) {
			return false;
		}
		if (dto.isRestrictedContact() != isRestrictedContact()) {
			return false;
		}
		if (!dto.getAuthor().getUuid().equals(getAuthor().getUuid())) {
			return false;
		}
		return commonEqualsElseLocale(dto);
	}
}
