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

import java.util.Date;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.mto.CopyMto;

public interface ShareService {

	public Set<Entry> create(Account actor, User owner, ShareContainer shareContainer)
			throws BusinessException;

	public DocumentEntry deleteAllShareEntries(Account actor, Account owner,
			String docEntryUuid, LogActionCause actionCause) throws BusinessException;

	public Entry delete(Account actor, Account owner, String entryUuid) throws BusinessException;

	ShareEntry delete(Account actor, Account owner, ShareEntry share, LogActionCause cause) throws BusinessException;

	Date getUndownloadedSharedDocumentsAlertDuration(Account actor);

	ShareEntry findForDownloadOrCopyRight(Account actor, Account owner, String uuid)  throws BusinessException;

	ShareEntry markAsCopied(Account actor, Account owner, String uuid, CopyMto copiedTo)  throws BusinessException;
}
