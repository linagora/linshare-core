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

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.AnonymousShareEntryBusinessService;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.Recipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.ShareFileDownloadEmailContext;
import org.linagora.linshare.core.notifications.context.ShareNewShareEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.AnonymousShareEntryResourceAccessControl;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;

import com.google.common.collect.Sets;

public class AnonymousShareEntryServiceImpl extends
		GenericEntryServiceImpl<Contact, AnonymousShareEntry> implements
		AnonymousShareEntryService {

	private final FunctionalityReadOnlyService functionalityService;

	private final AnonymousShareEntryBusinessService anonymousShareEntryBusinessService;

	private final LogEntryService logEntryService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final FavouriteRepository<String, User, RecipientFavourite> recipientFavouriteRepository;

	public AnonymousShareEntryServiceImpl(
			final FunctionalityReadOnlyService functionalityService,
			final AnonymousShareEntryBusinessService anonymousShareEntryBusinessService,
			final LogEntryService logEntryService,
			NotifierService notifierService,
			final MailBuildingService mailBuildingService,
			final DocumentEntryBusinessService documentEntryBusinessService,
			final FavouriteRepository<String, User, RecipientFavourite> recipientFavouriteRepository,
			final AnonymousShareEntryResourceAccessControl rac) {
		super(rac);
		this.functionalityService = functionalityService;
		this.anonymousShareEntryBusinessService = anonymousShareEntryBusinessService;
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

	// TODO FMA - Refactoring shares - this code is very ugly !!
	@Override
	public Set<AnonymousShareEntry> create(Account actor, User owner, ShareContainer sc, ShareEntryGroup shareEntryGroup)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(sc, "Share container is required.");
		checkCreatePermission(actor, owner, AnonymousShareEntry.class,
				BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN, null);
		Set<AnonymousShareEntry> entries = Sets.newHashSet();
		BooleanValueFunctionality anonymousUrlFunc = functionalityService.getAnonymousUrl(owner.getDomain());
		Boolean passwordProtected = sc.getSecured();
		if (passwordProtected == null) {
			passwordProtected = anonymousUrlFunc.getValue();
		}
		if (!anonymousUrlFunc.getDelegationPolicy().getStatus()) {
			// User have not the right to override admin parameter.
			passwordProtected = anonymousUrlFunc.getValue();
		}
		sc.setSecured(passwordProtected);
		for (Recipient recipient : sc.getAnonymousShareRecipients()) {
			AnonymousUrl anonymousUrl = anonymousShareEntryBusinessService
					.create(actor, owner, recipient, sc.getDocuments(), sc.getExpiryCalendar(),
							passwordProtected, shareEntryGroup, sc.getSharingNote());
			// Notifications
			ShareNewShareEmailContext context = new ShareNewShareEmailContext(owner, anonymousUrl, sc);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			sc.addMailContainer(mail);
			sc.addLogs(anonymousUrl.getLogs());
			recipientFavouriteRepository.incAndCreate(owner,
					recipient.getMail());
			entries.addAll(anonymousUrl.getAnonymousShareEntries());
		}
		// logs all entries in share container and reset it.
		logEntryService.insert(sc.getLogs());
		sc.getLogs().clear();
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
		ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, targetedAccount,
				LogAction.DELETE, share, AuditLogEntryType.ANONYMOUS_SHARE_ENTRY);
		logEntryService.insert(log);
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
		MailContainerWithRecipient mail = null;
		ShareFileDownloadEmailContext context = new ShareFileDownloadEmailContext(shareEntry);
		if (shareEntry.getDownloaded() <= 0) {
			mail = mailBuildingService.build(context);
		} else {
			// share entry was downloaded at least once.
			Boolean send = functionalityService.getAnonymousUrlNotification(
					shareEntry.getEntryOwner().getDomain()).getValue();
			if (send) {
				// send a notification by mail to the owner for every download
				mail = mailBuildingService.build(context);
			}
		}
		shareEntry = anonymousShareEntryBusinessService.updateDownloadCounter(shareEntry);
		ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, shareEntry.getAnonymousUrl().getContact(),
				shareEntry, AuditLogEntryType.ANONYMOUS_SHARE_ENTRY);
		logEntryService.insert(log);
		notifierService.sendNotification(mail);
		return documentEntryBusinessService.getDocumentStream(shareEntry
				.getDocumentEntry());
	}

	@Override
	public List<String> findAllExpiredEntries(Account actor, Account owner) {
		preChecks(actor, owner);
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You do not have the right to use this method.");
		}
		return anonymousShareEntryBusinessService.findAllExpiredEntries();
	}

	@Override
	public InputStream getAnonymousShareEntryThumbnailStream(Account actor,
			String shareUuid) throws BusinessException {
		AnonymousShareEntry shareEntry = find(actor, actor, shareUuid);
		rac.checkThumbNailDownloadPermission(actor, actor,
				AnonymousShareEntry.class,
				BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN, shareEntry);
		return documentEntryBusinessService
				.getDocumentThumbnailStream(shareEntry.getDocumentEntry());
	}
}
