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

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.ShareRecipientStatistic;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareEntryBusinessServiceImpl implements ShareEntryBusinessService {

	private final ShareEntryRepository shareEntryRepository ;

	private final DocumentEntryRepository documentEntryRepository ;

	private static final Logger logger = LoggerFactory.getLogger(ShareEntryBusinessServiceImpl.class);

	public ShareEntryBusinessServiceImpl(ShareEntryRepository shareEntryRepository,
			DocumentEntryRepository documentEntryRepository) {
		super();
		this.shareEntryRepository = shareEntryRepository;
		this.documentEntryRepository = documentEntryRepository;
	}

	@Override
	public ShareEntry find(String uuid) {
		return shareEntryRepository.findByUuid(uuid);
	}

	@Override
	public ShareEntry create(DocumentEntry documentEntry, User sender, User recipient, Calendar expirationDate, ShareEntryGroup shareEntryGroup, String sharingNote) throws BusinessException {
		ShareEntry shareEntity;
		if(sharingNote == null) {
			sharingNote = "";
		}
		ShareEntry current_share = shareEntryRepository.getShareEntry(documentEntry, sender, recipient);
		if(current_share == null) {
			// if not, we create one
			logger.debug("Creation of a new share between sender " + sender.getMail() + " and recipient " + recipient.getMail());
			ShareEntry share= new ShareEntry(sender, documentEntry.getName(), sharingNote, recipient, documentEntry, expirationDate, shareEntryGroup);
			shareEntity = shareEntryRepository.create(share);
			documentEntry.incrementShared();
		} else {
			// if it does, we update the expiration date
			logger.debug("The share (" + documentEntry.getUuid() +") between sender " + sender.getMail() + " and recipient " + recipient.getMail() + " already exists. Just updating expiration date.");
			shareEntity = current_share;
			shareEntity.setExpirationDate(expirationDate);
			if(!sharingNote.isEmpty()) {
				shareEntity.setComment(sharingNote);
			}
			shareEntryRepository.update(shareEntity);
		}

		// If the current document was previously shared, we need to rest its expiration date
		documentEntry.setExpirationDate(null);
		documentEntryRepository.update(documentEntry);
		return shareEntity;
	}

	@Override
	public void delete(ShareEntry share) throws BusinessException {
		shareEntryRepository.delete(share);
		DocumentEntry documentEntry = share.getDocumentEntry();
		documentEntry.decrementShared();
		documentEntryRepository.update(documentEntry);
	}

	@Override
	public ShareEntry update(ShareEntry entry) throws BusinessException {
		if (entry.getComment() == null) {
			entry.setComment("");
		}
		return shareEntryRepository.update(entry);
	}

	@Override
	public List<ShareEntry> findAllMyRecievedShareEntries(User owner) {
		return shareEntryRepository.findAllMyRecievedShareEntries(owner);
	}

	@Override
	public ShareEntry updateDownloadCounter(String uuid) throws BusinessException {
		ShareEntry shareEntry = find(uuid);
		if (shareEntry == null) {
			logger.error("Share not found : " + uuid);
			throw new BusinessException(BusinessErrorCode.SHARE_NOT_FOUND, "Share entry not found : " + uuid);
		}
		shareEntry.incrementDownloaded();
		return shareEntryRepository.update(shareEntry);
	}

	@Override
	public List<String> findAllExpiredEntries() {
		return shareEntryRepository.findAllExpiredEntries();
	}


	@Override
	public List<ShareRecipientStatistic> getShareRecipientStatistic(String domainUuid, String beginDate, String endDate) {
		return shareEntryRepository.getShareRecipientStatistic(domainUuid, beginDate, endDate);
	}
}
