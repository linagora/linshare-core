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
package org.linagora.linshare.webservice.adminv5;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.RestrictedContactDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDtoQuotaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ModeratorRoleEnum;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;

public interface UserRestService {

	Response findAll(List<String> domainsUuids, String domainUuid, String sortOrder, String sortField, String mail,
					 String firstName, String lastName, Boolean restricted, Boolean canCreateGuest, Boolean canUpload, String role,
					 String type, String moderatorRole, Integer greaterThan, Integer lowerThan, Integer pageNumber, Integer pageSize) throws BusinessException;

	Set<UserDto> autocomplete(String pattern, String accountType, String domain) throws BusinessException;

	UserDto find(String uuid) throws BusinessException;

	UserDto update(UserDto userDto, String uuid) throws BusinessException;

	UserDto delete(UserDto userDto, String uuid) throws BusinessException;

	UserDto create(UserDto userDto) throws BusinessException;

	List<RestrictedContactDto> findAllRestrictedContacts(String userUuid, String mail, String firstName,
			String lastName) throws BusinessException;

	RestrictedContactDto findRestrictedContact(String ownerUuid, String userUuid) throws BusinessException;

	RestrictedContactDto createRestrictedContact(String ownerUuid, RestrictedContactDto restrictedContactDto) throws BusinessException;

	RestrictedContactDto deleteRestrictedContact(String ownerUuid, RestrictedContactDto restrictedContactDto,
			String restrictedContactUuid) throws BusinessException;

	UserDtoQuotaDto findUserQuota(String accountUuid, String quotaUuid) throws BusinessException;

	SecondFactorDto find2FA(String uuid, String secondfaUuid) throws BusinessException;

	SecondFactorDto delete2FA(String uuid, String secondfaUuid, SecondFactorDto dto) throws BusinessException;

	UserDtoQuotaDto updateUserQuota(String userUuid, String quotaUuid, UserDtoQuotaDto dto) throws BusinessException;

	List<GuestDto> findAllUserGuests(String uuid, ModeratorRoleEnum role, String pattern) throws BusinessException;
}
