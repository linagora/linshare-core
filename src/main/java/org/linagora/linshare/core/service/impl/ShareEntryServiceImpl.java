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
package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareFileDownloadEmailContext;
import org.linagora.linshare.core.notifications.context.ShareFileShareDeletedEmailContext;
import org.linagora.linshare.core.notifications.context.ShareNewShareEmailContext;
import org.linagora.linshare.core.notifications.context.ShareWarnRecipientAboutExpiredShareEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.ShareEntryResourceAccessControl;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.entities.mto.ShareEntryMto;

import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;

public class ShareEntryServiceImpl extends GenericEntryServiceImpl<Account, ShareEntry>
		implements ShareEntryService {

	private final GuestRepository guestRepository;

	private final FunctionalityReadOnlyService functionalityService;

	private final ShareEntryBusinessService shareEntryBusinessService;

	private final LogEntryService logEntryService;

	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final FavouriteRepository<String, User, RecipientFavourite> recipientFavouriteRepository;

	public ShareEntryServiceImpl(
			GuestRepository guestRepository,
			FunctionalityReadOnlyService functionalityService,
			ShareEntryBusinessService shareEntryBusinessService,
			LogEntryService logEntryService,
			DocumentEntryBusinessService documentEntryBusinessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			FavouriteRepository<String, User, RecipientFavourite> recipientFavouriteRepository,
			ShareEntryResourceAccessControl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.guestRepository = guestRepository;
		this.functionalityService = functionalityService;
		this.shareEntryBusinessService = shareEntryBusinessService;
		this.logEntryService = logEntryService;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.recipientFavouriteRepository = recipientFavouriteRepository;
	}

	@Override
	public ShareEntry find(Account actor, Account owner, String uuid)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Missing share entry uuid");

		ShareEntry entry = shareEntryBusinessService.find(uuid);
		if (entry == null) {
			logger.error("Current actor " + actor.getAccountRepresentation()
					+ " is looking for a misssing share entry (" + uuid
					+ ") owned by : " + owner.getAccountRepresentation());
			String message = "Can not find share entry with uuid : " + uuid;
			throw new BusinessException(
					BusinessErrorCode.SHARE_ENTRY_NOT_FOUND, message);
		}
		checkReadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, null);
		return entry;
	}

	@Override
	public ShareEntry findForDownloadOrCopyRight(Account actor, Account owner, String uuid) throws BusinessException {
		ShareEntry share = find(actor, owner, uuid);
		checkDownloadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		return share;
	}

	@Override
	public ShareEntry markAsCopied(Account actor, Account owner, String uuid, CopyMto copiedTo)
			throws BusinessException {
		ShareEntry share = find(actor, owner, uuid);
		checkDownloadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		MailContainerWithRecipient mail = null;
		if (share.getDownloaded() <= 0) {
			ShareFileDownloadEmailContext context = new ShareFileDownloadEmailContext(share);
			mail = mailBuildingService.build(context);
		}
		share = shareEntryBusinessService.updateDownloadCounter(share.getUuid());
		ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.DOWNLOAD, share,
				AuditLogEntryType.SHARE_ENTRY);
		log.setCause(LogActionCause.COPY);
		log.setCopiedTo(copiedTo);
		logEntryService.insert(log);
		notifierService.sendNotification(mail, true);
		return share;
	}

	@Override
	public ShareEntry delete(Account actor, Account owner, String uuid, LogActionCause cause)
			throws BusinessException {
		Validate.notEmpty(uuid, "Missing share entry uuid");
		ShareEntry share = find(actor, owner, uuid);
		checkDeletePermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		logger.debug("Share deleted : " + share.getUuid());

		MailContainerWithRecipient mail = null;
		ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.DELETE, share,
				AuditLogEntryType.SHARE_ENTRY);
		log.setCause(cause);
		if (share.getRecipient().equals(owner)) {
			// If the modified account (aka owner parameter) is the recipient of this share.
			// We does not need to send him a notification.
			// But we need to warn the sender
			String senderUuid = share.getEntryOwner().getLsUuid();
			log.addRelatedAccounts(senderUuid);
			logEntryService.insert(log);
		} else {
			String recipientUuid = share.getRecipient().getLsUuid();
			log.addRelatedAccounts(recipientUuid);
			logEntryService.insert(log);
			if (LogActionCause.EXPIRATION.equals(cause)) {
				// The system is deleting the current share, we need to warn the recipient.
				EmailContext context = new ShareWarnRecipientAboutExpiredShareEmailContext(share);
				mail = mailBuildingService.build(context);
			} else {
				// The sender is deleting the current share, we need to warn the recipient.
				EmailContext context = new ShareFileShareDeletedEmailContext(share);
				mail = mailBuildingService.build(context);
			}
		}
		shareEntryBusinessService.delete(share);
		TimeUnitValueFunctionality fileExpirationFunc = functionalityService.getDefaultFileExpiryTimeFunctionality(owner.getDomain());
		if (fileExpirationFunc.getActivationPolicy().getStatus()) {
			DocumentEntry documentEntry = share.getDocumentEntry();
			if (documentEntryBusinessService.getRelatedEntriesCount(documentEntry) == 0 ) {
				Calendar deletionDate = Calendar.getInstance();
				deletionDate.add(fileExpirationFunc.toCalendarValue(), fileExpirationFunc.getValue());
				documentEntry.setExpirationDate(deletionDate);
				documentEntryBusinessService.update(documentEntry);
			}
		}
		notifierService.sendNotification(mail, true);
		return share;
	}

	@Override
	public ShareEntry update(Account actor, Account owner, ShareEntry dto)
			throws BusinessException {
		Validate.notNull(dto, "Missing share entry");
		String uuid = dto.getUuid();
		Validate.notEmpty(uuid, "Missing share entry uuid");
		ShareEntry share = find(actor, owner, uuid);
		/*
		 * Actually the owner have the right to update his own shareEntry. Is it
		 * really useful ?
		 */
		checkUpdatePermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.UPDATE, share,
				AuditLogEntryType.SHARE_ENTRY);
		share.setComment(dto.getComment());
		share = shareEntryBusinessService.update(share);
		log.setResourceUpdated(new ShareEntryMto(share));
		logEntryService.insert(log);
		return share;
	}

	@Override
	public ByteSource getThumbnailByteSource(Account actor, Account owner,
			String uuid, ThumbnailType kind) throws BusinessException {
		Validate.notEmpty(uuid, "Missing share entry uuid");
		ShareEntry share = find(actor, owner, uuid);
		checkThumbNailDownloadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		return documentEntryBusinessService.getThumbnailByteSource(share.
				getDocumentEntry(), kind);
	}

	@Override
	public ByteSource getByteSource(Account actor, Account owner, String uuid)
			throws BusinessException {
		Validate.notEmpty(uuid, "Missing share entry uuid");
		ShareEntry share = find(actor, owner, uuid);
		checkDownloadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		if (share.getDownloaded() <= 0) {
			ShareFileDownloadEmailContext context = new ShareFileDownloadEmailContext(share);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			notifierService.sendNotification(mail, true);
		}
		share = shareEntryBusinessService.updateDownloadCounter(share.getUuid());
		ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.DOWNLOAD, share,
				AuditLogEntryType.SHARE_ENTRY);
		String senderUuid = share.getEntryOwner().getLsUuid();
		log.addRelatedAccounts(senderUuid);
		logEntryService.insert(log);
		return documentEntryBusinessService.getByteSource(share
				.getDocumentEntry());
	}

	@Override
	public List<ShareEntry> findAllMyRecievedShareEntries(Account actor, Account owner) {
		preChecks(actor, owner);
		checkListPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, null);
		return shareEntryBusinessService.findAllMyRecievedShareEntries((User) owner);
	}

	@Override
	public Set<ShareEntry> create(Account actor, User owner, ShareContainer sc, ShareEntryGroup shareEntryGroup) {
		preChecks(actor, owner);
		Validate.notNull(sc);
		checkCreatePermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, null);
		Set<ShareEntry> entries = Sets.newHashSet();
		TimeUnitValueFunctionality functionality = functionalityService.getCollectedEmailsExpirationTimeFunctionality(owner.getDomain());
		Date contactExpirationDate = functionality.getContactExpirationDate();
		for (User recipient : sc.getShareRecipients()) {
			Set<ShareEntry> shares = Sets.newHashSet();
			for (DocumentEntry documentEntry : sc.getDocuments()) {
				ShareEntry createShare = shareEntryBusinessService.create(
						documentEntry, owner, recipient, sc.getExpiryCalendar(), shareEntryGroup, sc.getSharingNote());
				updateGuestExpiryDate(recipient);
				shares.add(createShare);
				recipientFavouriteRepository.incAndCreate(owner,
						recipient.getMail(), contactExpirationDate);
				ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.CREATE, createShare,
						AuditLogEntryType.SHARE_ENTRY);
				String recipientUuid = recipient.getLsUuid();
				log.addRelatedAccounts(recipientUuid);
				sc.addLog(log);
			}
			entries.addAll(shares);
			ShareNewShareEmailContext context = new ShareNewShareEmailContext(owner, recipient, shares, sc);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			sc.addMailContainer(mail);
		}
		// if there is no shares, ie anonymous shares only, there is no logs neither events.
		if (!sc.getLogs().isEmpty()) {
			// logs all logs and events in share container, create them and reset lists.
			logEntryService.insert(sc.getLogs());
			sc.getLogs().clear();
		}
		return entries;
	}

	private void updateGuestExpiryDate(User recipient) {
		// update guest account expiry date
		if (recipient.isGuest()) {

			// get new guest expiry date
			Calendar guestExpiryDate = Calendar.getInstance();
			AbstractDomain sourceDomain = ((Guest)recipient).getGuestSourceDomain();
			TimeUnitValueFunctionality guestFunctionality = functionalityService
					.getGuestsExpiration(sourceDomain);
			guestExpiryDate.add(guestFunctionality.toCalendarValue(),
					guestFunctionality.getMaxValue());

			Guest guest = guestRepository.findByMail(recipient.getLogin());
			guest.setExpirationDate(guestExpiryDate.getTime());
			try {
				guestRepository.update(guest);
			} catch (IllegalArgumentException e) {
				logger.error("Can't update expiration date of guest : "
						+ guest.getAccountRepresentation() + ":" + e.getMessage());
			} catch (BusinessException e) {
				logger.error("Can't update expiration date of guest : "
						+ guest.getAccountRepresentation() + ":" + e.getMessage());
			}
		}
	}

	@Override
	public List<String> findAllExpiredEntries(Account actor, Account owner) {
		preChecks(actor, owner);
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You do not have the right to use this method.");
		}
		return shareEntryBusinessService.findAllExpiredEntries();
	}

}
