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
		share.setComment(comment);
		shareEntryRepository.update(share);
	}


	@Override
	public List<ShareEntry> findAllMyShareEntries(User owner) {
		return shareEntryRepository.findAllMyShareEntries(owner);
	}

}
