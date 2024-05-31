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
package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessException;

import javax.annotation.Nonnull;

public interface ShareEntryGroupBusinessService {

	public ShareEntryGroup create(ShareEntryGroup entity) throws BusinessException;

	void delete(ShareEntryGroup shareEntryGroup)
			throws BusinessException;

	ShareEntryGroup findByUuid(String uuid) throws BusinessException;

	ShareEntryGroup update(ShareEntryGroup shareEntryGroup, ShareEntryGroup shareEntryGroupObject)
			throws BusinessException;

	ShareEntryGroup update(ShareEntryGroup shareEntryGroup) throws BusinessException;

	List<String> findAllAboutToBeNotified();

	List<String> findAllToPurge();

	List<ShareEntryGroup> findAll(Account owner) throws BusinessException;

	void transferShareEntryGroupFromGuestToInternal(@Nonnull final Guest guest, @Nonnull final Account owner);
}
