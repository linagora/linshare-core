/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.business.service.impl;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
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

}
