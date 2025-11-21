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

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.ShareRecipientStatistic;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.exception.BusinessException;

import javax.annotation.Nonnull;

public interface ShareEntryBusinessService {

	ShareEntry create(DocumentEntry documentEntry, User sender, User recipient,
			Calendar expirationDate, ShareEntryGroup shareEntryGroup, String sharingNote)
			throws BusinessException;

	void delete(ShareEntry share) throws BusinessException;

	ShareEntry find(String uuid);

	ShareEntry update(ShareEntry entry) throws BusinessException;

	List<ShareEntry> findAllMyRecievedShareEntries(User owner);

	ShareEntry updateDownloadCounter(String uuid) throws BusinessException;

	List<String> findAllExpiredEntries();

	List<ShareRecipientStatistic> getTopSharesByFileSize(List<String> domainUuids, String beginDate, String endDate, boolean addAnonymousShares);

	List<ShareRecipientStatistic> getTopSharesByFileCount(List<String> domainUuids, String beginDate, String endDate, boolean addAnonymousShares);

	void transferShareEntryFromGuestToInternal(@Nonnull final Guest guestAccount, @Nonnull final User owner);

	/**
	 * Check whether the provided document entry is, already, explicitly (outside the scope of a contact list) shared by
	 * the provided sender with the provided recipient.
	 *
	 * <p>
	 * This means there's already a persisted share entry with the provided details that has {@code contactListUuid} set
	 * to null.
	 * </p>
	 *
	 * @param documentEntry document entry of which to check the share. Not {@code null}.
	 * @param sender        the sender of the share. Not {@code null}.
	 * @param recipient     recipient of the share. Not {@code null}.
	 * @return {@code true} if the share already exists but as an explicit share and not as part of a contact list.
	 * {@code false} otherwise.
	 */
	public boolean isShareEntryAlreadyExistsWithoutContactList(@Nonnull final DocumentEntry documentEntry,
			@Nonnull final User sender, @Nonnull final User recipient);

}
