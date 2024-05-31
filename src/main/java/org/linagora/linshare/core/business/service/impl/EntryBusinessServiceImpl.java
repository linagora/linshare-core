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
package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.repository.EntryRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class EntryBusinessServiceImpl implements EntryBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(EntryBusinessServiceImpl.class);

	private final EntryRepository entryRepository;

	private final ShareEntryRepository shareEntryRepository;

	private final AnonymousShareEntryRepository anonymousShareEntryRepository;

	public EntryBusinessServiceImpl(EntryRepository entryRepository,
			ShareEntryRepository shareEntryRepository,
			AnonymousShareEntryRepository anonymousShareEntryRepository) {
		super();
		this.entryRepository = entryRepository;
		this.shareEntryRepository = shareEntryRepository;
		this.anonymousShareEntryRepository = anonymousShareEntryRepository;
	}

	@Override
	public Entry find(String entryUuid) throws BusinessException {
		Validate.notEmpty(entryUuid, "The entry uiid is required.");
		return entryRepository.findById(entryUuid);
	}

	@Override
	public List<Entry> findAllMyShareEntries(User owner) {
		Validate.notNull(owner, "The owner is required.");
		return entryRepository.findAllMyShareEntries(owner);
	}

	@Override
	public List<ShareEntry> findAllMyShareEntries(User owner,
			DocumentEntry entry) {
		return shareEntryRepository.findAllMyShareEntries(owner, entry);
	}

	@Override
	public List<AnonymousShareEntry> findAllMyAnonymousShareEntries(User owner,
			DocumentEntry entry) {
		return anonymousShareEntryRepository.findAllMyAnonymousShareEntries(owner, entry);
	}

	@Override
	public void transferEntriesFromGuestToInternal(@Nonnull final Guest guestAccount,@Nonnull final User owner) {
		final List<Entry> entries = entryRepository.findAllMyEntries(guestAccount);
		if (entries != null) {
			logger.debug("Start transferring entries from guest to internal");
			for (final Entry entry : entries) {
				try {
					entry.setEntryOwner(owner);
					entryRepository.update(entry);
					logger.debug("the entry is transferred successfully");
				} catch (final BusinessException | IllegalArgumentException e) {
					logger.error("An error occurred while transferring entries from guest to internal");
					throw e;
				}
			}
		} else {
			logger.debug("entry list is null");
		}
	}

}
