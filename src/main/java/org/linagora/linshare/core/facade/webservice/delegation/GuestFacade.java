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
package org.linagora.linshare.core.facade.webservice.delegation;

import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountContactListDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.utils.Version;

import javax.annotation.Nonnull;

public interface GuestFacade extends DelegationGenericFacade {

	List<GuestDto> findAll(Version version, String pattern, String moderatorRole) throws BusinessException;

	List<GuestDto> findAll(String actorUuid) throws BusinessException;

	List<GuestDto> search(Version version, UserSearchDto userSearchDto) throws BusinessException;

	GuestDto find(Version version, String actorUuid, String uuid) throws BusinessException;

	GuestDto find(String actorUuid, String domain, String mail) throws BusinessException;

	GuestDto create(Version version, String actorUuid, GuestDto dto) throws BusinessException;

	GuestDto update(Version version, String actorUuid, GuestDto dto, String uuid) throws BusinessException;

	GuestDto delete(Version version, String actorUuid, GuestDto dto, String uuid) throws BusinessException;

	void resetPassword(GuestDto dto, String uuid) throws BusinessException;

	void changePassword(PasswordDto password);

	Map<String, Integer> getPasswordRules() throws BusinessException;

	/**
	 * Retrieves contact lists of a guest identified by its UUID, with support
	 * for version-specific data retrieval.
	 *
	 * @param version The API {@link Version} for data retrieval. Must not be {@code null}.
	 * @param uuid    The unique identifier of the {@link Guest}. Must not be {@code null} or empty.
	 * @return        A list of {@link AccountContactListDto} for the specified guest,
	 *                or an empty list if none are found.
	 */
	public @Nonnull List<AccountContactListDto> findContactListsByGuest(@Nonnull final Version version, @Nonnull final String uuid) throws BusinessException;
}
