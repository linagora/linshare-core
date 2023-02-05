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
package org.linagora.linshare.core.service;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;

import com.google.common.io.ByteSource;

public interface AnonymousShareEntryService {

	AnonymousShareEntry find(Account actor, Account targetedAccount, String shareUuid)
			throws BusinessException;

	Set<AnonymousShareEntry> create(Account actor, User targetedAccount, ShareContainer shareContainer, ShareEntryGroup shareEntryGroup)
			throws BusinessException;

	void delete(Account actor, Account targetedAccount, String shareUuid)
			throws BusinessException;

	ByteSource getAnonymousShareEntryByteSource(Account actor, String shareUuid)
			throws BusinessException;

	ByteSource getAnonymousShareEntryThumbnailByteSource(Account actor, String shareUuid, ThumbnailType kind)
			throws BusinessException;

	List<String> findAllExpiredEntries(Account actor, Account owner);
}
