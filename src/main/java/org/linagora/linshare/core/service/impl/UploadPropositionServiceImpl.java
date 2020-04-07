/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.UploadPropositionBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadPropositionExceptionRuleType;
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.LanguageEnumValueFunctionality;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.UploadPropositionResourceAccessControl;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadPropositionExceptionRuleService;
import org.linagora.linshare.core.service.UploadPropositionService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.UploadProposition;
import org.linagora.linshare.mongo.entities.UploadPropositionExceptionRule;
import org.linagora.linshare.mongo.entities.logs.UploadPropositionAuditLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class UploadPropositionServiceImpl  extends GenericServiceImpl<Account, UploadProposition> implements UploadPropositionService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionServiceImpl.class);

	private final UploadPropositionBusinessService uploadPropositionBusinessService;

	private final UserService userService;

	private final UserRepository<User> userRepository;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	@SuppressWarnings("unused")
	private final MailBuildingService mailBuildingService;

	@SuppressWarnings("unused")
	private final NotifierService notifierService;
	
	private final UploadRequestGroupService uploadRequestGroupService;

	private final UploadPropositionExceptionRuleService uploadPropositionExceptionRuleService;

	private final LogEntryService logEntryService;

	public UploadPropositionServiceImpl(
			final UploadPropositionBusinessService uploadPropositionBusinessService,
			final UploadPropositionResourceAccessControl rac,
			final UserService userService,
			final UserRepository<User> userRepository,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService,
			final UploadRequestGroupService uploadRequestGroupService,
			final UploadPropositionExceptionRuleService uploadPropositionExceptionRuleService,
			final LogEntryService logEntryService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.uploadPropositionBusinessService = uploadPropositionBusinessService;
		this.userService = userService;
		this.userRepository = userRepository;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.uploadRequestGroupService = uploadRequestGroupService;
		this.uploadPropositionExceptionRuleService = uploadPropositionExceptionRuleService;
		this.logEntryService = logEntryService;
	}

	@Override
	public UploadProposition create(Account authUser, String recipientMail, UploadProposition uploadProposition) {
		Validate.notNull(uploadProposition, "The Upload proposition cannot be null");
		Account targetedAccount = userRepository.findByMail(recipientMail);
		preChecks(authUser, targetedAccount);
		checkCreatePermission(authUser, targetedAccount, UploadProposition.class,
				BusinessErrorCode.UPLOAD_PROPOSITION_CAN_NOT_CREATE, null);
		if (UploadPropositionStatus.SYSTEM_REJECTED.equals(uploadProposition.getStatus())
				|| (isInExceptionRuleList(authUser, targetedAccount, uploadProposition.getContact().getMail(),
						UploadPropositionExceptionRuleType.DENY))) {
			// The uploadProposition has been rejected by the system : no upload request nor
			// upload proposition are created
			if (UploadPropositionStatus.SYSTEM_PENDING.equals(uploadProposition.getStatus())) {
				uploadProposition.setStatus(UploadPropositionStatus.USER_REJECTED);
			}
			logger.debug("REJECTED Upload proposition FROM " + uploadProposition.getContact().toString() + " TO "
					+ targetedAccount.getAccountRepresentation());
			return uploadProposition;
		}
		if (Strings.isNullOrEmpty(uploadProposition.getAccountUuid())) {
			uploadProposition.setAccountUuid(targetedAccount.getLsUuid());
		}
		if (Strings.isNullOrEmpty(uploadProposition.getDomainUuid())) {
			uploadProposition.setDomainUuid(targetedAccount.getDomainId());
		}
		UploadProposition created;
		if (UploadPropositionStatus.SYSTEM_ACCEPTED.equals(uploadProposition.getStatus())
				|| (isInExceptionRuleList(authUser, targetedAccount, uploadProposition.getContact().getMail(),
						UploadPropositionExceptionRuleType.ALLOW))) {
			if (UploadPropositionStatus.SYSTEM_PENDING.equals(uploadProposition.getStatus())) {
				uploadProposition.setStatus(UploadPropositionStatus.USER_ACCEPTED);
			}
			// The uploadProposition has been accepted by the system : an upload request is
			// directly created
			acceptHook((User) targetedAccount, uploadProposition);
			created = uploadProposition;
		} else {
			// No system rules have been applied to the proposition : an upload proposition
			// is submitted to the targeted account
			created = uploadPropositionBusinessService.create(uploadProposition);
			uploadPropositionBusinessService.updateStatus(created, UploadPropositionStatus.USER_PENDING);
			// TODO: UploadProposition is not supported anymore since 2.0. Do we want to support it again ?
//			MailContainerWithRecipient mail = mailBuildingService.buildCreateUploadProposition((User) targetedAccount,
//					uploadProposition);
//			notifierService.sendNotification(mail);
			UploadPropositionAuditLogEntry log = new UploadPropositionAuditLogEntry(authUser, targetedAccount,
					LogAction.CREATE, AuditLogEntryType.UPLOAD_PROPOSITION, created.getUuid(), created);
			logEntryService.insert(log);
		}
		return created;
	}

	@Override
	public UploadProposition delete(Account authUser, Account actor, UploadProposition uploadProposition)
			throws BusinessException {
		Validate.notNull(authUser, "AuthUser must be set.");
		Validate.notNull(actor, "Actor must be set");
		Validate.notNull(uploadProposition, "UploadProposition must be set");
		Validate.notEmpty(uploadProposition.getUuid(), "Uuid must be set");
		UploadProposition found = uploadPropositionBusinessService.findByUuid(uploadProposition.getUuid());
		checkDeletePermission(authUser, actor, UploadProposition.class,
				BusinessErrorCode.UPLOAD_PROPOSITION_CAN_NOT_DELETE, found);
		UploadPropositionAuditLogEntry log = new UploadPropositionAuditLogEntry(authUser, actor, LogAction.DELETE,
				AuditLogEntryType.UPLOAD_PROPOSITION, found.getUuid(), found);
		uploadPropositionBusinessService.delete(found);
		logEntryService.insert(log);
		return found;
	}

	@Override
	public UploadProposition find(Account authUser, Account actor, String uuid) throws BusinessException {
		Validate.notNull(authUser, "AuthUser must be set.");
		Validate.notNull(actor, "Actor must be set");
		UploadProposition uploadProposition = uploadPropositionBusinessService.findByUuid(uuid);
		if (uploadProposition == null) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_PROPOSITION_NOT_FOUND, "Can not find upload proposition with uuid : " + uuid);
		}
		checkReadPermission(authUser, actor, UploadProposition.class, BusinessErrorCode.UPLOAD_PROPOSITION_CAN_NOT_READ,
				uploadProposition);
		return uploadProposition;
	}

	@Override
	public List<UploadProposition> findAllByAccount(Account authUser, Account actor) throws BusinessException {
		Validate.notNull(authUser, "AuthUser must be set.");
		Validate.notNull(actor, "Actor must be set");
		checkListPermission(authUser, actor, UploadProposition.class, BusinessErrorCode.UPLOAD_PROPOSITION_CAN_NOT_LIST,
				null);
		return uploadPropositionBusinessService.findAllByAccountUuid(actor.getLsUuid());
	}

	@Override
	public void checkIfValidRecipient(Account actor, String mail,
			String domainId) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(mail, "Mail must be set.");
		if (!actor.hasUploadPropositionRole()) {
			logger.error(actor.getAccountRepresentation()
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
	public UploadProposition accept(Account authUser, Account actor, String uuid) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(uuid, "Upload Proposition uuid must be set");
		UploadProposition found = find(authUser, actor, uuid);
		checkUpdatePermission(authUser, actor, UploadProposition.class,
				BusinessErrorCode.UPLOAD_PROPOSITION_CAN_NOT_UPDATE, found);
		found = uploadPropositionBusinessService.updateStatus(found, UploadPropositionStatus.USER_ACCEPTED);
		acceptHook((User) actor, found);
		UploadPropositionAuditLogEntry log = new UploadPropositionAuditLogEntry(authUser, actor, LogAction.UPDATE,
				AuditLogEntryType.UPLOAD_PROPOSITION, found.getUuid(), found);
		log.setResourceUpdated(found);
		logEntryService.insert(log);
		return delete(authUser, actor, found);
	}

	@Override
	public UploadProposition reject(Account authUser, Account actor, String uuid) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(uuid, "Upload Proposition uuid must be set");
		UploadProposition found = find(authUser, actor, uuid);
		checkUpdatePermission(authUser, actor, UploadProposition.class,
				BusinessErrorCode.UPLOAD_PROPOSITION_CAN_NOT_UPDATE, found);
		found = uploadPropositionBusinessService.updateStatus(found, UploadPropositionStatus.USER_REJECTED);
		// TODO: UploadProposition is not supported anymore since 2.0. Do we want to support it again ?
//		MailContainerWithRecipient mail = mailBuildingService.buildRejectUploadProposition((User) actor, found);
//		notifierService.sendNotification(mail);
		UploadPropositionAuditLogEntry log = new UploadPropositionAuditLogEntry(authUser, actor, LogAction.UPDATE,
				AuditLogEntryType.UPLOAD_PROPOSITION, found.getUuid(), found);
		log.setResourceUpdated(found);
		logEntryService.insert(log);
		return delete(authUser, actor, found);
	}

	public void acceptHook(User owner, UploadProposition created)
			throws BusinessException {
		UploadRequest req = new UploadRequest();
		req.setUploadPropositionRequestUuid(created.getUuid());
		getDefaultValue(owner, req);// get value default from domain
		Contact contact = new Contact(created.getContact().getMail());
		uploadRequestGroupService.create(owner, owner, req, Lists.newArrayList(contact),
				created.getLabel(), created.getBody(), null);
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

	private boolean isInExceptionRuleList(Account authUser, Account actor, String mail, UploadPropositionExceptionRuleType exceptionRuleType) {
		List<UploadPropositionExceptionRule> exceptionRules = uploadPropositionExceptionRuleService.findByExceptionRule(authUser, actor, exceptionRuleType);
		for( UploadPropositionExceptionRule exceptionRule : exceptionRules) {
			if (exceptionRule.getMail().equals(mail)) {
				return true;
			}
		}
		return false;
	}
}
