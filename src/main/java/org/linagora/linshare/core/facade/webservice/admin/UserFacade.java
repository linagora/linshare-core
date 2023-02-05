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
package org.linagora.linshare.core.facade.webservice.admin;


import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.InconsistentSearchDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;
import org.linagora.linshare.utils.Version;

public interface UserFacade extends AdminGenericFacade {

	List<UserDto> search(UserSearchDto userSearchDto) throws BusinessException;

	Set<UserDto> searchInternals(String pattern) throws BusinessException;

	Set<UserDto> searchGuests(String pattern) throws BusinessException;

	UserDto update(UserDto userDto, Version version) throws BusinessException;

	UserDto delete(UserDto userDto) throws BusinessException;

	Set<UserDto> findAllInconsistent() throws BusinessException;

	void updateInconsistent(UserDto userDto) throws BusinessException;

	void changePassword(PasswordDto password) throws BusinessException;

	UserDto findUser(String uuid, Version version) throws BusinessException;

	boolean exist(String uuid) throws BusinessException;

	UserDto create(UserDto userDto) throws BusinessException;

	boolean updateEmail(String currentEmail, String newEmail);

	List<InconsistentSearchDto> checkInconsistentUserStatus(UserSearchDto dto);

	List<String> autocompleteInconsistent(UserSearchDto dto) throws BusinessException;

	SecondFactorDto delete2FA(String userUuid, String secondFactorUuid, SecondFactorDto dto) throws BusinessException;

	SecondFactorDto find2FA(String userUuid, String secondFactorUuid) throws BusinessException;

	UserDto isAuthorized(Role role, Version version) throws BusinessException;
}
