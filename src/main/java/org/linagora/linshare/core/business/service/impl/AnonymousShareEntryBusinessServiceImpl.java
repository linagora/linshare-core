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


	private AnonymousShareEntry createAnonymousShare(DocumentEntry documentEntry, AnonymousUrl anonymousUrl, User sender, Contact contact, Calendar expirationDate) throws BusinessException {
		
		logger.debug("Creation of a new anonymous share between sender " + sender.getMail() + " and recipient " + contact.getMail());
		AnonymousShareEntry share= new AnonymousShareEntry(sender, documentEntry.getName(), documentEntry.getComment(), documentEntry, anonymousUrl , expirationDate);
		AnonymousShareEntry anonymousShare = anonymousShareEntryRepository.create(share);
		
		// If the current document was previously shared, we need to rest its expiration date
		documentEntry.setExpirationDate(null);
		
		documentEntry.getAnonymousShareEntries().add(anonymousShare);
		sender.getEntries().add(anonymousShare);
		documentEntryRepository.update(documentEntry);
		accountService.update(sender);
		
		return anonymousShare;
	}

	
	@Override
	public AnonymousUrl createAnonymousShare(List<DocumentEntry> documentEntries, User sender, Contact recipient, Calendar expirationDate, Boolean passwordProtected) throws BusinessException {
		
		Contact contact = contactRepository.find(recipient);
		if(contact == null) {
			contact = contactRepository.create(recipient);
		}
		
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.create(passwordProtected, contact);
		
		for (DocumentEntry documentEntry : documentEntries) {
			AnonymousShareEntry anonymousShareEntry = createAnonymousShare(documentEntry, anonymousUrl, sender, contact, expirationDate);
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
