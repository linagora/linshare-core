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
package org.linagora.linshare.core.business.service.impl;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareEntryBusinessServiceImpl implements ShareEntryBusinessService {

	private final ShareEntryRepository shareEntryRepository ;
	private final AccountService accountService;
	private final DocumentEntryRepository documentEntryRepository ;
	
	private static final Logger logger = LoggerFactory.getLogger(ShareEntryBusinessServiceImpl.class);
	

	public ShareEntryBusinessServiceImpl(ShareEntryRepository shareEntryRepository, AccountService accountService, DocumentEntryRepository documentEntryRepository) {
		super();
		this.shareEntryRepository = shareEntryRepository;
		this.accountService = accountService;
		this.documentEntryRepository = documentEntryRepository;
	}


	@Override
	public ShareEntry findByUuid(String uuid) {
		return shareEntryRepository.findById(uuid);
	}


	@Override
	public ShareEntry createShare(DocumentEntry documentEntry, User sender, User recipient, Calendar expirationDate) throws BusinessException {
		ShareEntry shareEntity;
		ShareEntry current_share = shareEntryRepository.getShareEntry(documentEntry, sender, recipient);
		if(current_share == null) {
			// if not, we create one
			logger.debug("Creation of a new share between sender " + sender.getMail() + " and recipient " + recipient.getMail());
			ShareEntry share= new ShareEntry(sender, documentEntry.getName(), documentEntry.getComment(), recipient, documentEntry, expirationDate);
			shareEntity = shareEntryRepository.create(share);
		} else {
			// if it does, we update the expiration date
			logger.debug("The share (" + documentEntry.getUuid() +") between sender " + sender.getMail() + " and recipient " + recipient.getMail() + " already exists. Just updating expiration date.");
			shareEntity = current_share;
			shareEntity.setExpirationDate(expirationDate);
			shareEntryRepository.update(shareEntity);
		}
		
		// If the current document was previously shared, we need to rest its expiration date
		documentEntry.setExpirationDate(null);
		
		documentEntry.getShareEntries().add(shareEntity);
		recipient.getShareEntries().add(shareEntity);
		sender.getEntries().add(shareEntity);
		
		documentEntryRepository.update(documentEntry);
		accountService.update(recipient);
		accountService.update(sender);
		
		
		return shareEntity;
	}


	@Override
	public void deleteShare(ShareEntry share) throws BusinessException {
		
		shareEntryRepository.delete(share);
		
		DocumentEntry documentEntry = share.getDocumentEntry();
		documentEntry.getShareEntries().remove(share);
		
		Account recipient = share.getRecipient();
		recipient.getShareEntries().remove(share);
		
		Account sender = share.getEntryOwner();
		sender.getEntries().remove(share);
		
		documentEntryRepository.update(documentEntry);
		accountService.update(recipient);
		accountService.update(sender);
	}


	@Override
	public void updateShareComment(ShareEntry share, String comment) throws BusinessException {
		
		share.setComment(comment==null?"":comment);
		shareEntryRepository.update(share);
	}


	@Override
	public List<ShareEntry> findAllMyShareEntries(User owner) {
		return shareEntryRepository.findAllMyShareEntries(owner);
	}


	@Override
	public void addDownload(ShareEntry entry) throws BusinessException {
		entry.incrementDownloaded();
		shareEntryRepository.update(entry);
	}


}
