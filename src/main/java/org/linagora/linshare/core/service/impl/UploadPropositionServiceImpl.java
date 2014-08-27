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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.UploadPropositionBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.UploadProposition;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
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

	public UploadPropositionServiceImpl(
			final UploadPropositionBusinessService uploadPropositionBusinessService,
			final DomainBusinessService domainBusinessService,
			final UploadRequestService uploadRequestService,
			final UploadRequestUrlService uploadRequestUrlService,
			final UserService userService,
			final FunctionalityReadOnlyService functionalityReadOnlyService) {
		super();
		this.uploadPropositionBusinessService = uploadPropositionBusinessService;
		this.domainBusinessService = domainBusinessService;
		this.uploadRequestService = uploadRequestService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.userService = userService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	@Override
	public UploadProposition create(UploadProposition proposition,
			UploadPropositionActionType action) throws BusinessException {
		Validate.notNull(proposition, "UploadProposition must be set.");

		AbstractDomain rootDomain = domainBusinessService.getUniqueRootDomain();
		proposition.setDomain(rootDomain);

		UploadProposition created;
		if (action.equals(UploadPropositionActionType.ACCEPT)) {
			proposition.setStatus(UploadPropositionStatus.SYSTEM_ACCEPTED);
			created = uploadPropositionBusinessService.create(proposition);
			User owner = null;
			try {
				owner = userService.findOrCreateUser(proposition
						.getRecipientMail(), StringUtils.defaultString(
						proposition.getDomainSource(),
						rootDomain.getIdentifier()));
			} catch (BusinessException e) {
				logger.error("The recipient of the upload proposition can't be found : "
						+ created.getUuid()
						+ ": "
						+ proposition.getRecipientMail());
				return null;
			}
			acceptHook(owner, created);
		} else {
			created = uploadPropositionBusinessService.create(proposition);
		}
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
			logger.equals(actor.getAccountReprentation()
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
	}

	public void acceptHook(User owner, UploadProposition created)
			throws BusinessException {
		AbstractDomain domain = owner.getDomain();
		UploadRequest e = new UploadRequest();
		UploadRequestGroup grp = new UploadRequestGroup(created);

		grp = uploadRequestService.createRequestGroup(owner, grp);
		e.setOwner(owner);
		e.setAbstractDomain(owner.getDomain());
		e.setUploadRequestGroup(grp);
		e.setUploadPropositionRequestUuid(created.getUuid());
		e.setActivationDate(new Date());
		e.setNotificationDate(new Date());
		e.setCanDelete(true);
		e.setCanClose(true);
		e.setCanEditExpiryDate(true);
		e.setSecured(false);

		TimeUnitValueFunctionality expiryDateFunc = functionalityReadOnlyService
				.getUploadRequestExpiryTimeFunctionality(domain);

		if (expiryDateFunc.getActivationPolicy().getStatus()) {
			logger.debug("expiryDateFunc is activated");
			@SuppressWarnings("deprecation")
			Date expiryDate = DateUtils.add(new Date(),
					expiryDateFunc.toCalendarUnitValue(),
					expiryDateFunc.getValue());
			e.setExpiryDate(expiryDate);
		}
		e.setNotificationDate(e.getExpiryDate());

		SizeUnitValueFunctionality maxDepositSizeFunc = functionalityReadOnlyService
				.getUploadRequestMaxDepositSizeFunctionality(domain);

		if (maxDepositSizeFunc.getActivationPolicy().getStatus()) {
			logger.debug("maxDepositSizeFunc is activated");
			long maxDepositSize = ((FileSizeUnitClass) maxDepositSizeFunc
					.getUnit()).getPlainSize(maxDepositSizeFunc.getValue());
			e.setMaxDepositSize(maxDepositSize);
		}

		IntegerValueFunctionality maxFileCountFunc = functionalityReadOnlyService
				.getUploadRequestMaxFileCountFunctionality(domain);

		if (maxFileCountFunc.getActivationPolicy().getStatus()) {
			logger.debug("maxFileCountFunc is activated");
			int maxFileCount = maxFileCountFunc.getValue();
			e.setMaxFileCount(maxFileCount);
		}

		SizeUnitValueFunctionality maxFileSizeFunc = functionalityReadOnlyService
				.getUploadRequestMaxFileSizeFunctionality(domain);

		if (maxFileSizeFunc.getActivationPolicy().getStatus()) {
			logger.debug("maxFileSizeFunc is activated");
			long maxFileSize = ((FileSizeUnitClass) maxFileSizeFunc.getUnit())
					.getPlainSize(maxFileSizeFunc.getValue());
			e.setMaxFileSize(maxFileSize);
		}

		StringValueFunctionality notificationLangFunc = functionalityReadOnlyService
				.getUploadRequestNotificationLanguageFunctionality(domain);

		if (notificationLangFunc.getActivationPolicy().getStatus()) {
			logger.debug("notificationLangFunc is activated");
			e.setLocale(notificationLangFunc.getValue());
		}

		Contact contact = new Contact(created.getRecipientMail());
		uploadRequestService.createRequest(owner, e, contact);
	}
}
