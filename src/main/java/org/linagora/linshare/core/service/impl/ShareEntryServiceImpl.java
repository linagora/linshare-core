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
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.ShareEntryResourceAccessControl;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class ShareEntryServiceImpl extends GenericEntryServiceImpl<Account, ShareEntry>
		implements ShareEntryService {

	private static final Logger logger = LoggerFactory
			.getLogger(ShareEntryServiceImpl.class);

	private final GuestRepository guestRepository;

	private final FunctionalityReadOnlyService functionalityService;

	private final ShareEntryBusinessService shareEntryBusinessService;

	private final LogEntryService logEntryService;

	private final DocumentEntryService documentEntryService;

	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final FavouriteRepository<String, User, RecipientFavourite> recipientFavouriteRepository;

	public ShareEntryServiceImpl(
			GuestRepository guestRepository,
			FunctionalityReadOnlyService functionalityService,
			ShareEntryBusinessService shareEntryBusinessService,
			LogEntryService logEntryService,
			DocumentEntryService documentEntryService,
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
		this.documentEntryService = documentEntryService;
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
			logger.error("Current actor " + actor.getAccountReprentation()
					+ " is looking for a misssing share entry (" + uuid
					+ ") owned by : " + owner.getAccountReprentation());
			String message = "Can not find share entry with uuid : " + uuid;
			throw new BusinessException(
					BusinessErrorCode.SHARE_ENTRY_NOT_FOUND, message);
		}
		checkReadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, null);
		return entry;
	}

	@Override
	public void delete(Account actor, Account owner, String uuid)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Missing share entry uuid");
		ShareEntry share = find(actor, owner, uuid);
		checkDeletePermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		ShareLogEntry logEntry = new ShareLogEntry(owner, share,
				LogAction.SHARE_DELETE, "Delete a sharing");
		logEntryService.create(logEntry);
		logger.info("Share deleted : " + share.getUuid());
		shareEntryBusinessService.delete(share);
		// No need to send a notification to the recipient if he is the current
		// owner.
		if (!share.getRecipient().equals(owner)) {
			MailContainerWithRecipient mail = mailBuildingService
					.buildSharedDocDeleted(share.getRecipient(), share);
			notifierService.sendNotification(mail);
		}
	}

	@Override
	public DocumentEntry copy(Account actor, Account owner, String shareUuid)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(shareUuid, "Missing share entry uuid");
		// step1 : find the resource
		ShareEntry share = find(actor, owner, shareUuid);
		checkDownloadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		/*
		 * Already exists in DocumentEntry rac, but here to avoid to go deeper in the method.
		 */
		if (!((User) owner).getCanUpload()) {
			throw new BusinessException(BusinessErrorCode.NO_UPLOAD_RIGHTS_FOR_ACTOR, "Actor do not have upload rights.");
		}
		// step2 : log the copy
		ShareLogEntry logEntryShare = ShareLogEntry.hasCopiedAShare(owner,
				share);
		logEntryService.create(logEntryShare);

		// step3 : copy the resource
		InputStream stream = getStream(actor,
				owner, share.getUuid());
		DocumentEntry newDocumentEntry = documentEntryService.create(actor,
				owner, stream, share.getName(), share.getComment(), false, null);

		// step4 : remove the share
		ShareLogEntry logEntry = new ShareLogEntry(owner, share,
				LogAction.SHARE_DELETE,
				"Remove a received sharing (Copy of a sharing)");
		logEntryService.create(logEntry);
		logger.info("delete share : " + share.getUuid());

		// step 5 : notification
		if (share.getDownloaded() < 1) {
			MailContainerWithRecipient mail = mailBuildingService
					.buildRegisteredDownload(share);
			notifierService.sendNotification(mail);
		}
		// The share is now useless. We can delete it.
		delete(actor, owner, shareUuid);
		return newDocumentEntry;
	}

	@Override
	public ShareEntry update(Account actor, Account owner, ShareEntry dto)
			throws BusinessException {
		preChecks(actor, owner);
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
		share.setComment(dto.getComment());
		return shareEntryBusinessService.update(share);
	}

	@Override
	public InputStream getThumbnailStream(Account actor, Account owner,
			String uuid) throws BusinessException {
		preChecks(actor, owner);
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
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Missing share entry uuid");
		ShareEntry share = find(actor, owner, uuid);
		checkDownloadPermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.SHARE_ENTRY_FORBIDDEN, share);
		ShareLogEntry logEntryActor = ShareLogEntry.hasDownloadedAShare(owner,
				share);
		ShareLogEntry logEntryTarget = ShareLogEntry.aShareWasDownloaded(owner,
				share);
		logEntryService.create(logEntryActor);
		logEntryService.create(logEntryTarget);
		if (share.getDownloaded() <= 0) {
			MailContainerWithRecipient mail = mailBuildingService
					.buildRegisteredDownload(share);
			notifierService.sendNotification(mail);
		}
		shareEntryBusinessService.updateDownloadCounter(share.getUuid());
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
			MailContainer mailContainer = new MailContainer(
					recipient.getExternalMailLocale(), sc.getMessage(), sc.getSubject());
			Set<ShareEntry> shares = Sets.newHashSet();
			for (DocumentEntry documentEntry : sc.getDocuments()) {
				ShareEntry createShare = shareEntryBusinessService.create(
						documentEntry, owner, recipient, sc.getExpiryCalendar(), shareEntryGroup);
				updateGuestExpiryDate(recipient,  (User) recipient.getOwner());
				shares.add(createShare);
				recipientFavouriteRepository.incAndCreate(owner,
						recipient.getMail());
				ShareLogEntry logEntry = new ShareLogEntry(owner, createShare,
						LogAction.FILE_SHARE, "Sharing of a file");
				if(sc.getEnableUSDA()){
					logEntry = new ShareLogEntry(owner, createShare,
							LogAction.FILE_SHARE_WITH_ALERT_FOR_USD, "Anonymous sharing of a file");
				}
				logEntryService.create(logEntry);
				logEntryService.create(new ShareLogEntry(recipient,
						LogAction.SHARE_RECEIVED, "Receiving a shared file",
						createShare, owner));
			}
			entries.addAll(shares);
			MailContainerWithRecipient mail = null;
			if (sc.isEncrypted()) {
				mail = mailBuildingService.buildNewSharingCyphered(owner,
						mailContainer, recipient, shares);
			} else {
				mail = mailBuildingService.buildNewSharing(owner,
						mailContainer, recipient, shares);
			}
			sc.addMailContainer(mail);
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
						+ guest.getAccountReprentation() + ":" + e.getMessage());
			} catch (BusinessException e) {
				logger.error("Can't update expiration date of guest : "
						+ guest.getAccountReprentation() + ":" + e.getMessage());
			}
		}
	}
}
