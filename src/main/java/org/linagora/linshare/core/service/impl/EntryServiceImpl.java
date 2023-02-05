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
package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.EntryService;
import org.linagora.linshare.core.service.ShareEntryService;

/**
 * @author fred
 *
 */
public class EntryServiceImpl implements EntryService {

	private final ShareEntryService shareEntryService;

	private final DocumentEntryService documentEntryService;

	private final AnonymousShareEntryService anonymousShareEntryService;


	public EntryServiceImpl(ShareEntryService shareEntryService, DocumentEntryService documentEntryService, AnonymousShareEntryService anonymousShareEntryService) {
		super();
		this.shareEntryService = shareEntryService;
		this.documentEntryService = documentEntryService;
		this.anonymousShareEntryService = anonymousShareEntryService;
	}

	@Override
	public void deleteAllShareEntriesWithDocumentEntry(Account actor, Account owner, String docEntryUuid) throws BusinessException {
		DocumentEntry documentEntry = documentEntryService.find(actor, owner, docEntryUuid);
		deleteAllShareEntries(actor, owner, documentEntry);
		documentEntryService.delete(actor, owner, documentEntry.getUuid());
	}

	private void deleteAllShareEntries(Account actor, Account owner, DocumentEntry entry) throws BusinessException {
		List<String> a = new ArrayList<String>();
		List<String> b = new ArrayList<String>();

		for (AnonymousShareEntry anonymousShareEntry : entry.getAnonymousShareEntries()) {
			a.add(anonymousShareEntry.getUuid());
		}

		for (ShareEntry shareEntry : entry.getShareEntries()) {
			b.add(shareEntry.getUuid());
		}

		for (String uuid : a) {
			anonymousShareEntryService.delete(actor, owner, uuid);
		}

		for (String uuid : b) {
			shareEntryService.delete(owner, owner, uuid, null);
		}
	}

	@Override
	public void deleteAllShareEntriesWithDocumentEntries( Account actor , User owner) throws BusinessException {
		List<DocumentEntry> documentEntries = documentEntryService.findAll(actor, owner);
		for (DocumentEntry documentEntry : documentEntries) {
			this.deleteAllShareEntriesWithDocumentEntry(actor, owner, documentEntry.getUuid());
		}
	}

	@Override
	public void deleteAllReceivedShareEntries(Account actor, User recipient) throws BusinessException {
		for (ShareEntry shareEntry : shareEntryService.findAllMyRecievedShareEntries(actor, recipient)) {
			shareEntryService.delete(actor, actor, shareEntry.getUuid(), null);
		}
	}
}
