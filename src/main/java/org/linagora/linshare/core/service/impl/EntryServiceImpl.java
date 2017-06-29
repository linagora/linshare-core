/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fred
 *
 */
public class EntryServiceImpl implements EntryService {

	private static final Logger logger = LoggerFactory.getLogger(EntryServiceImpl.class);

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
