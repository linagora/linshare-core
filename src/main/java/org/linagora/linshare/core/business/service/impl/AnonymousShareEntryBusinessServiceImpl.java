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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.business.service.AnonymousShareEntryBusinessService;
import org.linagora.linshare.core.business.service.AnonymousUrlBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.Recipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The whole business service and service need to be revamped. It is very ugly :(
 * @author FMartin
 *
 */
public class AnonymousShareEntryBusinessServiceImpl implements AnonymousShareEntryBusinessService {

	private final AnonymousShareEntryRepository anonymousShareEntryRepository ;
	private final AccountService accountService;
	private final DocumentEntryRepository documentEntryRepository ;
	private final ContactRepository contactRepository ;
	private final AnonymousUrlBusinessService businessService;

	private static final Logger logger = LoggerFactory.getLogger(AnonymousShareEntryBusinessServiceImpl.class);

	public AnonymousShareEntryBusinessServiceImpl(AnonymousShareEntryRepository anonymousShareEntryRepository, AccountService accountService, DocumentEntryRepository documentEntryRepository, ContactRepository contactRepository,
			AnonymousUrlBusinessService anonymousUrlBusinessService) {
		super();
		this.anonymousShareEntryRepository = anonymousShareEntryRepository;
		this.accountService = accountService;
		this.documentEntryRepository = documentEntryRepository;
		this.contactRepository = contactRepository;
		this.businessService = anonymousUrlBusinessService;
	}

	@Override
	public AnonymousShareEntry findByUuid(String uuid) {
		return anonymousShareEntryRepository.findById(uuid);
	}

	private AnonymousShareEntry createAnonymousShare(DocumentEntry documentEntry, AnonymousUrl anonymousUrl, User sender, Contact contact, Calendar expirationDate, ShareEntryGroup shareEntryGroup, String sharingNote) throws BusinessException {

		logger.debug("Creation of a new anonymous share between sender " + sender.getMail() + " and recipient " + contact.getMail());
		if(sharingNote == null) {
			sharingNote = "";
		}
		AnonymousShareEntry share= new AnonymousShareEntry(sender, documentEntry.getName(), sharingNote, documentEntry, anonymousUrl , expirationDate, shareEntryGroup);
		AnonymousShareEntry anonymousShare = anonymousShareEntryRepository.create(share);

		// If the current document was previously shared, we need to reset its expiration date
		documentEntry.setExpirationDate(null);
		documentEntry.incrementShared();

		documentEntry.getAnonymousShareEntries().add(anonymousShare);
		sender.getEntries().add(anonymousShare);
		return anonymousShare;
	}

	@Override
	public AnonymousUrl create(Account actor,
			User sender,
			Recipient recipient,
			Set<DocumentEntry> documentEntries,
			Calendar expirationCalendar,
			Boolean passwordProtected, ShareEntryGroup shareEntryGroup, String sharingNote) throws BusinessException {

		Contact someContact = new Contact(recipient.getMail());
		Contact contact = contactRepository.find(someContact);
		if(contact == null) {
			contact = contactRepository.create(someContact);
		}
		AnonymousUrl anonymousUrl = businessService.create(passwordProtected, contact);
		for (DocumentEntry documentEntry : documentEntries) {
			AnonymousShareEntry anonymousShareEntry = createAnonymousShare(documentEntry, anonymousUrl, sender, contact, expirationCalendar, shareEntryGroup, sharingNote);
			anonymousUrl.getAnonymousShareEntries().add(anonymousShareEntry);
			ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, sender, LogAction.CREATE, anonymousShareEntry, AuditLogEntryType.ANONYMOUS_SHARE_ENTRY);
			anonymousUrl.addLog(log);
		}
		businessService.update(anonymousUrl);
		return anonymousUrl;
	}

	@Override
	public void delete(AnonymousShareEntry anonymousShare) throws BusinessException {
		anonymousShareEntryRepository.delete(anonymousShare);

		DocumentEntry documentEntry = anonymousShare.getDocumentEntry();
		documentEntry.decrementShared();
		documentEntry.getAnonymousShareEntries().remove(anonymousShare);

		Account sender = anonymousShare.getEntryOwner();
		sender.getEntries().remove(anonymousShare);
		documentEntry.setModificationDate(new GregorianCalendar());
		documentEntryRepository.update(documentEntry);
		accountService.update(sender);
	}

	@Override
	public AnonymousShareEntry updateDownloadCounter(AnonymousShareEntry entry)
			throws BusinessException {
		entry.incrementDownloaded();
		return anonymousShareEntryRepository.update(entry);
	}

	@Override
	public List<String> findAllExpiredEntries() {
		return anonymousShareEntryRepository.findAllExpiredEntries();
	}
}
