/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.AnonymousShareEntryBusinessService;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.Recipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AnonymousShareEntryResourceAccessControl;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class AnonymousShareEntryServiceImpl extends
		GenericEntryServiceImpl<Contact, AnonymousShareEntry> implements
		AnonymousShareEntryService {

	private final FunctionalityReadOnlyService functionalityService;

	private final AnonymousShareEntryBusinessService anonymousShareEntryBusinessService;

	private final ShareExpiryDateService shareExpiryDateService;

	private final LogEntryService logEntryService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final FavouriteRepository<String, User, RecipientFavourite> recipientFavouriteRepository;

	private static final Logger logger = LoggerFactory
			.getLogger(AnonymousShareEntryServiceImpl.class);

	public AnonymousShareEntryServiceImpl(
			final FunctionalityReadOnlyService functionalityService,
			final AnonymousShareEntryBusinessService anonymousShareEntryBusinessService,
			final ShareExpiryDateService shareExpiryDateService,
			final LogEntryService logEntryService,
			NotifierService notifierService,
			final MailBuildingService mailBuildingService,
			final DocumentEntryBusinessService documentEntryBusinessService,
			final FavouriteRepository<String, User, RecipientFavourite> recipientFavouriteRepository,
			final AnonymousShareEntryResourceAccessControl rac) {
		super(rac);
		this.functionalityService = functionalityService;
		this.anonymousShareEntryBusinessService = anonymousShareEntryBusinessService;
		this.shareExpiryDateService = shareExpiryDateService;
		this.logEntryService = logEntryService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.recipientFavouriteRepository = recipientFavouriteRepository;
	}

	@Override
	public AnonymousShareEntry find(Account actor, Account targetedAccount,
			String shareUuid) throws BusinessException {
		preChecks(actor, targetedAccount);
		Validate.notEmpty(shareUuid, "Share uuid is required.");
		AnonymousShareEntry share = anonymousShareEntryBusinessService
				.findByUuid(shareUuid);
		if (share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(
					BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_NOT_FOUND,
					"Share entry not found : " + shareUuid);
		}
		checkReadPermission(actor, targetedAccount, AnonymousShareEntry.class,
				BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN, share);
		return share;
	}

	// TODO FMA - Refactoring shares
	@Override
	public Set<AnonymousShareEntry> create(Account actor, User targetedAccount, ShareContainer sc)
			throws BusinessException {
		preChecks(actor, targetedAccount);
		Validate.notNull(sc, "Share container is required.");
		checkCreatePermission(actor, targetedAccount, AnonymousShareEntry.class,
				BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN, null);
		Set<AnonymousShareEntry> entries = Sets.newHashSet();
		Date expiryDate = sc.getExpiryDate();
		Boolean passwordProtected = sc.getSecured();
		if (passwordProtected == null) passwordProtected = false;
		if (functionalityService.isSauMadatory(targetedAccount.getDomain()
				.getIdentifier())) {
			passwordProtected = true;
		} else if (functionalityService.isSauForbidden(targetedAccount.getDomain()
				.getIdentifier())) {
			passwordProtected = false;
		}
		if (expiryDate == null) {
			expiryDate = shareExpiryDateService
					.computeMinShareExpiryDateOfList(sc.getDocuments(), targetedAccount);
		}
		for (Recipient recipient : sc.getAnonymousShareRecipients()) {
			Language mailLocale = recipient.getLocale();
			if (mailLocale == null){
				mailLocale = targetedAccount.getExternalMailLocale();
			}
			MailContainer mailContainer = new MailContainer(
					mailLocale, sc.getMessage(), sc.getSubject());
			AnonymousUrl anonymousUrl = anonymousShareEntryBusinessService
					.create(targetedAccount, recipient, sc.getDocuments(), expiryDate,
							passwordProtected);
			// logs.
			for (DocumentEntry documentEntry : sc.getDocuments()) {
				ShareLogEntry logEntry = new ShareLogEntry(targetedAccount, documentEntry,
						LogAction.FILE_SHARE, "Anonymous sharing of a file",
						expiryDate, recipient);
				logEntryService.create(logEntry);
			}
			// Notifications
			MailContainerWithRecipient mail = mailBuildingService
					.buildNewSharingProtected(targetedAccount, mailContainer,
							anonymousUrl);
			sc.addMailContainer(mail);
			recipientFavouriteRepository.incAndCreate(targetedAccount,
					recipient.getMail());
			entries.addAll(anonymousUrl.getAnonymousShareEntries());
		}
		return entries;
	}

	@Override
	public void delete(Account actor, Account targetedAccount, String shareUuid)
			throws BusinessException {
		preChecks(actor, targetedAccount);
		Validate.notEmpty(shareUuid, "Share uuid is required.");
		AnonymousShareEntry share = find(actor, targetedAccount, shareUuid);
		checkDeletePermission(actor, targetedAccount, AnonymousShareEntry.class,
				BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN, share);
		anonymousShareEntryBusinessService.delete(share);
		ShareLogEntry logEntry = new ShareLogEntry(targetedAccount, share,
				LogAction.SHARE_DELETE, "Deleting anonymous share");
		logEntryService.create(logEntry);

		// TODO : anonymous share deletion notification
		// notifierService.sendNotification();
	}

	@Override
	public InputStream getAnonymousShareEntryStream(Account actor,
			String shareUuid) throws BusinessException {
		preChecks(actor, null, true);
		Validate.notEmpty(shareUuid, "Share uuid is required.");
		AnonymousShareEntry shareEntry = anonymousShareEntryBusinessService
				.findByUuid(shareUuid);
		if (shareEntry == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(
					BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_NOT_FOUND,
					"Share entry not found : " + shareUuid);
		}
		checkDownloadPermission(actor, null, AnonymousShareEntry.class,
				BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN, shareEntry);
		ShareLogEntry logEntry = new ShareLogEntry(shareEntry.getEntryOwner(),
				shareEntry, LogAction.ANONYMOUS_SHARE_DOWNLOAD,
				"Anonymous user "
						+ shareEntry.getEntryOwner().getAccountReprentation()
						+ " downloaded a file");
		logEntryService.create(logEntry);
		// send a notification by mail to the owner
		MailContainerWithRecipient mail = mailBuildingService
				.buildAnonymousDownload(shareEntry);
		notifierService.sendNotification(mail);
		return documentEntryBusinessService.getDocumentStream(shareEntry
				.getDocumentEntry());
	}
}
