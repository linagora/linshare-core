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
package org.linagora.linshare.webservice.userv5;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountContactListDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;

import javax.annotation.Nonnull;

public interface GuestRestService {

	GuestDto find(String uuid) throws BusinessException;

	void head(String uuid) throws BusinessException;

	List<GuestDto> findAll(String moderatorRole, String pattern) throws BusinessException;

	GuestDto create(GuestDto guest) throws BusinessException;

	GuestDto update(GuestDto guest, String uuid) throws BusinessException;

	GuestDto delete(GuestDto guest, String uuid) throws BusinessException;

	List<GuestDto> search(UserSearchDto userSearchDto) throws BusinessException;

	void resetPassword(GuestDto dto, String uuid) throws BusinessException;

	/**
	 * Retrieves the contact lists for a specific guest identified by its UUID.
	 *
	 * @param uuid The unique identifier of the guest. Must not be null.
	 * @return A list of {@link AccountContactListDto} representing the allowed contact lists.
	 *         May be empty if none are found.
	 * @throws BusinessException if an error occurs or if the guest is not found.
	 */
	public @Nonnull List<AccountContactListDto> findContactListsByGuest(@Nonnull final String uuid);
}
