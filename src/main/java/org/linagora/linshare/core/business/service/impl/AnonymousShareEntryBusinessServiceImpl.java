package org.linagora.linshare.core.business.service.impl;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.business.service.AnonymousShareEntryBusinessService;
import org.linagora.linshare.core.business.service.AnonymousUrlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnonymousShareEntryBusinessServiceImpl implements AnonymousShareEntryBusinessService {

	private final AnonymousShareEntryRepository anonymousShareEntryRepository ;
	private final AccountService accountService;
	private final DocumentEntryRepository documentEntryRepository ;
	private final ContactRepository contactRepository ;
	private final AnonymousUrlBusinessService anonymousUrlBusinessService;
	
	
	private static final Logger logger = LoggerFactory.getLogger(AnonymousShareEntryBusinessServiceImpl.class);
	
	public AnonymousShareEntryBusinessServiceImpl(AnonymousShareEntryRepository anonymousShareEntryRepository, AccountService accountService, DocumentEntryRepository documentEntryRepository, ContactRepository contactRepository,
			AnonymousUrlBusinessService anonymousUrlBusinessService) {
		super();
		this.anonymousShareEntryRepository = anonymousShareEntryRepository;
		this.accountService = accountService;
		this.documentEntryRepository = documentEntryRepository;
		this.contactRepository = contactRepository;
		this.anonymousUrlBusinessService = anonymousUrlBusinessService;
	}
	

	@Override
	public AnonymousShareEntry findByUuid(String uuid) {
		return anonymousShareEntryRepository.findById(uuid);
	}

	
	
	@Override
	public AnonymousShareEntry findByUuidForDownload(String uuid) throws BusinessException {
		AnonymousShareEntry shareEntry = anonymousShareEntryRepository.findById(uuid);
		if(shareEntry == null) {
			logger.error("Share not found : " + uuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + uuid);
		}
		
		// update count
		shareEntry.incrementDownload();
		anonymousShareEntryRepository.update(shareEntry);
		
		return shareEntry;
	}


	public AnonymousShareEntry createAnonymousShare(DocumentEntry documentEntry, AnonymousUrl anonymousUrl, User sender, Contact recipient, Calendar expirationDate) throws BusinessException {
		
		AnonymousShareEntry anonymousShare = null;
		AnonymousShareEntry current_share = null;
		
		
		Contact contact = contactRepository.find(recipient);
		if(contact != null) {
			current_share = anonymousShareEntryRepository.getAnonymousShareEntry(documentEntry, sender, contact);
		} else {
			contact = contactRepository.create(recipient);
		}
		
		if(current_share != null) {
			// if it does, we update the expiration date
			logger.debug("The share (" + documentEntry.getUuid() +") between sender " + sender.getMail() + " and recipient " + contact.getMail() + " already exists. Just updating expiration date.");
			anonymousShare = current_share;
			anonymousShare.setExpirationDate(expirationDate);
			anonymousShareEntryRepository.update(anonymousShare);
		} else {
			// if not, we create one
			logger.debug("Creation of a new anonymous share between sender " + sender.getMail() + " and recipient " + contact.getMail());
			AnonymousShareEntry share= new AnonymousShareEntry(sender, documentEntry.getName(), documentEntry.getComment(), documentEntry, anonymousUrl , contact, expirationDate);
			anonymousShare = anonymousShareEntryRepository.create(share);
			contact.getAnonymousShareEntries().add(anonymousShare);
			contactRepository.update(contact);
			
			// If the current document was previously shared, we need to rest its expiration date
			documentEntry.setExpirationDate(null);
			
			documentEntry.getAnonymousShareEntries().add(anonymousShare);
			sender.getEntries().add(anonymousShare);
			documentEntryRepository.update(documentEntry);
			accountService.update(sender);
		}
		
		return anonymousShare;
	}

	
	@Override
	public AnonymousUrl createAnonymousShare(List<DocumentEntry> documentEntries, User sender, Contact recipient, Calendar expirationDate, Boolean passwordProtected) throws BusinessException {
		
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.create(passwordProtected);
		
		for (DocumentEntry documentEntry : documentEntries) {
			AnonymousShareEntry anonymousShareEntry = createAnonymousShare(documentEntry, anonymousUrl, sender, recipient, expirationDate);
			anonymousUrl.getAnonymousShareEntries().add(anonymousShareEntry);
		}
		anonymousUrlBusinessService.update(anonymousUrl);
		
		return anonymousUrl;
	}

	
	@Override
	public void deleteAnonymousShare(AnonymousShareEntry anonymousShare) throws BusinessException {
		anonymousShareEntryRepository.delete(anonymousShare);
		
		DocumentEntry documentEntry = anonymousShare.getDocumentEntry();
		documentEntry.getAnonymousShareEntries().remove(anonymousShare);
		
		Account sender = anonymousShare.getEntryOwner();
		sender.getEntries().remove(anonymousShare);
		
		documentEntryRepository.update(documentEntry);
		accountService.update(sender);
	}

	
}
