/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

	private final TimeService timeService;

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
			final TimeService timeService) {
		super(groupRac, sanitizerInputHtmlBusinessService);
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
		this.functionalityService = functionalityService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.notifierService = notifierService;
		this.logEntryService = logEntryService;
		this.uploadRequestService = uploadRequestService;
		this.requestEntryService = requestEntryService;
		this.timeService = timeService;
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
		UploadRequestGroup req = uploadRequestGroupBusinessService.findByUuid(uuid);
		checkReadPermission(actor,
				req.getOwner(), UploadRequestGroup.class,
				BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, req);
		return req;
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
		UploadRequestGroup uploadRequestGroup = new UploadRequestGroup(owner, domain, subject, body,
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
		if (collectiveMode) {
			container = uploadRequestService.create(actor, owner, req, container);
			for (Contact contact : contacts) {
				Validate.notNull(contact, "contact must be set");
				Validate.notEmpty(contact.getMail(), "mail of the contact must be set");
				container = uploadRequestUrlService.create(container.getUploadRequests().iterator().next(), contact, container);
			}
		} else {
			for (Contact contact : contacts) {
				Validate.notNull(contact, "contact must be set");
				Validate.notEmpty(contact.getMail(), "mail of the contact must be set");
				UploadRequest clone = req.clone();
				container = uploadRequestService.create(actor, owner, clone, container);
				container = uploadRequestUrlService.create(clone, contact, container);
			}
		}
		uploadRequestGroup.setUploadRequests(container.getUploadRequests());
		notifierService.sendNotification(container.getMailContainers());
		logEntryService.insert(container.getLogs());
		return uploadRequestGroup;
	}

	private UploadRequest initUploadRequest(User owner, UploadRequest req) {
		AbstractDomain domain = owner.getDomain();
		checkActivationDate(domain, req);
		checkExpiryAndNoticationDate(domain, req);
		checkMaxDepositSize(domain, req);
		checkMaxFileCount(domain, req);
		checkMaxFileSize(domain, req);
		checkNotificationLanguage(domain, req);
		checkCanDelete(domain, req);
		checkCanClose(domain, req);
		checkSecuredUrl(domain, req);
		UploadRequestStatus status = req.getActivationDate().after(new Date())?UploadRequestStatus.CREATED:UploadRequestStatus.ENABLED;
		req.setStatus(status);
		req.setCanEditExpiryDate(true);
		return req;
	}

	private void checkSecuredUrl(AbstractDomain domain, UploadRequest req) {
		BooleanValueFunctionality func = functionalityService
				.getUploadRequestSecureUrlFunctionality(domain);
		boolean secure = checkBoolean(func, req.isProtectedByPassword());
		req.setProtectedByPassword(secure);
	}

	private void checkCanDelete(AbstractDomain domain, UploadRequest req) {
		BooleanValueFunctionality func = functionalityService
				.getUploadRequestCandDeleteFileFunctionality(domain);
		boolean canDelete = checkBoolean(func, req.isCanDelete());
		req.setCanDelete(canDelete);
	}

	private void checkCanClose(AbstractDomain domain, UploadRequest req) {
		BooleanValueFunctionality func = functionalityService
				.getUploadRequestCanCloseFunctionality(domain);
		boolean canClose = checkBoolean(func, req.isCanClose());
		req.setCanClose(canClose);
	}

	private void checkActivationDate(AbstractDomain domain, UploadRequest req) {
		TimeUnitValueFunctionality func = functionalityService
				.getUploadRequestActivationTimeFunctionality(domain);
		Date checkDate = functionalityService.getDateValue(func, req.getActivationDate(),
				BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID);
		if (checkDate == null) {
			checkDate = timeService.dateNow();
		}
		req.setActivationDate(checkDate);
	}

	private void checkExpiryAndNoticationDate(AbstractDomain domain,
			UploadRequest req) {
		TimeUnitValueFunctionality funcExpiry = functionalityService
				.getUploadRequestExpiryTimeFunctionality(domain);
		TimeUnitValueFunctionality funcNotify = functionalityService
				.getUploadRequestNotificationTimeFunctionality(domain);
		Date expiryDate = functionalityService.getDateValue(funcExpiry, req.getExpiryDate(),
				BusinessErrorCode.UPLOAD_REQUEST_EXPIRY_DATE_INVALID);
		req.setExpiryDate(expiryDate);
		Date notifDate = functionalityService.getNotificationDateValue(funcNotify, req.getNotificationDate(),
				req.getExpiryDate(), BusinessErrorCode.UPLOAD_REQUEST_NOTIFICATION_DATE_INVALID); // Must have a setted value in order to return a value not null
		req.setNotificationDate(notifDate);
	}

	private void checkNotificationLanguage(AbstractDomain domain,
			UploadRequest req) {
		LanguageEnumValueFunctionality func = functionalityService
				.getUploadRequestNotificationLanguageFunctionality(domain);
		Language userLocale = req.getLocale();
		Language checkLanguage = checkLanguage(func, userLocale);
		req.setLocale(checkLanguage);
	}

	private void checkMaxFileSize(AbstractDomain domain, UploadRequest req) {
		SizeUnitValueFunctionality func = functionalityService
				.getUploadRequestMaxFileSizeFunctionality(domain);
		Long checkSize = functionalityService.getSizeValue(func, req.getMaxFileSize(), BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID);
		req.setMaxFileSize(checkSize);
	}

	private void checkMaxFileCount(AbstractDomain domain, UploadRequest req) {
		IntegerValueFunctionality func = functionalityService
				.getUploadRequestMaxFileCountFunctionality(domain);
		Integer checkInteger = functionalityService.getIntegerValue(func, req.getMaxFileCount(),
				BusinessErrorCode.UPLOAD_REQUEST_INTEGER_VALUE_INVALID);
		req.setMaxFileCount(checkInteger);
	}

	private void checkMaxDepositSize(AbstractDomain domain, UploadRequest req) {
		SizeUnitValueFunctionality func = functionalityService
				.getUploadRequestMaxDepositSizeFunctionality(domain);
		Long checkSize = functionalityService.getSizeValue(func, req.getMaxDepositSize(), BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID);
		req.setMaxDepositSize(checkSize);
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
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupBusinessService.findByUuid(requestGroupUuid);
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
		for (UploadRequest uploadRequest : uploadRequestGroup.getUploadRequests()) {
			if (!uploadRequest.getUploadRequestGroup().isCollective() && status.equals(uploadRequest.getStatus())) {
				logger.debug(
						"The group is individual ==> skip already updated UR's that had same status, Group status {} | UR status {}",
						uploadRequest.getUploadRequestGroup().getStatus(), uploadRequest.getStatus());
				continue;
			}
			uploadRequestService.updateStatus(authUser, actor, uploadRequest.getUuid(), status, copy);
		}
		if (!uploadRequestGroup.isCollective()) {
			// Insert only audit trace for UPLOAD_REQUEST type when the group is collective
			groupLog.setResourceUpdated(new UploadRequestGroupMto(uploadRequestGroup, true));
			logEntryService.insert(groupLog);
		}
		return uploadRequestGroup;
	}

	public UploadRequestGroup update(User authUser, User actor, UploadRequestGroup uploadRequestGroup, Boolean force) {
		UploadRequestGroup group = uploadRequestGroupBusinessService.findByUuid(uploadRequestGroup.getUuid());
		checkUpdatePermission(authUser, actor, UploadRequestGroup.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
				group);
		UploadRequestGroupAuditLogEntry groupLog = new UploadRequestGroupAuditLogEntry(new AccountMto(authUser),
				new AccountMto(actor), LogAction.UPDATE, AuditLogEntryType.UPLOAD_REQUEST_GROUP,
				uploadRequestGroup.getUuid(), group);
		group.setModificationDate(new Date());
		group.setBusinessSubject(uploadRequestGroup.getSubject());
		group.setBusinessBody(uploadRequestGroup.getBody());
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
		for (ContactDto recipient : recipientEmail) {
			Validate.notEmpty(recipient.getMail(), "Mail must be set");
			Contact contact = new Contact(recipient.getMail());
			UploadRequest uploadRequest = new UploadRequest(uploadRequestGroup);
			if (!uploadRequestGroup.isCollective()) {
				container = uploadRequestService.create(authUser, actor, uploadRequest, container);
				uploadRequestGroup.getUploadRequests().add(uploadRequest);
			} else {
				uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
			}
			container = uploadRequestUrlService.create(uploadRequest, contact, container);
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
}
