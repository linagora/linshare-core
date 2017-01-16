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

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.UploadPropositionBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.LanguageEnumValueFunctionality;
import org.linagora.linshare.core.domain.entities.UploadProposition;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadPropositionService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPropositionServiceImpl implements UploadPropositionService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionServiceImpl.class);

	private final UploadPropositionBusinessService uploadPropositionBusinessService;

	private final DomainBusinessService domainBusinessService;

	private final UploadRequestService uploadRequestService;

	private final UploadRequestUrlService uploadRequestUrlService;

	private final UserService userService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final MailBuildingService mailBuildingService;

	private final NotifierService notifierService;

	public UploadPropositionServiceImpl(
			final UploadPropositionBusinessService uploadPropositionBusinessService,
			final DomainBusinessService domainBusinessService,
			final UploadRequestService uploadRequestService,
			final UploadRequestUrlService uploadRequestUrlService,
			final UserService userService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService) {
		super();
		this.uploadPropositionBusinessService = uploadPropositionBusinessService;
		this.domainBusinessService = domainBusinessService;
		this.uploadRequestService = uploadRequestService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.userService = userService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
	}

	@Override
	public UploadProposition create(UploadProposition proposition,
			UploadPropositionActionType action) throws BusinessException {
		Validate.notNull(proposition, "UploadProposition must be set.");

		AbstractDomain rootDomain = domainBusinessService.getUniqueRootDomain();
		proposition.setDomain(rootDomain);

		UploadProposition created;
		boolean accept = action.equals(UploadPropositionActionType.ACCEPT);
		if (accept) {
			proposition.setStatus(UploadPropositionStatus.SYSTEM_ACCEPTED);
		}
		created = uploadPropositionBusinessService.create(proposition);
		User owner = null;
		try {
			owner = userService.findOrCreateUser(proposition
					.getRecipientMail(), StringUtils.defaultString(
					proposition.getDomainSource(),
					rootDomain.getUuid()));
		} catch (BusinessException e) {
			logger.error("The recipient of the upload proposition can't be found : "
					+ created.getUuid()
					+ ": "
					+ proposition.getRecipientMail());
			return null;
		}
		if (accept) {
			acceptHook(owner, created);
		}
		MailContainerWithRecipient mail = mailBuildingService.buildCreateUploadProposition(owner, proposition);
		notifierService.sendNotification(mail);
		return created;
	}

	@Override
	public void delete(Account actor, UploadProposition prop)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		uploadPropositionBusinessService.delete(prop);
	}

	@Override
	public UploadProposition find(Account actor, String uuid)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		return uploadPropositionBusinessService.findByUuid(uuid);
	}

	@Override
	public List<UploadProposition> findAll(User actor) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		return uploadPropositionBusinessService.findAllByMail(actor.getMail());
	}

	@Override
	public void checkIfValidRecipient(Account actor, String mail,
			String domainId) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(mail, "Mail must be set.");
		if (!actor.hasUploadPropositionRole()) {
			logger.equals(actor.getAccountRepresentation()
					+ " is using an unauthorized api");
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"You are not authorized to use this method.");
		}
		if (domainId == null) {
			domainId = LinShareConstants.rootDomainIdentifier;
		}
		try {
			userService.findOrCreateUserWithDomainPolicies(mail, domainId);
		} catch (BusinessException ex) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Recipient not found.");
		}
	}

	@Override
	public void accept(User actor, UploadProposition e)
			throws BusinessException {
		logger.debug("Accepting proposition: " + e.getUuid());
		e.setStatus(UploadPropositionStatus.USER_ACCEPTED);
		e = uploadPropositionBusinessService.update(e);
		acceptHook(actor, e);
	}

	@Override
	public void reject(User actor, UploadProposition e)
			throws BusinessException {
		logger.debug("Rejecting proposition: " + e.getUuid());
		e.setStatus(UploadPropositionStatus.USER_REJECTED);
		uploadPropositionBusinessService.update(e);
		MailContainerWithRecipient mail = mailBuildingService
				.buildRejectUploadProposition(actor, e);
		notifierService.sendNotification(mail);
	}

	public void acceptHook(User owner, UploadProposition created)
			throws BusinessException {
		UploadRequest req = new UploadRequest();
		req.setUploadPropositionRequestUuid(created.getUuid());
		getDefaultValue(owner, req);// get value default from domain
		Contact contact = new Contact(created.getMail());
		uploadRequestService.createRequest(owner, owner, req, contact,
				created.getSubject(), created.getBody(), null);
	}

	public void getDefaultValue(User owner, UploadRequest req)
			throws BusinessException {
		AbstractDomain domain = owner.getDomain();

		TimeUnitValueFunctionality expiryDateFunc = functionalityReadOnlyService
				.getUploadRequestExpiryTimeFunctionality(domain);

		if (expiryDateFunc.getActivationPolicy().getStatus()) {
			logger.debug("expiryDateFunc is activated");
			if (expiryDateFunc.getDelegationPolicy() != null
					&& expiryDateFunc.getDelegationPolicy().getStatus()) {
				logger.debug("expiryDateFunc has a delegation policy");
			}
			Calendar c = Calendar.getInstance();
			c.add(expiryDateFunc.toCalendarValue(),
					expiryDateFunc.getValue());
			req.setExpiryDate(c.getTime());
		}

		SizeUnitValueFunctionality maxDepositSizeFunc = functionalityReadOnlyService
				.getUploadRequestMaxDepositSizeFunctionality(domain);

		if (maxDepositSizeFunc.getActivationPolicy().getStatus()) {
			logger.debug("maxDepositSizeFunc is activated");
			if (maxDepositSizeFunc.getDelegationPolicy() != null
					&& maxDepositSizeFunc.getDelegationPolicy().getStatus()) {
				logger.debug("maxDepositSizeFunc has a delegation policy");
			}
			long maxDepositSize = ((FileSizeUnitClass) maxDepositSizeFunc
					.getUnit()).getPlainSize(maxDepositSizeFunc.getValue());
			req.setMaxDepositSize(maxDepositSize);
		}

		IntegerValueFunctionality maxFileCountFunc = functionalityReadOnlyService
				.getUploadRequestMaxFileCountFunctionality(domain);

		if (maxFileCountFunc.getActivationPolicy().getStatus()) {
			logger.debug("maxFileCountFunc is activated");
			if (maxFileCountFunc.getDelegationPolicy() != null
					&& maxFileCountFunc.getDelegationPolicy().getStatus()) {
				logger.debug("maxFileCountFunc has a delegation policy");
			}
			int maxFileCount = maxFileCountFunc.getValue();
			req.setMaxFileCount(maxFileCount);
		}

		SizeUnitValueFunctionality maxFileSizeFunc = functionalityReadOnlyService
				.getUploadRequestMaxFileSizeFunctionality(domain);

		if (maxFileSizeFunc.getActivationPolicy().getStatus()) {
			logger.debug("maxFileSizeFunc is activated");
			if (maxFileSizeFunc.getDelegationPolicy() != null
					&& maxFileSizeFunc.getDelegationPolicy().getStatus()) {
				logger.debug("maxFileSizeFunc has a delegation policy");
			}
			long maxFileSize = ((FileSizeUnitClass) maxFileSizeFunc.getUnit())
					.getPlainSize(maxFileSizeFunc.getValue());
			req.setMaxFileSize(maxFileSize);
		}

		LanguageEnumValueFunctionality notificationLangFunc = functionalityReadOnlyService
				.getUploadRequestNotificationLanguageFunctionality(domain);

		if (notificationLangFunc.getActivationPolicy().getStatus()) {
			logger.debug("notificationLangFunc is activated");
			if (notificationLangFunc.getDelegationPolicy() != null
					&& notificationLangFunc.getDelegationPolicy().getStatus()) {
				logger.debug("notificationLangFunc has a delegation policy");
			}
			String locale = notificationLangFunc.getValue().getTapestryLocale();
			req.setLocale(locale);
		}

		BooleanValueFunctionality secureUrlFunc = functionalityReadOnlyService
				.getUploadRequestSecureUrlFunctionality(domain);

		if (secureUrlFunc.getActivationPolicy().getStatus()) {
			logger.debug("secureUrlFunc is activated");
			if (secureUrlFunc.getDelegationPolicy() != null
					&& secureUrlFunc.getDelegationPolicy().getStatus()) {
				logger.debug("secureUrlFunc has a delegation policy");
			}
			req.setSecured(secureUrlFunc.getValue());
		}

		BooleanValueFunctionality canDeleteFunc = functionalityReadOnlyService
				.getUploadRequestCandDeleteFileFunctionality(domain);

		if (canDeleteFunc.getActivationPolicy().getStatus()) {
			logger.debug("depositFunc is activated");
			if (canDeleteFunc.getDelegationPolicy() != null
					&& canDeleteFunc.getDelegationPolicy().getStatus()) {
				logger.debug("depositFunc has a delegation policy");
			}
			req.setCanDelete(canDeleteFunc.getValue());
		}

		BooleanValueFunctionality canCloseFunc = functionalityReadOnlyService
				.getUploadRequestCanCloseFunctionality(domain);

		if (canCloseFunc.getActivationPolicy().getStatus()) {
			logger.debug("canCloseFunc  is activated");
			if (canCloseFunc.getDelegationPolicy() != null
					&& canCloseFunc.getDelegationPolicy().getStatus()) {
				logger.debug("canCloseFunc  has a delegation policy");
			}
			req.setCanClose(canCloseFunc.getValue());
		}
	}
}
