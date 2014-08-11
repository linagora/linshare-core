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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class ShareEntryServiceImpl extends GenericEntryService implements ShareEntryService {

	private static final Logger logger = LoggerFactory.getLogger(ShareEntryServiceImpl.class);

	private final GuestRepository guestRepository;

	private final FunctionalityReadOnlyService functionalityService;

	private final ShareEntryBusinessService shareEntryBusinessService;

	private final ShareExpiryDateService shareExpiryDateService;

	private final LogEntryService logEntryService;

	private final DocumentEntryService documentEntryService ;

	private final DocumentEntryBusinessService documentEntryBusinessService ;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final FavouriteRepository<String, User, RecipientFavourite> recipientFavouriteRepository;

public ShareEntryServiceImpl(GuestRepository guestRepository,
			FunctionalityReadOnlyService functionalityService,
			ShareEntryBusinessService shareEntryBusinessService,
			ShareExpiryDateService shareExpiryDateService,
			LogEntryService logEntryService,
			DocumentEntryService documentEntryService,
			DocumentEntryBusinessService documentEntryBusinessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			FavouriteRepository<String, User, RecipientFavourite> recipientFavouriteRepository
			) {
		super();
		this.guestRepository = guestRepository;
		this.functionalityService = functionalityService;
		this.shareEntryBusinessService = shareEntryBusinessService;
		this.shareExpiryDateService = shareExpiryDateService;
		this.logEntryService = logEntryService;
		this.documentEntryService = documentEntryService;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.recipientFavouriteRepository = recipientFavouriteRepository;
	}

	@Override
	public ShareEntry find(User actor, User owner, String uuid) throws BusinessException {
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
		checkReadPermission(actor, entry, BusinessErrorCode.SHARE_ENTRY_FORBIDDEN);
		return entry;
	}

	@Override
	public void delete(Account actor, String shareUuid) throws BusinessException {
		ShareEntry share = shareEntryBusinessService.find(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		deleteShare(actor, share);
	}


	@Override
	public void deleteShare(Account actor, ShareEntry share) throws BusinessException {
		if (share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor) || actor.hasSuperAdminRole()
				|| actor.hasSystemAccountRole()) {
			ShareLogEntry logEntry = new ShareLogEntry(actor, share, LogAction.SHARE_DELETE, "Delete a sharing");
			logEntryService.create(logEntry);

			logger.info("delete share : " + share.getUuid());
			shareEntryBusinessService.delete(share);

			if (share.getEntryOwner().equals(actor) || actor.hasSuperAdminRole() || actor.hasSystemAccountRole()) {
				MailContainerWithRecipient mail = mailBuildingService.buildSharedDocDeleted(actor, share);
				notifierService.sendNotification(mail);
			}

		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + share.getUuid());
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"You are not authorized to delete this share, it does not belong to you.");
		}
	}


	@Override
	public void deleteShare(SystemAccount actor, ShareEntry share) throws BusinessException {
		if(share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor) || actor.hasSuperAdminRole() || actor.hasSystemAccountRole()) {
			ShareLogEntry logEntry = new ShareLogEntry(actor, share, LogAction.SHARE_DELETE, "Delete a sharing");
			logEntryService.create(logEntry);
			logger.info("delete share : " + share.getUuid());
			shareEntryBusinessService.delete(share);
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + share.getUuid());
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to delete this share, it does not belong to you.");
		}
	}


	@Override
	public DocumentEntry copyDocumentFromShare(String shareUuid, User actor) throws BusinessException {

		ShareEntry share = shareEntryBusinessService.find(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}

		if(share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor)) {
			 //log the copy
			ShareLogEntry logEntryShare = ShareLogEntry.hasCopiedAShare(actor, share);
			logEntryService.create(logEntryShare);

			DocumentEntry newDocumentEntry = documentEntryService.duplicateDocumentEntry(actor, share.getDocumentEntry().getUuid());

			ShareLogEntry logEntry = new ShareLogEntry(actor, share, LogAction.SHARE_DELETE, "Remove a received sharing (Copy of a sharing)"); 
			logEntryService.create(logEntry);
			logger.info("delete share : " + share.getUuid());

			if (share.getDownloaded() < 1) {
				MailContainerWithRecipient mail = mailBuildingService.buildRegisteredDownload(share);
				notifierService.sendNotification(mail);
			}
			shareEntryBusinessService.delete(share);

			return newDocumentEntry;
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to copy this share, it does not belong to you.");
		}

	}

	@Override
	public ShareEntry update(User actor, ShareEntry dto) throws BusinessException {
		String shareUuid = dto.getUuid();
		Validate.notEmpty(shareUuid);
		ShareEntry share = shareEntryBusinessService.find(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		if(!share.getRecipient().equals(actor)) {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to update comment on this share, it does not belong to you.");
		}
		return shareEntryBusinessService.update(share);
	}

	@Override
	public InputStream getThumbnailStream(User actor, String shareEntryUuid) throws BusinessException {
		try {
			ShareEntry shareEntry = find(actor, actor, shareEntryUuid);
			if (!shareEntry.getRecipient().equals(actor)) {
				throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to get thumbnail for this share.");
			}
			return documentEntryBusinessService.getDocumentThumbnailStream(shareEntry.getDocumentEntry());
		} catch (BusinessException e) {
			logger.error("Can't find share for thumbnail : " + shareEntryUuid + " : " + e.getMessage());
			throw e;
		}
	}

	@Override
	public InputStream getStream(User actor, String shareEntryUuid) throws BusinessException {
		try {
			ShareEntry shareEntry = find(actor, actor, shareEntryUuid);
			if (!shareEntry.getRecipient().equals(actor)) {
				throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to get this share.");
			}
			ShareLogEntry logEntryActor = ShareLogEntry.hasDownloadedAShare(actor, shareEntry);
			ShareLogEntry logEntryTarget = ShareLogEntry.aShareWasDownloaded(actor, shareEntry);
			logEntryService.create(logEntryActor);
			logEntryService.create(logEntryTarget);
			if (shareEntry.getDownloaded() <=0) {
				MailContainerWithRecipient mail = mailBuildingService.buildRegisteredDownload(shareEntry);
				notifierService.sendNotification(mail);
			}
			shareEntryBusinessService.updateDownloadCounter(shareEntry.getUuid());
			return documentEntryBusinessService.getDocumentStream(shareEntry.getDocumentEntry());
		} catch (BusinessException e) {
			logger.error("Can't find share for thumbnail : " + shareEntryUuid + " : " + e.getMessage());
			throw e;
		}
	}


	@Override
	public List<ShareEntry> findAllMyShareEntries(Account actor, User owner) {
		// TODO check permissions
		return shareEntryBusinessService.findAllMyShareEntries(owner);
	}


	@Override
	public void sendDocumentEntryUpdateNotification(ShareEntry shareEntry, String friendlySize, String originalFileName) {
		try {
			MailContainerWithRecipient mail = mailBuildingService.buildSharedDocUpdated(shareEntry, originalFileName, friendlySize);
			notifierService.sendNotification(mail);
		} catch (BusinessException e) {
			logger.error("Error while trying to notify document update ", e);
		}
	}

	@Override
	public void create(Account actor, User owner, ShareContainer sc) {
		Date expiryDate = sc.getExpiryDate();
		if (expiryDate == null) {
			expiryDate = shareExpiryDateService
					.computeMinShareExpiryDateOfList(sc.getDocuments(), owner);
		}
		for (User recipient : sc.getShareRecipients()) {
			MailContainer mailContainer = new MailContainer(
					recipient.getLocale(), sc.getMessage(), sc.getSubject());
			Set<ShareEntry> shares = Sets.newHashSet();
			for (DocumentEntry documentEntry : sc.getDocuments()) {
				ShareEntry createShare = shareEntryBusinessService.create(documentEntry,
						owner, recipient, expiryDate);
				updateGuestExpiryDate(recipient, owner);
				shares.add(createShare);
				recipientFavouriteRepository.incAndCreate(owner, recipient.getMail());
				logEntryService.create(new ShareLogEntry(owner, createShare, LogAction.FILE_SHARE, "Sharing of a file"));
				logEntryService.create(new ShareLogEntry(
						recipient, LogAction.SHARE_RECEIVED, "Receiving a shared file", createShare, owner));
			}
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
	}

	private void updateGuestExpiryDate(User recipient, User sender) {
		// update guest account expiry date
		if (recipient.isGuest()) {

			// get new guest expiry date
			Calendar guestExpiryDate = Calendar.getInstance();
			TimeUnitValueFunctionality guestFunctionality = functionalityService.getGuestAccountExpiryTimeFunctionality(sender.getDomain());
			guestExpiryDate.add(guestFunctionality.toCalendarUnitValue(), guestFunctionality.getValue());

			Guest guest = guestRepository.findByMail(recipient.getLogin());
			guest.setExpirationDate(guestExpiryDate.getTime());
			try {
				guestRepository.update(guest);
			} catch (IllegalArgumentException e) {
				logger.error("Can't update expiration date of guest : " + guest.getAccountReprentation() + ":" + e.getMessage());
			} catch (BusinessException e) {
				logger.error("Can't update expiration date of guest : " + guest.getAccountReprentation() + ":" + e.getMessage());
			}
		}
	}

	@Override
	protected boolean isAuthorized(Account actor, Account owner,
			PermissionType permission, Object entry, String resourceName) {
		if (entry != null) {
			ShareEntry s = (ShareEntry) entry;
			if (actor.equals(s.getRecipient())) {
				return true;
			}
		}
		return super
				.isAuthorized(actor, owner, permission, entry, resourceName);
	}

	@Override
	protected boolean hasReadPermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.SHARES_GET);
	}

	@Override
	protected boolean hasListPermission(Account actor) {
		return this.hasPermission(actor,
				TechnicalAccountPermissionType.SHARES_LIST);
	}

	@Override
	protected boolean hasDeletePermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.SHARES_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.SHARES_CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.SHARES_UPDATE);
	}
}
