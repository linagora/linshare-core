/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.Recipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.ShareNewShareAcknowledgementEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.ShareEntryResourceAccessControl;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryGroupService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.mto.CopyMto;

import com.google.common.collect.Sets;

public class ShareServiceImpl extends GenericServiceImpl<Account, ShareEntry> implements
		ShareService {

	private final FunctionalityReadOnlyService funcService;

	private final DocumentEntryService documentEntryService;

	private final UserService userService;

	private final GuestService guestService;

	private final AnonymousShareEntryService anonymousShareEntryService;

	private final ShareEntryService shareEntryService;

	private final NotifierService notifierService;

	private final EntryBusinessService entryBusinessService;

	private final MailBuildingService mailBuildingService;
	
	private TimeService timeService;

	// TODO : To be fix one day.
	@SuppressWarnings("unused")
	private final ShareExpiryDateService shareExpiryDateService;

	private final ShareEntryGroupService shareEntryGroupService;

	public ShareServiceImpl(
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final DocumentEntryService documentEntryService,
			final UserService userService,
			final GuestService guestService,
			final AnonymousShareEntryService anonymousShareEntryService,
			final ShareEntryService shareEntryService,
			final NotifierService notifierService,
			final EntryBusinessService entryBusinessService,
			final ShareEntryResourceAccessControl rac,
			final MailBuildingService mailBuildingService,
			final ShareExpiryDateService shareExpiryDateService,
			final ShareEntryGroupService shareEntryGroupService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			TimeService timeService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.funcService = functionalityReadOnlyService;
		this.documentEntryService = documentEntryService;
		this.userService = userService;
		this.guestService = guestService;
		this.anonymousShareEntryService = anonymousShareEntryService;
		this.shareEntryService = shareEntryService;
		this.notifierService = notifierService;
		this.entryBusinessService = entryBusinessService;
		this.mailBuildingService = mailBuildingService;
		this.shareExpiryDateService = shareExpiryDateService;
		this.shareEntryGroupService = shareEntryGroupService;
		this.timeService = timeService;
	}

	@Override
	public ShareEntry findForDownloadOrCopyRight(Account actor, Account owner, String uuid) throws BusinessException {
		return shareEntryService.findForDownloadOrCopyRight(actor, owner, uuid);
	}

	// TODO FMA - Refactoring shares
	@Override
	public Set<Entry> create(Account actor, User owner, ShareContainer shareContainer)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(shareContainer);
		checkCreatePermission(actor, owner, ShareEntry.class,
				BusinessErrorCode.FORBIDDEN, null);

		// Check functionalities

		// Check recipients
		transformRecipients(actor, owner, shareContainer);
		if (shareContainer.needAnonymousShares()) {
			if (!hasRightsToShareWithExternals(owner)) {
				throw new BusinessException(
						BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN,
						"You are not authorized to create anonymous share entries.");
			}
		}

		if (!shareContainer.canShare()) {
			throw new BusinessException(
					BusinessErrorCode.SHARE_MISSING_RECIPIENTS,
					"Can not share documents, missing recipients.");
		}

		// Check documents
		transformDocuments(actor, owner, shareContainer);

		// Check expiration date.
		TimeUnitValueFunctionality shareExpirationFunctionality = funcService
				.getDefaultShareExpiryTimeFunctionality(actor.getDomain());

		shareContainer.setExpiryDate(funcService.getDateValue(shareExpirationFunctionality,
				shareContainer.getExpiryDate(), BusinessErrorCode.SHARE_EXPIRY_DATE_INVALID));

		shareContainer.updateEncryptedStatus();

		// Creation
		ShareEntryGroup shareEntryGroup = new ShareEntryGroup(owner, shareContainer.getSubject());
		shareEntryGroup.setExpirationDate(shareContainer.getExpiryDate());

		BooleanValueFunctionality usdaFunc = funcService
				.getUndownloadedSharedDocumentsAlert(actor.getDomain());
		shareContainer.setEnableUSDA(usdaFunc.getFinalValue(shareContainer.getEnableUSDA()));
		if (shareContainer.getEnableUSDA()) {
			Date duration = getUndownloadedSharedDocumentsAlertDuration(
					actor,
					shareContainer.getNotificationDateForUSDA(),
					shareContainer.getExpiryDate());
			shareContainer.setNotificationDateForUSDA(duration);
			shareEntryGroup.setNotificationDate(duration);
		}

		shareEntryGroup = shareEntryGroupService.create(actor, shareEntryGroup);

		Set<Entry> entries = Sets.newHashSet();
		if (shareContainer.needAnonymousShares()) {
			entries.addAll(anonymousShareEntryService.create(actor, owner, shareContainer, shareEntryGroup));
		}
		entries.addAll(shareEntryService.create(actor, owner, shareContainer, shareEntryGroup));

		BooleanValueFunctionality acknowledgementFunc = funcService
				.getAcknowledgement(actor.getDomain());
		if (acknowledgementFunc.getFinalValue(shareContainer.isAcknowledgement())) {
			ShareNewShareAcknowledgementEmailContext context = new ShareNewShareAcknowledgementEmailContext(owner, shareContainer, entries);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			notifierService.sendNotification(mail);
		}
		// Notification
		notifierService.sendNotification(shareContainer.getMailContainers());
		return entries;
	}

	@Override
	public Date getUndownloadedSharedDocumentsAlertDuration(Account actor) {
		return getUndownloadedSharedDocumentsAlertDuration(actor, null, null);
	}

	/**
	 *
	 * @param owner
	 * @param userInputNotificationDate : user date
	 * @param expiryDate : could be null
	 * @return Date
	 */
	private Date getUndownloadedSharedDocumentsAlertDuration(Account owner, Date userInputNotificationDate, Date expiryDate) {
		Date defaultUsdaNotificationDate = null;
		IntegerValueFunctionality usdaDurationFunc = funcService
				.getUndownloadedSharedDocumentsAlertDuration(owner.getDomain());
		Integer usdaDuration = usdaDurationFunc.getValue();
		Date dateNow = timeService.dateNow();
		Calendar c = Calendar.getInstance();
		c.setTime(dateNow);
		int day = c.get(Calendar.DAY_OF_WEEK);
		int nbWeek = (day -2 + usdaDuration) / 5;
		int finalamount = usdaDuration + nbWeek * 2;
		c.add(Calendar.DATE, finalamount);
		defaultUsdaNotificationDate = setEndOfDayTime(c.getTime());
		if (usdaDurationFunc.getDelegationPolicy().getStatus()) {
			if (userInputNotificationDate != null) {
				userInputNotificationDate = setEndOfDayTime(userInputNotificationDate);
				if (userInputNotificationDate.before(dateNow)) {
					throw new BusinessException(
							BusinessErrorCode.SHARE_WRONG_USDA_NOTIFICATION_DATE_BEFORE,
							"Can not share documents, notification date for USDA is before today.");
				}
				if (expiryDate != null && userInputNotificationDate.after(expiryDate)) {
					throw new BusinessException(
							BusinessErrorCode.SHARE_WRONG_USDA_NOTIFICATION_DATE_AFTER,
							"Can not share documents, notification date for USDA is after the max date.");
				}
				defaultUsdaNotificationDate = userInputNotificationDate;
			}
		}
		return defaultUsdaNotificationDate;
	}

	private boolean hasRightsToShareWithExternals(User sender) {
		AbstractDomain domain = sender.getDomain();
		if (domain != null) {
			Functionality func = funcService
					.getAnonymousUrl(domain);
			return func.getActivationPolicy().getStatus();
		}
		return false;
	}

	private void transformRecipients(Account actor, User owner,
			ShareContainer shareContainer) throws BusinessException {

		// Initialize the shareContainer for guest if needed.
		if (owner.isGuest() && owner.isRestricted()) {
			List<AllowedContact> allowedContacts = guestService.load(actor, owner);
			shareContainer.addAllowedRecipients(allowedContacts);
		}
		BooleanValueFunctionality aufas = funcService.getAnonymousUrlForceAnonymousSharing(owner.getDomain());
		Boolean forceAnonymousSharing = aufas.getFinalValue(shareContainer.getForceAnonymousSharing());
		for (Recipient recipient : shareContainer.getRecipients()) {
			if (forceAnonymousSharing) {
				recipient.setLocale(owner.getExternalMailLocale());
				shareContainer.addAnonymousShareRecipient(recipient);
				continue;
			}
			// step 1
			if (addUserByUuid(shareContainer, recipient)) {
				// no need to look further.
				continue;
			}
			// step 2
			if (addUserByDomainAndMail(shareContainer, recipient, owner)) {
				// no need to look further.
				continue;
			}
			// step 3
			if (addUserByMail(shareContainer, recipient, owner)) {
				// no need to look further.
				continue;
			}
			// step 4
			// It did not find a account related to the recipient object.
			recipient.setLocale(owner.getExternalMailLocale());
			shareContainer.addAnonymousShareRecipient(recipient);
		}
	}

	private boolean addUserByUuid(ShareContainer shareContainer,
			Recipient recipient) throws BusinessException {
		String uuid = recipient.getUuid();
		if (uuid != null) {
			logger.debug("step1:looking into the database using : " + uuid);
			User user = userService.findByLsUuid(uuid);
			if (user != null) {
				logger.debug("step1:user found : "
						+ user.getAccountRepresentation());
				shareContainer.addShareRecipient(user);
				return true;
			}
		}
		return false;
	}

	private boolean addUserByDomainAndMail(ShareContainer shareContainer,
			Recipient recipient, Account owner) throws BusinessException {
		String mail = recipient.getMail();
		String domain = null;
		if (recipient.getDomain() != null) {
			domain = recipient.getDomain().getUuid();
		}
		if (mail != null && domain != null) {
			logger.debug("step2:looking into the database and the ldap using domain and mail : "
					+ domain + " : " + mail);
			try {
				User user = userService.findOrCreateUserWithDomainPolicies(
						domain, mail, owner.getDomainId());
				logger.debug("step2:user found : "
						+ user.getAccountRepresentation());
				shareContainer.addShareRecipient(user);
				return true;
			} catch (BusinessException e) {
				if (!e.getErrorCode().equals(BusinessErrorCode.USER_NOT_FOUND)) {
					throw e;
				}
			}
		}
		return false;
	}

	private boolean addUserByMail(ShareContainer shareContainer,
			Recipient recipient, Account owner) throws BusinessException {
		String mail = recipient.getMail();
		if (mail != null) {
			// step 3
			logger.debug("step3:looking into the database and the ldap using only mail : "
					+ mail);
			try {
				User user = userService.findOrCreateUserWithDomainPolicies(
						mail, owner.getDomainId());
				logger.debug("step3:user found : "
						+ user.getAccountRepresentation());
				shareContainer.addShareRecipient(user);
				return true;
			} catch (BusinessException e) {
				if (!e.getErrorCode().equals(BusinessErrorCode.USER_NOT_FOUND)) {
					throw e;
				}
			}
		}
		return false;
	}

	private Date setEndOfDayTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	protected void transformDocuments(Account actor, User owner,
			ShareContainer shareContainer) throws BusinessException {
		for (String uuid : shareContainer.getDocumentUuids()) {
			DocumentEntry doc = documentEntryService.find(actor, owner, uuid);
			shareContainer.addDocumentEntry(doc);
		}
	}

	@Override
	public DocumentEntry deleteAllShareEntries(Account actor, Account owner,
			String docEntryUuid, LogActionCause actionCause) throws BusinessException {
		DocumentEntry entry = documentEntryService.find(actor, owner,
				docEntryUuid);
		List<AnonymousShareEntry> list1 = entryBusinessService.findAllMyAnonymousShareEntries((User)owner, entry);
		List<ShareEntry> list2 = entryBusinessService.findAllMyShareEntries((User)owner, entry);
		for (AnonymousShareEntry share : list1) {
			anonymousShareEntryService.delete(actor, owner, share.getUuid());
		}
		for (ShareEntry share : list2) {
			shareEntryService.delete(actor, owner, share.getUuid(), actionCause);
		}
		return documentEntryService.find(actor, owner, entry.getUuid());
	}

	@Override
	public Entry delete(Account actor, Account owner, String entryUuid) throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(entryUuid);
		// TODO : To be improved.
		Entry entry = entryBusinessService.find(entryUuid);
		if (entry == null) {
			String msg = "Can not find the current entry : " + entryUuid;
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.SHARE_NOT_FOUND, msg);
		}
		if (entry.getEntryType().equals(EntryType.SHARE)) {
			shareEntryService.delete(actor, owner, entryUuid, LogActionCause.UNDEFINED);
		} else if (entry.getEntryType().equals(EntryType.ANONYMOUS_SHARE)) {
			anonymousShareEntryService.delete(actor, owner, entryUuid);
		} else {
			String msg = "Can not find the current entry : " + entryUuid;
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.SHARE_NOT_FOUND, msg);
		}
		return entry;
	}

	@Override
	public ShareEntry delete(Account actor, Account owner, ShareEntry share, LogActionCause cause)
			throws BusinessException {
		return shareEntryService.delete(actor, owner, share.getUuid(), cause);
	}

	@Override
	public ShareEntry markAsCopied(Account actor, Account owner, String uuid, CopyMto copiedTo) throws BusinessException {
		return shareEntryService.markAsCopied(actor, owner, uuid, copiedTo);
	}
}
