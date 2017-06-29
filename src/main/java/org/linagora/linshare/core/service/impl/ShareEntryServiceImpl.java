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
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
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
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.ShareEntryResourceAccessControl;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.mongo.entities.EventNotification;
import org.linagora.linshare.mongo.entities.logs.DocumentEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.ShareEntryMto;

import com.google.common.collect.Sets;

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
			ShareEntryResourceAccessControl rac) {
		super(rac);
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
	public void delete(Account actor, Account owner, String uuid, LogActionCause cause)
			throws BusinessException {
		Validate.notEmpty(uuid, "Missing share entry uuid");
		ShareEntry share = find(actor, owner, uuid);
		checkDeletePermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		logger.info("Share deleted : " + share.getUuid());
		shareEntryBusinessService.delete(share);
		ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.DELETE, share,
				AuditLogEntryType.SHARE_ENTRY);
		if (cause != null) {
			log.setCause(cause);
		}
		if (share.getRecipient().equals(owner)) {
			// If the modified account (aka owner parameter) is the recipient of this share.
			// We does not need to send him a notification.
			// But we need to warn the sender
			String senderUuid = share.getEntryOwner().getLsUuid();
			log.addRelatedAccounts(senderUuid);
			EventNotification event = new EventNotification(log, senderUuid);
			logEntryService.insert(log, event);
			// Mail to the sender ?
		} else {
			// The sender is deleting the current share, we need to warn the recipient.
			String recipientUuid = share.getRecipient().getLsUuid();
			log.addRelatedAccounts(recipientUuid);
			EventNotification event = new EventNotification(log, recipientUuid);
			logEntryService.insert(log, event);
			EmailContext context = new ShareFileShareDeletedEmailContext(share);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			notifierService.sendNotification(mail);
		}
	}

	@Override
	public DocumentEntry copy(Account actor, Account owner, String shareUuid)
			throws BusinessException {
		Validate.notEmpty(shareUuid, "Missing share entry uuid");
		// step1 : find the resource, and it does the preChecks(actor, owner);
		ShareEntry share = find(actor, owner, shareUuid);
		checkDownloadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		/*
		 * This check already exists in DocumentEntry rac, but we do it it to avoid to go deeper in this method (performance).
		 */
		if (!((User) owner).getCanUpload()) {
			throw new BusinessException(BusinessErrorCode.NO_UPLOAD_RIGHTS_FOR_ACTOR, "Actor do not have upload rights.");
		}
		// Check if we have the right to download the specified document entry
		DocumentEntry documentEntry = null;
		// step2 : copy the resource
		Calendar expiryTime = functionalityService.getDefaultFileExpiryTime(owner.getDomain());
		documentEntry = documentEntryBusinessService.copyFromShareEntry(owner, share, expiryTime);
		// step3 : log the document creation
		DocumentEntryAuditLogEntry docLog = new DocumentEntryAuditLogEntry(actor, owner, documentEntry, LogAction.CREATE);
		docLog.setCause(LogActionCause.COPY);
		docLog.setFromResourceUuid(shareUuid);
		// step4 : remove the share
		logger.info("delete share : " + share.getUuid());
		// step 5 : notification
		if (share.getDownloaded() < 1) {
			ShareFileDownloadEmailContext context = new ShareFileDownloadEmailContext(share);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			notifierService.sendNotification(mail);
		}
		// The share is now useless. We can delete it.
		shareEntryBusinessService.delete(share);
		// step6 : log the share deletion
		ShareEntryAuditLogEntry shareLog = new ShareEntryAuditLogEntry(actor, owner, LogAction.DELETE, share,
				AuditLogEntryType.SHARE_ENTRY);
		String senderUuid = share.getEntryOwner().getLsUuid();
		shareLog.addRelatedAccounts(senderUuid);
		shareLog.setCause(LogActionCause.COPY);
		// step create an event to notify the sender of share deletion.
		EventNotification event = new EventNotification(shareLog, senderUuid);
		logEntryService.insert(docLog);
		logEntryService.insert(shareLog, event);
		return documentEntry;
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
	public InputStream getThumbnailStream(Account actor, Account owner,
			String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing share entry uuid");
		ShareEntry share = find(actor, owner, uuid);
		checkThumbNailDownloadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		return documentEntryBusinessService.getDocumentThumbnailStream(share
				.getDocumentEntry());
	}

	@Override
	public InputStream getStream(Account actor, Account owner, String uuid)
			throws BusinessException {
		Validate.notEmpty(uuid, "Missing share entry uuid");
		ShareEntry share = find(actor, owner, uuid);
		checkDownloadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		if (share.getDownloaded() <= 0) {
			ShareFileDownloadEmailContext context = new ShareFileDownloadEmailContext(share);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			notifierService.sendNotification(mail);
		}
		share = shareEntryBusinessService.updateDownloadCounter(share.getUuid());
		ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.DOWNLOAD, share,
				AuditLogEntryType.SHARE_ENTRY);
		String senderUuid = share.getEntryOwner().getLsUuid();
		log.addRelatedAccounts(senderUuid);
		EventNotification event = new EventNotification(log, senderUuid);
		logEntryService.insert(log, event);
		return documentEntryBusinessService.getDocumentStream(share
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
		for (User recipient : sc.getShareRecipients()) {
			Set<ShareEntry> shares = Sets.newHashSet();
			for (DocumentEntry documentEntry : sc.getDocuments()) {
				ShareEntry createShare = shareEntryBusinessService.create(
						documentEntry, owner, recipient, sc.getExpiryCalendar(), shareEntryGroup, sc.getSharingNote());
				updateGuestExpiryDate(recipient,  (User) recipient.getOwner());
				shares.add(createShare);
				recipientFavouriteRepository.incAndCreate(owner,
						recipient.getMail());
				ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.CREATE, createShare,
						AuditLogEntryType.SHARE_ENTRY);
				String recipientUuid = recipient.getLsUuid();
				log.addRelatedAccounts(recipientUuid);
				sc.addLog(log);
				sc.addEvent(new EventNotification(log, recipientUuid));
			}
			entries.addAll(shares);
			ShareNewShareEmailContext context = new ShareNewShareEmailContext(owner, recipient, shares, sc);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			sc.addMailContainer(mail);
		}
		// if there is no shares, ie anonymous shares only, there is no logs neither events.
		if (!sc.getLogs().isEmpty()) {
			// logs all logs and events in share container, create them and reset lists.
			logEntryService.insert(sc.getLogs(), sc.getEvents());
			sc.getLogs().clear();
			sc.getEvents().clear();
		}
		return entries;
	}

	private void updateGuestExpiryDate(User recipient, User recipientOwner) {
		// update guest account expiry date
		if (recipient.isGuest()) {

			// get new guest expiry date
			Calendar guestExpiryDate = Calendar.getInstance();
			TimeUnitValueFunctionality guestFunctionality = functionalityService
					.getGuestsExpiration(recipientOwner.getDomain());
			guestExpiryDate.add(guestFunctionality.toCalendarValue(),
					guestFunctionality.getValue());

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
