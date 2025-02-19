/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestGroupBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.LanguageEnumValueFunctionality;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.UploadRequestContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.rac.UploadRequestGroupResourceAccessControl;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.logs.UploadRequestGroupAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.UploadRequestGroupMto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class UploadRequestGroupServiceImpl extends GenericServiceImpl<Account, UploadRequestGroup> implements UploadRequestGroupService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadRequestGroupServiceImpl.class);

	private final UploadRequestGroupBusinessService uploadRequestGroupBusinessService;

	private final FunctionalityReadOnlyService functionalityService;

	private final UploadRequestUrlService uploadRequestUrlService;

	private final NotifierService notifierService;

	private final LogEntryService logEntryService;

	private final UploadRequestService uploadRequestService;

	private final UploadRequestEntryService requestEntryService;

	private final RecipientFavouriteRepository recipientFavouriteRepository;

	private TimeService timeService;

	public UploadRequestGroupServiceImpl(
			final UploadRequestGroupBusinessService uploadRequestGroupBusinessService,
			final UploadRequestGroupResourceAccessControl groupRac,
			final FunctionalityReadOnlyService functionalityService,
			final UploadRequestUrlService uploadRequestUrlService,
			final NotifierService notifierService,
			final LogEntryService logEntryService,
			final UploadRequestService uploadRequestService,
			final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			final UploadRequestEntryService requestEntryService,
			final RecipientFavouriteRepository recipientFavouriteRepository,
			TimeService timeService) {
		super(groupRac, sanitizerInputHtmlBusinessService);
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
		this.functionalityService = functionalityService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.notifierService = notifierService;
		this.logEntryService = logEntryService;
		this.uploadRequestService = uploadRequestService;
		this.requestEntryService = requestEntryService;
		this.timeService = timeService;
		this.recipientFavouriteRepository = recipientFavouriteRepository;
	}

	@Override
	public List<UploadRequestGroup> findAll(Account actor, Account owner, List<UploadRequestStatus> statusList)
			throws BusinessException {
		preChecks(actor, owner);
		checkListPermission(actor, owner, UploadRequestGroup.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
				null);
		return uploadRequestGroupBusinessService.findAll(owner, statusList);
	}

	@Override
	public UploadRequestGroup find(Account actor, Account owner, String uuid) {
		preChecks(actor, owner);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupBusinessService.findByUuid(uuid);
		if (uploadRequestGroup == null) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_GROUP_NOT_FOUND,
					"Can not find upload request with uuid : " + uuid);
		}
		checkReadPermission(actor, uploadRequestGroup.getOwner(), UploadRequestGroup.class,
				BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, uploadRequestGroup);
		return uploadRequestGroup;
	}

	@Override
	public UploadRequestGroup create(Account actor, User owner, UploadRequest inputRequest,
			List<Contact> contacts, String subject, String body, Boolean collectiveMode) throws BusinessException {
		checkCreatePermission(actor, owner, UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, null);
		AbstractDomain domain = owner.getDomain();
		if (collectiveMode == null) {
			collectiveMode = false;
		}
		UploadRequest req = initUploadRequest(owner, inputRequest);
		UploadRequestGroup uploadRequestGroup = new UploadRequestGroup(owner, domain, sanitize(subject), sanitize(body),
				req.getActivationDate(), req.isCanDelete(), req.isCanClose(), req.isCanEditExpiryDate(),
				req.getLocale(), req.isProtectedByPassword(), req.getEnableNotification(), collectiveMode, req.getStatus(),
				req.getExpiryDate(), req.getNotificationDate(), req.getMaxFileCount(), req.getMaxDepositSize(),
				req.getMaxFileSize());
		uploadRequestGroup = uploadRequestGroupBusinessService.create(uploadRequestGroup);
		UploadRequestContainer container = new UploadRequestContainer();
		UploadRequestGroupAuditLogEntry groupLog = new UploadRequestGroupAuditLogEntry(new AccountMto(actor),
				new AccountMto(owner), LogAction.CREATE, AuditLogEntryType.UPLOAD_REQUEST_GROUP,
				uploadRequestGroup.getUuid(), uploadRequestGroup);
		if (!uploadRequestGroup.isCollective()) {
			container.addLog(groupLog);
		}
		req.setUploadRequestGroup(uploadRequestGroup);
		TimeUnitValueFunctionality functionality = functionalityService.getCollectedEmailsExpirationTimeFunctionality(owner.getDomain());
		Date contactExpirationDate = functionality.getContactExpirationDate();
		if (collectiveMode) {
			// we need to add contacts to the container to be able to notify this list to every recipient in a collective upload request
			container.setRecipients(contacts);
			container = uploadRequestService.create(actor, owner, req, container);
			for (Contact contact : contacts) {
				Validate.notNull(contact, "contact must be set");
				Validate.notEmpty(contact.getMail(), "mail of the contact must be set");
				container = uploadRequestUrlService.create(container.getUploadRequests().iterator().next(), contact, container);
				recipientFavouriteRepository.incAndCreate(owner, contact.getMail(), contactExpirationDate, false);
			}
		} else {
			for (Contact contact : contacts) {
				Validate.notNull(contact, "contact must be set");
				Validate.notEmpty(contact.getMail(), "mail of the contact must be set");
				UploadRequest clone = req.clone();
				container = uploadRequestService.create(actor, owner, clone, container);
				container = uploadRequestUrlService.create(clone, contact, container);
				recipientFavouriteRepository.incAndCreate(owner, contact.getMail(), contactExpirationDate, false);
			}
		}
		uploadRequestGroup.setUploadRequests(container.getUploadRequests());
		notifierService.sendNotification(container.getMailContainers());
		logEntryService.insert(container.getLogs());
		return uploadRequestGroup;
	}

	private UploadRequest initUploadRequest(User owner, UploadRequest req) {
		AbstractDomain domain = owner.getDomain();
		if (null == req.getActivationDate()) {
			req.setActivationDate(timeService.dateNow());
			req.setStatus(UploadRequestStatus.ENABLED);
		} else {
			req.setStatus(UploadRequestStatus.CREATED);
			Date activationDate = functionalityService.roundToUpperHour(req.getActivationDate());
			req.setActivationDate(applyBusinessRuleForActivationDate(domain, activationDate));
		}
		req.setExpiryDate(functionalityService.roundToUpperHour(req.getExpiryDate()));
		req.setNotificationDate(functionalityService.roundToUpperHour(req.getNotificationDate()));
		applyBusinessRuleForExpiryAndNoticationDate(domain, req);
		applyBusinessRuleForMaxDepositSize(domain, req);
		applyBusinessRuleForMaxFileCount(domain, req);
		applyBusinessRuleForMaxFileSize(domain, req);
		applyBusinessRuleForNotificationLanguage(domain, req);
		applyBusinessRuleForCanDelete(domain, req);
		applyBusinessRuleForCanClose(domain, req);
		applyBusinessRuleForSecuredUrl(domain, req);
		req.setCanEditExpiryDate(true);
		return req;
	}

	private UploadRequestGroup checkUploadRequestGroupData(User owner, UploadRequestGroup group) {
		AbstractDomain domain = owner.getDomain();
		group.setExpiryDate(functionalityService.roundToUpperHour(group.getExpiryDate()));
		group.setNotificationDate(functionalityService.roundToUpperHour(group.getNotificationDate()));
		applyBusinessRuleForExpiryAndNoticationDate(domain, group);
		applyBusinessRuleForMaxDepositSize(domain, group);
		applyBusinessRuleForMaxFileCount(domain, group);
		applyBusinessRuleForMaxFileSize(domain, group);
		applyBusinessRuleForNotificationLanguage(domain, group);
		applyBusinessRuleForCanDelete(domain, group);
		applyBusinessRuleForCanClose(domain, group);
		return group;
	}

	private Boolean applyBusinessRuleForSecuredUrl(AbstractDomain domain, Boolean isProtectedByPassword) {
		BooleanValueFunctionality func = functionalityService.getUploadRequestSecureUrlFunctionality(domain);
		return checkBoolean(func, isProtectedByPassword);
	}

	private void applyBusinessRuleForSecuredUrl(AbstractDomain domain, UploadRequest req) {
		req.setProtectedByPassword(applyBusinessRuleForSecuredUrl(domain, req.isProtectedByPassword()));
	}

	private Boolean applyBusinessRuleForCanDelete(AbstractDomain domain, Boolean canDeleteToCheck) {
		BooleanValueFunctionality func = functionalityService.getUploadRequestCandDeleteFileFunctionality(domain);
		return checkBoolean(func, canDeleteToCheck);
	}

	private void applyBusinessRuleForCanDelete(AbstractDomain domain, UploadRequest req) {
		req.setCanDelete(applyBusinessRuleForCanDelete(domain, req.isCanDelete()));
	}

	private void applyBusinessRuleForCanDelete(AbstractDomain domain, UploadRequestGroup group) {
		group.setCanDelete(applyBusinessRuleForCanDelete(domain, group.getCanDelete()));
	}

	private Boolean applyBusinessRuleForCanClose(AbstractDomain domain, Boolean canClose) {
		BooleanValueFunctionality func = functionalityService.getUploadRequestCanCloseFunctionality(domain);
		return checkBoolean(func, canClose);
	}

	private void applyBusinessRuleForCanClose(AbstractDomain domain, UploadRequest req) {
		req.setCanClose(applyBusinessRuleForCanClose(domain, req.isCanClose()));
	}

	private void applyBusinessRuleForCanClose(AbstractDomain domain, UploadRequestGroup group) {
		group.setCanClose(applyBusinessRuleForCanClose(domain, group.getCanClose()));
	}

	private Date applyBusinessRuleForActivationDate(AbstractDomain domain, Date dateToCheck) {
		TimeUnitValueFunctionality func = functionalityService
				.getUploadRequestActivationTimeFunctionality(domain);
		Date checkDate = functionalityService.getUploadRequestDateValue(func, dateToCheck,
				BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID);
		if (checkDate == null) {
			checkDate = timeService.dateNow();
		}
		return checkDate;
	}

	private Date applyBusinessRuleForExpiryDate(AbstractDomain domain, Date expiryDate, Date activationDate) {
		TimeUnitValueFunctionality funcExpiry = functionalityService.getUploadRequestExpiryTimeFunctionality(domain);
		return functionalityService.getUploadRequestExpirationDateValue(funcExpiry, expiryDate, activationDate,
				BusinessErrorCode.UPLOAD_REQUEST_EXPIRY_DATE_INVALID);
	}


	private Date applyBusinessRuleForNoticationDate(AbstractDomain domain, Date expiryDate, Date notifDate) {
		TimeUnitValueFunctionality funcNotify = functionalityService
				.getUploadRequestNotificationTimeFunctionality(domain);
		return functionalityService.getNotificationDateValue(funcNotify, notifDate, expiryDate,
				BusinessErrorCode.UPLOAD_REQUEST_NOTIFICATION_DATE_INVALID); // Must have a setted value in order to
																				// return a value not null
	}


	private void applyBusinessRuleForExpiryAndNoticationDate(AbstractDomain domain, UploadRequest req) {
		req.setExpiryDate(applyBusinessRuleForExpiryDate(domain, req.getExpiryDate(), req.getActivationDate()));
		req.setNotificationDate(applyBusinessRuleForNoticationDate(domain, req.getExpiryDate(), req.getNotificationDate()));
	}

	private void applyBusinessRuleForExpiryAndNoticationDate(AbstractDomain domain, UploadRequestGroup group) {
		group.setExpiryDate(applyBusinessRuleForExpiryDate(domain, group.getExpiryDate(), group.getActivationDate()));
		group.setNotificationDate(applyBusinessRuleForNoticationDate(domain, group.getExpiryDate(), group.getNotificationDate()));
	}


	private Language applyBusinessRuleForNotificationLanguage(AbstractDomain domain, Language userLocale) {
		LanguageEnumValueFunctionality func = functionalityService
				.getUploadRequestNotificationLanguageFunctionality(domain);
		return checkLanguage(func, userLocale);
	}

	private void applyBusinessRuleForNotificationLanguage(AbstractDomain domain, UploadRequest req) {
		req.setLocale(applyBusinessRuleForNotificationLanguage(domain, req.getLocale()));
	}

	private void applyBusinessRuleForNotificationLanguage(AbstractDomain domain, UploadRequestGroup group) {
		group.setLocale(applyBusinessRuleForNotificationLanguage(domain, group.getLocale()));
	}

	private Long applyBusinessRuleForMaxFileSize(AbstractDomain domain, Long checkSize) {
		SizeUnitValueFunctionality func = functionalityService.getUploadRequestMaxFileSizeFunctionality(domain);
		return functionalityService.getSizeValue(func, checkSize, BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID);
	}

	private void applyBusinessRuleForMaxFileSize(AbstractDomain domain, UploadRequest req) {
		req.setMaxFileSize(applyBusinessRuleForMaxFileSize(domain, req.getMaxFileSize()));
	}

	private void applyBusinessRuleForMaxFileSize(AbstractDomain domain, UploadRequestGroup group) {
		group.setMaxFileSize(applyBusinessRuleForMaxFileSize(domain, group.getMaxFileSize()));
	}

	private Integer applyBusinessRuleForMaxFileCount(AbstractDomain domain, Integer checkInteger) {
		IntegerValueFunctionality func = functionalityService.getUploadRequestMaxFileCountFunctionality(domain);
		return functionalityService.getIntegerValue(func, checkInteger,
				BusinessErrorCode.UPLOAD_REQUEST_INTEGER_VALUE_INVALID);
	}

	private void applyBusinessRuleForMaxFileCount(AbstractDomain domain, UploadRequest req) {
		req.setMaxFileCount(applyBusinessRuleForMaxFileCount(domain, req.getMaxFileCount()));
	}

	private void applyBusinessRuleForMaxFileCount(AbstractDomain domain, UploadRequestGroup group) {
		group.setMaxFileCount(applyBusinessRuleForMaxFileCount(domain, group.getMaxFileCount()));
	}

	private Long applyBusinessRuleForMaxDepositSize(AbstractDomain domain, Long maxDepositSizeToCheck) {
		SizeUnitValueFunctionality func = functionalityService.getUploadRequestMaxDepositSizeFunctionality(domain);
		return functionalityService.getSizeValue(func, maxDepositSizeToCheck,
				BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID);
	}

	private void applyBusinessRuleForMaxDepositSize(AbstractDomain domain, UploadRequest req) {
		req.setMaxDepositSize(applyBusinessRuleForMaxDepositSize(domain, req.getMaxDepositSize()));
	}

	private void applyBusinessRuleForMaxDepositSize(AbstractDomain domain, UploadRequestGroup group) {
		group.setMaxDepositSize(applyBusinessRuleForMaxDepositSize(domain, group.getMaxDepositSize()));
	}

	private boolean checkBoolean(BooleanValueFunctionality func, Boolean current) {
		if (func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is activated");
			Boolean defaultValue = func.getValue();
			if (func.getDelegationPolicy() != null
					&& func.getDelegationPolicy().getStatus()) {
				logger.debug(func.getIdentifier() + " has a delegation policy");
				if (current != null) {
					return current;
				}
				return defaultValue;
			} else {
				// there is no delegation, the current value should be the
				// system value or null
				logger.debug(func.getIdentifier()
						+ " does not have a delegation policy");
				if (current != null) {
					if (!current.equals(defaultValue)) {
						logger.warn("the current value " + current.toString()
								+ " is different than system value "
								+ defaultValue);
					}
				}
				return defaultValue;
			}
		} else {
			logger.debug(func.getIdentifier() + " is not activated");
			if (current != null) {
				logger.warn("the current value " + current.toString()
						+ " should be null for the functionality "
						+ func.toString());
			}
			return false;
		}
	}

	private Language checkLanguage(LanguageEnumValueFunctionality func, Language current) {
		Language defaultValue = func.getValue();
		if (func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is activated");
			if (func.getDelegationPolicy() != null
					&& func.getDelegationPolicy().getStatus()) {
				logger.debug(func.getIdentifier() + " has a delegation policy");
				if (current != null) {
					return current;
				}
				return defaultValue;
			} else {
				// there is no delegation, the current value should be the
				// system value or null
				logger.debug(func.getIdentifier()
						+ " does not have a delegation policy");
				if (current != null) {
					if (!current.equals(defaultValue)) {
						logger.warn("the current value " + current.toString()
								+ " is different than system value "
								+ defaultValue);
					}
				}
				return defaultValue;
			}
		} else {
			logger.debug(func.getIdentifier() + " is not activated");
			if (current != null) {
				logger.warn("the current value " + current.toString()
						+ " should be null for the functionality "
						+ func.toString());
			}
			return null;
		}
	}

	@Override
	public UploadRequestGroup updateStatus(Account authUser, Account actor, String requestGroupUuid,
			UploadRequestStatus status, boolean copy) {
		preChecks(authUser, actor);
		UploadRequestGroup uploadRequestGroup = find(authUser, actor, requestGroupUuid);
		checkUpdatePermission(authUser, actor, UploadRequestGroup.class,
				BusinessErrorCode.UPLOAD_REQUEST_GROUP_FORBIDDEN, uploadRequestGroup);
		if (status.equals(uploadRequestGroup.getStatus())) {
			logger.debug("The new status {} is the same with current one {}, no operation was performed", status,
					uploadRequestGroup.getStatus());
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_GROUP_STATUS_NOT_MODIFIED,
					"The new status is the same, no operation was performed");
		}
		if (uploadRequestGroup.isCollective() && uploadRequestGroup.getUploadRequests().size() > 1) {
			logger.error("Collective URG {} should have only one element {}", uploadRequestGroup.getUuid(),
					uploadRequestGroup.getUploadRequests().size());
		}
		UploadRequestGroupAuditLogEntry groupLog = new UploadRequestGroupAuditLogEntry(new AccountMto(authUser),
				new AccountMto(actor), LogAction.UPDATE, AuditLogEntryType.UPLOAD_REQUEST_GROUP,
				uploadRequestGroup.getUuid(), uploadRequestGroup);
		uploadRequestGroup = uploadRequestGroupBusinessService.updateStatus(uploadRequestGroup, status);
		updateNestedUploadRequestStatus(authUser, actor, status, copy, uploadRequestGroup);
		if (!uploadRequestGroup.isCollective()) {
			// Insert only audit trace for UPLOAD_REQUEST type when the group is collective
			groupLog.setResourceUpdated(new UploadRequestGroupMto(uploadRequestGroup, true));
			logEntryService.insert(groupLog);
		}
		return uploadRequestGroup;
	}

	private void updateNestedUploadRequestStatus(Account authUser, Account actor, UploadRequestStatus status,
			boolean copy, UploadRequestGroup uploadRequestGroup) {
		for (UploadRequest request : uploadRequestService.findUploadRequestsToUpdate(authUser, actor,
				uploadRequestGroup, UploadRequestStatus.listAllowedStatusToUpdate(status))) {
			uploadRequestService.updateStatus(authUser, actor, request.getUuid(), status, copy);
		}
	}

	public UploadRequestGroup update(User authUser, User actor, UploadRequestGroup uploadRequestGroup, Boolean force) {
		UploadRequestGroup group = find(authUser, actor, uploadRequestGroup.getUuid());
		if (group.isClosed() || group.isArchived()) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_GROUP_UPDATE_FORBIDDEN,
					"Cannot update the current upload request group: " + group.getUuid() + " because it is: "
							+ group.getStatus());
		}
		checkUpdatePermission(authUser, actor, UploadRequestGroup.class,
				BusinessErrorCode.UPLOAD_REQUEST_GROUP_FORBIDDEN, group);
		UploadRequestGroupAuditLogEntry groupLog = new UploadRequestGroupAuditLogEntry(new AccountMto(authUser),
				new AccountMto(actor), LogAction.UPDATE, AuditLogEntryType.UPLOAD_REQUEST_GROUP,
				uploadRequestGroup.getUuid(), group);
		group.setModificationDate(new Date());
		group.setBusinessSubject(sanitize(uploadRequestGroup.getSubject()));
		group.setBusinessBody(sanitize(uploadRequestGroup.getBody()));
		group.setBusinessMaxFileCount(uploadRequestGroup.getMaxFileCount());
		group.setBusinessMaxDepositSize(uploadRequestGroup.getMaxDepositSize());
		group.setBusinessMaxFileSize(uploadRequestGroup.getMaxFileSize());
		group.setBusinessNotificationDate(uploadRequestGroup.getNotificationDate());
		group.setBusinessActivationDate(uploadRequestGroup.getActivationDate());
		group.setBusinessExpiryDate(uploadRequestGroup.getExpiryDate());
		group.setBusinessCanDelete(uploadRequestGroup.getCanDelete());
		group.setBusinessCanClose(uploadRequestGroup.getCanClose());
		group.setBusinessCanEditExpiryDate(uploadRequestGroup.getCanEditExpiryDate());
		group.setBusinessLocale(uploadRequestGroup.getLocale());
		group.setBusinessEnableNotification(uploadRequestGroup.getEnableNotification());
		group = checkUploadRequestGroupData(authUser, group);
		for (UploadRequest uploadRequest : group.getUploadRequests()) {
			if (force) {
				uploadRequest.setPristine(true);
				uploadRequestService.update(authUser, actor, uploadRequest.getUuid(), new UploadRequest(group), true);
			} else if (uploadRequest.isPristine()) {
				uploadRequestService.update(authUser, actor, uploadRequest.getUuid(), new UploadRequest(group), true);
			}
		}
		uploadRequestGroup = uploadRequestGroupBusinessService.update(group);
		if (!group.isCollective()) {
			// Insert only audit trace for UPLOAD_REQUEST type when the group is collective
			groupLog.setResourceUpdated(new UploadRequestGroupMto(uploadRequestGroup, true));
			logEntryService.insert(groupLog);
		}
		return group;
	}

	@Override
	public List<String> findOutdatedRequestsGroup(SystemAccount actor) {
		checkActorPermission(actor);
		return uploadRequestGroupBusinessService.findOutdatedRequests();
	}

	private void checkActorPermission(SystemAccount actor) {
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You have no rights to access this service.");
		}
	}

	private void checkStatusPermission(UploadRequestStatus status) {
		if (!(UploadRequestStatus.CREATED.equals(status)) && !(UploadRequestStatus.ENABLED.equals(status))) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You have no rights to add new recipient");
		}
	}

	@Override
	public UploadRequestGroup addNewRecipients(User authUser, User actor, UploadRequestGroup uploadRequestGroup,
			List<ContactDto> recipientEmail) {
		checkStatusPermission(uploadRequestGroup.getStatus());
		checkUpdatePermission(authUser, actor, UploadRequestGroup.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
				uploadRequestGroup);
		UploadRequestContainer container = new UploadRequestContainer();
		TimeUnitValueFunctionality functionality = functionalityService.getCollectedEmailsExpirationTimeFunctionality(actor.getDomain());
		Date contactExpirationDate = functionality.getContactExpirationDate();
		for (ContactDto recipient : recipientEmail) {
			Validate.notEmpty(recipient.getMail(), "Mail must be set");
			Contact contact = new Contact(recipient.getMail());
			UploadRequest uploadRequest = new UploadRequest(uploadRequestGroup);
			if (uploadRequestGroup.isCollective()) {
				uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
				List<Contact> recipients = Lists.newArrayList();
				recipients.add(contact);
				for (UploadRequestUrl u : uploadRequest.getUploadRequestURLs()) {
					recipients.add(u.getContact());
				}
				container.setRecipients(recipients);
			} else {
				container = uploadRequestService.create(authUser, actor, uploadRequest, container);
				uploadRequestGroup.getUploadRequests().add(uploadRequest);
			}
			container = uploadRequestUrlService.create(uploadRequest, contact, container);
			recipientFavouriteRepository.incAndCreate(actor, contact.getMail(), contactExpirationDate, true);
		}
		notifierService.sendNotification(container.getMailContainers());
		logEntryService.insert(container.getLogs());
		return uploadRequestGroup;
	}

	@Override
	public FileAndMetaData downloadEntries(Account authUser, Account actor, UploadRequestGroup uploadRequestGroup,
			String requestUuid) {
		preChecks(authUser, actor);
		if (!Lists.newArrayList(UploadRequestStatus.ENABLED, UploadRequestStatus.CLOSED)
				.contains(uploadRequestGroup.getStatus())) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_GROUP_ENTRIES_ARCHIVE_DOWNLOAD_FORBIDDEN,
					"You are not authorized to archive download the entries of this upload request group: "
							+ uploadRequestGroup.getUuid() + "it's status must be ENABLED or CLOSED");
		}
		List<UploadRequestEntry> entries = getEntriesToDownload(authUser, actor, uploadRequestGroup, requestUuid);
		FileAndMetaData dataFile = requestEntryService.downloadEntries(authUser, actor, uploadRequestGroup, entries);
		return dataFile;
	}

	private List<UploadRequestEntry> getEntriesToDownload(Account authUser, Account actor,
			UploadRequestGroup uploadRequestGroup, String requestUuid) {
		List<UploadRequestEntry> entries = Lists.newArrayList();
		if (uploadRequestGroup.isCollective()) {
			for (UploadRequest request : uploadRequestGroup.getUploadRequests()) {
				for (UploadRequestUrl requestUrl : request.getUploadRequestURLs()) {
					entries.addAll(requestEntryService.findAllExtEntries(requestUrl));
				}
			}
		} else {
			Validate.notEmpty(requestUuid,
					"The upload request group is not collective, the upload request uuid must be set");
			UploadRequest request = uploadRequestService.find(authUser, actor, requestUuid);
			if (!uploadRequestGroup.getUploadRequests().contains(request)) {
				throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
						"The upload request which you are trying to download it s entry does not exist on the upload request group with uuid: "
								+ uploadRequestGroup.getUuid());
			} else {
				for (UploadRequestUrl requestUrl : request.getUploadRequestURLs()) {
					entries.addAll(requestEntryService.findAllExtEntries(requestUrl));
				}
			}
		}
		return entries;
	}

	@Override
	public Integer countNbrUploadedFiles(UploadRequestGroup uploadRequestGroup) {
		return uploadRequestGroupBusinessService.countNbrUploadedFiles(uploadRequestGroup);
	}

	@Override
	public Long computeEntriesSize(UploadRequestGroup uploadRequestGroup) {
		return uploadRequestGroupBusinessService.computeEntriesSize(uploadRequestGroup);
	}

	@Override
	public void transferUploadRequestGroupsFromGuestToInternal(final Account guest, final Account owner) {
		this.uploadRequestGroupBusinessService.transferUploadRequestGroupsFromGuestToInternal(guest,owner);
	}
}
