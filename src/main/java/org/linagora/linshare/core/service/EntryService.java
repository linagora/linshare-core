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

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface EntryService {

	/**
	 * The document entry and all its shares will be removed. A mail notification will be sent.
	 * @param actor
	 * @param owner 
	 * @param docEntryUuid
	 * @throws BusinessException
	 */
	public void deleteAllShareEntriesWithDocumentEntry(Account actor, Account owner, String docEntryUuid) throws BusinessException;

	/**
	 * All The document entries own by the user "owner" and all its shares will be removed. No mail will be sent.
	 * @param owner
	 */
	public void deleteAllShareEntriesWithDocumentEntries(Account actor, User owner ) throws BusinessException;

	/**
	 * All The share entries received by the user "recipient" will be removed. No mail will be sent.
	 * @param actor
	 * @param recipient
	 * @throws BusinessException
	 */
	public void deleteAllReceivedShareEntries(Account actor, User recipient ) throws BusinessException;

}
