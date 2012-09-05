package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.EntryService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public void deleteAllShareEntriesWithDocumentEntry(Account actor, String docEntryUuid, MailContainer mailContainer) throws BusinessException {
		try {
			DocumentEntry entry = documentEntryService.findById(actor, docEntryUuid);
			
			List<String> a = new ArrayList<String>();
			List<String> b = new ArrayList<String>();
			
			for (AnonymousShareEntry anonymousShareEntry : entry.getAnonymousShareEntries()) {
				a.add(anonymousShareEntry.getUuid());
			}
			
			for (ShareEntry shareEntry : entry.getShareEntries()) {
				b.add(shareEntry.getUuid());
			}

			for (String uuid : a) {
				anonymousShareEntryService.deleteShare(actor, uuid, mailContainer);
			}
			
			for (String uuid : b) {
				shareEntryService.deleteShare(actor, uuid, mailContainer);
			}
			
			documentEntryService.deleteDocumentEntry(actor, entry.getUuid());
			
		} catch (BusinessException e) {
			logger.error("can not delete document : " + docEntryUuid);
			throw e;
		}
	}
	
	
	@Override
	public void deleteAllShareEntriesWithDocumentEntry(Account actor, String docEntryUuid) throws BusinessException {
		this.deleteAllShareEntriesWithDocumentEntry(actor, docEntryUuid, null);
	}


	@Override
	public void deleteAllShareEntriesWithDocumentEntries( Account actor , User owner) throws BusinessException {
		List<DocumentEntry> documentEntries = documentEntryService.findAllMyDocumentEntries(actor, owner);
		for (DocumentEntry documentEntry : documentEntries) {
			this.deleteAllShareEntriesWithDocumentEntry(actor, documentEntry.getUuid());
		}
	}


	@Override
	public void deleteAllReceivedShareEntries(Account actor, User recipient) throws BusinessException {
		for (ShareEntry shareEntry : shareEntryService.findAllMyShareEntries(actor, recipient)) {
			shareEntryService.deleteShare(actor, shareEntry.getUuid(), null);
		}
		
	}


	@Override
	public void sendSharedUpdateDocNotification(Account actor, DocumentEntry documentEntry, String friendlySize, String originalFileName, MailContainer mailContainer) {
		if(documentEntry.isShared()) {
			for (AnonymousShareEntry anonymousShareEntry : documentEntry.getAnonymousShareEntries()) {
				anonymousShareEntryService.sendDocumentEntryUpdateNotification(actor, anonymousShareEntry, friendlySize, originalFileName, mailContainer);
			}
			
			for (ShareEntry shareEntry : documentEntry.getShareEntries()) {
				shareEntryService.sendDocumentEntryUpdateNotification(actor, shareEntry, friendlySize, originalFileName, mailContainer);
			}
		}
	}

}
