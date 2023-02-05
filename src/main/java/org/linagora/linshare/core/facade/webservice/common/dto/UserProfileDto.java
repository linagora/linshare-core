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

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.UserLanguage;
import org.linagora.linshare.core.domain.entities.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "UserProfile")
@Schema(name = "UserProfile", description = "User profile")
public class UserProfileDto extends AbstractUserProfileDto<UserProfileDto> {

	public static AbstractUserProfileDto from(User user) {
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setUuid(user.getLsUuid());
		userProfileDto.setFirstName(user.getFirstName());
		userProfileDto.setLastName(user.getLastName());
		userProfileDto.setMail(user.getMail());
		userProfileDto.setCreationDate(user.getCreationDate());
		userProfileDto.setModificationDate(user.getModificationDate());
		userProfileDto.setMailLocale(UserLanguage.from(user.getMailLocale()));
		userProfileDto.setExternalMailLocale(UserLanguage.from(user.getExternalMailLocale()));
		userProfileDto.setExternalMailLocale(UserLanguage.from(user.getExternalMailLocale()));
		userProfileDto.setPersonalSpaceEnabled(user.isCanUpload());
		userProfileDto.setAccountType(user.getAccountType());
		userProfileDto.setCanCreateGuest(user.isCanCreateGuest());
		userProfileDto.validation();
		return userProfileDto;
	}

	@Schema(description = "User has the ability to create guest", required = true)
	private Boolean canCreateGuest;

	public UserProfileDto() {
	}

	public boolean isCanCreateGuest() {
		return canCreateGuest;
	}

	public void setCanCreateGuest(boolean canCreateGuest) {
		this.canCreateGuest = canCreateGuest;
	}

	@Override
	public void validation() {
		super.validation();
		Validate.notNull(canCreateGuest, "'canCreateGuest' must be set.");
	}

	@Override
	public String toString() {
		return abstractToString()
			.add("canCreateGuest", canCreateGuest)
			.toString();
	}

	@Override
	public boolean equalsElseLocale(UserProfileDto dto) {
		if (dto.isCanCreateGuest() != isCanCreateGuest()) {
			return false;
		}
		return commonEqualsElseLocale(dto);
	}
}

