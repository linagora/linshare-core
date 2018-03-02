/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.UploadRequestGroupBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.LanguageEnumValueFunctionality;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.UploadRequestContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.UploadRequestGroupResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
import org.linagora.linshare.mongo.entities.logs.UploadRequestGroupAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadRequestGroupServiceImpl extends GenericServiceImpl<Account, UploadRequestGroup> implements UploadRequestGroupService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadRequestGroupServiceImpl.class);

	private final UploadRequestGroupBusinessService uploadRequestGroupBusinessService;

	private final UploadRequestGroupResourceAccessControl groupRac;
	private final FunctionalityReadOnlyService functionalityService;
	private final UploadRequestUrlService uploadRequestUrlService;
	private final MailBuildingService mailBuildingService;
	private final NotifierService notifierService;
	private final LogEntryService logEntryService;
	private final UploadRequestService uploadRequestService;

	public UploadRequestGroupServiceImpl(
			final UploadRequestGroupBusinessService uploadRequestGroupBusinessService,
			final UploadRequestGroupResourceAccessControl groupRac,
			final FunctionalityReadOnlyService functionalityService,
			final UploadRequestUrlService uploadRequestUrlService,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService,
			final LogEntryService logEntryService,
			final UploadRequestService uploadRequestService) {
		super(groupRac);
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
		this.groupRac = groupRac;
		this.functionalityService = functionalityService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.logEntryService = logEntryService;
		this.uploadRequestService = uploadRequestService;
	}

	@Override
	public List<UploadRequestGroup> findAllGroupRequest(Account actor, Account owner, List<UploadRequestStatus> statusList)
			throws BusinessException {
		preChecks(actor, owner);
		groupRac.checkListPermission(actor, owner, UploadRequestGroup.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
				null);
		return uploadRequestGroupBusinessService.findAll(owner, statusList);
	}

	@Override
	public UploadRequestGroup findRequestGroupByUuid(Account actor, Account owner, String uuid) {
		preChecks(actor, owner);
		UploadRequestGroup req = uploadRequestGroupBusinessService.findByUuid(uuid);
		groupRac.checkReadPermission(actor,
				req.getOwner(), UploadRequestGroup.class,
				BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, req);
		return req;
	}

	@Override
	public List<UploadRequest> createRequest(Account actor, User owner,
			UploadRequest inputRequest, List<Contact> contacts, String subject,
			String body, Boolean groupedMode) throws BusinessException {
		checkCreatePermission(actor, owner, UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, null);
		AbstractDomain domain = owner.getDomain();
		BooleanValueFunctionality groupedFunc = functionalityService.getUploadRequestGroupedFunctionality(domain);
		boolean groupedModeLocal = groupedFunc.getValue();
		if (groupedFunc.getActivationPolicy().getStatus()) {
			if (groupedFunc.getDelegationPolicy().getStatus()) {
				if (groupedMode != null) {
					groupedModeLocal = groupedMode;
				}
			}
		} else {
			groupedModeLocal = false;
		}
		UploadRequest req = initUploadRequest(owner, inputRequest);
		UploadRequestGroup uploadRequestGroup = new UploadRequestGroup(owner, domain, subject, body,
				req.getActivationDate(), req.isCanDelete(), req.isCanClose(),
				req.isCanEditExpiryDate(), req.getLocale(), req.isSecured(),req.getEnableNotification(),
				!groupedModeLocal, req.getStatus(),req.getExpiryDate(),req.getNotificationDate(),
				req.getMaxFileCount(), req.getMaxDepositSize(), req.getMaxFileSize());
		uploadRequestGroup = uploadRequestGroupBusinessService.create(uploadRequestGroup);
		UploadRequestContainer container = new UploadRequestContainer();
		req.setUploadRequestGroup(uploadRequestGroup);
		if (groupedModeLocal) {
			uploadRequestService.create(actor, owner, req, container);
			for (Contact contact : contacts) {
				container = uploadRequestUrlService.create(container.getUploadRequests().get(0), contact, container);
			}
		} else {
			for (Contact contact : contacts) {
				UploadRequest clone = req.clone();
				container = uploadRequestService.create(actor, owner, clone, container);
				container = uploadRequestUrlService.create(container.getUploadRequests().get(0), contact, container);
			}
		}
		// TODO move this logs
//		List<AuditLogEntryUser> log = Lists.newArrayList();
//		for (UploadRequest r : requests) {
//			log.add(new UploadRequestAuditLogEntry(new AccountMto(actor),
//				new AccountMto(owner), LogAction.CREATE, AuditLogEntryType.UPLOAD_REQUEST,
//				r.getUuid(), r));
//		}
//		logEntryService.insert(log);
		notifierService.sendNotification(container.getMailContainers());
		UploadRequestGroupAuditLogEntry groupLog = new UploadRequestGroupAuditLogEntry(new AccountMto(actor),
				new AccountMto(owner), LogAction.CREATE, AuditLogEntryType.UPLOAD_REQUEST_GROUP,
				uploadRequestGroup.getUuid(), uploadRequestGroup);
		logEntryService.insert(groupLog);
		return container.getUploadRequests();
	}

	private UploadRequest initUploadRequest(User owner, UploadRequest req) {
		AbstractDomain domain = owner.getDomain();
		UploadRequestStatus status = req.getActivationDate().after(new Date())?UploadRequestStatus.CREATED:UploadRequestStatus.ENABLED;
		req.setStatus(status);
		checkActivationDate(domain, req);
		checkExpiryAndNoticationDate(domain, req);
		checkMaxDepositSize(domain, req);
		checkMaxFileCount(domain, req);
		checkMaxFileSize(domain, req);
		checkNotificationLanguage(domain, req);
		checkCanDelete(domain, req);
		checkCanClose(domain, req);
		checkSecuredUrl(domain, req);
		Functionality func = functionalityService.getUploadRequestProlongationFunctionality(domain);
		req.setCanEditExpiryDate(func.getActivationPolicy().getStatus());
		return req;
	}

	private void checkSecuredUrl(AbstractDomain domain, UploadRequest req) {
		BooleanValueFunctionality func = functionalityService
				.getUploadRequestSecureUrlFunctionality(domain);
		boolean secure = checkBoolean(func, req.isSecured());
		req.setSecured(secure);
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
		Date checkDate = checkDate(func, req.getActivationDate());
		if (checkDate == null) {
			checkDate = new Date();
		}
		req.setActivationDate(checkDate);
	}

	private void checkExpiryAndNoticationDate(AbstractDomain domain,
			UploadRequest req) {
		TimeUnitValueFunctionality funcExpiry = functionalityService
				.getUploadRequestExpiryTimeFunctionality(domain);
		TimeUnitValueFunctionality funcNotify = functionalityService
				.getUploadRequestNotificationTimeFunctionality(domain);
		Date expiryDate = checkDate(funcExpiry, req.getExpiryDate());
		req.setExpiryDate(expiryDate);
		Date notifDate =  checkNotificationDate(funcNotify, req.getNotificationDate(), req.getExpiryDate()); // Must have a setted value in order to return a value not null
		req.setNotificationDate(notifDate);
	}

	private void checkNotificationLanguage(AbstractDomain domain,
			UploadRequest req) {
		LanguageEnumValueFunctionality func = functionalityService
				.getUploadRequestNotificationLanguageFunctionality(domain);
		Language userLocale = Language.fromTapestryLocale(req.getLocale());
		Language checkLanguage = checkLanguage(func, userLocale);
		req.setLocale(checkLanguage.getTapestryLocale());
	}

	private void checkMaxFileSize(AbstractDomain domain, UploadRequest req) {
		SizeUnitValueFunctionality func = functionalityService
				.getUploadRequestMaxFileSizeFunctionality(domain);
		Long checkSize = checkSize(func, req.getMaxFileSize());
		req.setMaxFileSize(checkSize);
	}

	private void checkMaxFileCount(AbstractDomain domain, UploadRequest req) {
		IntegerValueFunctionality func = functionalityService
				.getUploadRequestMaxFileCountFunctionality(domain);
		Integer checkInteger = checkInteger(func, req.getMaxFileCount());
		req.setMaxFileCount(checkInteger);
	}

	private void checkMaxDepositSize(AbstractDomain domain, UploadRequest req) {
		SizeUnitValueFunctionality func = functionalityService
				.getUploadRequestMaxDepositSizeFunctionality(domain);
		Long checkSize = checkSize(func, req.getMaxDepositSize());
		req.setMaxDepositSize(checkSize);
	}
	
	private Long checkSize(SizeUnitValueFunctionality func, Long currentSize) {
		if (func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is activated");
			long maxSize = ((FileSizeUnitClass) func.getUnit())
					.getPlainSize(func.getValue());
			if (func.getDelegationPolicy() != null
					&& func.getDelegationPolicy().getStatus()) {
				logger.debug(func.getIdentifier() + " has a delegation policy");
				if (currentSize != null) {
					if (!(currentSize > 0 && currentSize <= maxSize)) {
						logger.warn("the current value " + currentSize.toString()
								+ " is out of range : " + func.toString());
						return maxSize;
					}
					return currentSize;
				}
				return maxSize;
			} else {
				// there is no delegation, the current value should be the
				// system value or null
				logger.debug(func.getIdentifier()
						+ " does not have a delegation policy");
				if (currentSize != null) {
					if (!currentSize.equals(maxSize)) {
						logger.warn("the current value "
								+ currentSize.toString()
								+ " is different than system value " + maxSize);
					}
				}
				return maxSize;
			}
		} else {
			logger.debug(func.getIdentifier() + " is not activated");
			if (currentSize != null) {
				logger.warn("the current value " + currentSize.toString()
						+ " should be null for the functionality "
						+ func.toString());
			}
			return null;
		}
	}

	private Integer checkInteger(IntegerValueFunctionality func,
			Integer currentSize) {
		if (func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is activated");
			int maxSize = func.getValue();
			if (func.getDelegationPolicy() != null
					&& func.getDelegationPolicy().getStatus()) {
				logger.debug(func.getIdentifier() + " has a delegation policy");
				if (currentSize != null) {
					if (!(currentSize > 0 && currentSize <= maxSize)) {
						logger.warn("the current value " + currentSize.toString()
								+ " is out of range : " + func.toString());
						return maxSize;
					}
					return currentSize;
				}
				return maxSize;
			} else {
				// there is no delegation, the current value should be the
				// system value or null
				logger.debug(func.getIdentifier()
						+ " does not have a delegation policy");
				if (currentSize != null) {
					if (!currentSize.equals(maxSize)) {
						logger.warn("the current value "
								+ currentSize.toString()
								+ " is different than system value " + maxSize);
					}
				}
				return maxSize;
			}
		} else {
			logger.debug(func.getIdentifier() + " is not activated");
			if (currentSize != null) {
				logger.warn("the current value " + currentSize.toString()
						+ " should be null for the functionality "
						+ func.toString());
			}
			return null;
		}
	}

	/**
	 * Check if the current input date is after now and not before now more the
	 * functionality duration if delegation policy allowed it.
	 * now() < currentDate <  now() + func.value
	 * Otherwise functionality value is used as default value.
	 * @param func
	 * @param currentDate
	 * @return the proper date is returned according to activation policy,
	 * configuration policy and others checks.
	 */
	private Date checkDate(TimeUnitValueFunctionality func, Date currentDate) {
		if (func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is activated");
			Calendar c = new GregorianCalendar();
			c.add(func.toCalendarValue(), func.getValue());
			Date maxDate = c.getTime();
			if (func.getDelegationPolicy() != null
					&& func.getDelegationPolicy().getStatus()) {
				logger.debug(func.getIdentifier() + " has a delegation policy");
				if (currentDate != null) {
					if (currentDate.after(maxDate)
							|| currentDate.before(new Date())) {
						logger.warn("the current value "
								+ currentDate.toString()
								+ " is out of range : " + func.toString()
								+ " : " + maxDate.toString());
						return maxDate;
					} else {
						return currentDate;
					}
				}
				return maxDate;
			} else {
				// there is no delegation, the current value should be the
				// system value or null
				logger.debug(func.getIdentifier()
						+ " does not have a delegation policy");
				if (currentDate != null) {
					if (!currentDate.equals(maxDate)) {
						logger.warn("the current value "
								+ currentDate.toString()
								+ " is different than system value " + maxDate);
					}
				}
				return maxDate;
			}
		} else {
			logger.debug(func.getIdentifier() + " is not activated");
			if (currentDate != null) {
				logger.warn("the current value " + currentDate.toString()
						+ " should be null for the functionality "
						+ func.toString());
			}
			return null;
		}
	}

	/**
	 * Check if the current input date is after now and not before now more the
	 * functionality duration if delegation policy allowed it.
	 * now() < currentDate <  now() + func.value
	 * Otherwise functionality value is used as default value.
	 * @param func
	 * @param currentDate
	 * @param expirationDate TODO
	 * @return the proper date is returned according to activation policy,
	 * configuration policy and others checks.
	 */
	private Date checkNotificationDate(TimeUnitValueFunctionality func, Date currentDate, Date expirationDate) {
		if (func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is activated");
			Calendar c = new GregorianCalendar();
			c.setTime(expirationDate);
			c.add(func.toCalendarValue(), - func.getValue());
			Date minDate = c.getTime();
			if (func.getDelegationPolicy() != null
					&& func.getDelegationPolicy().getStatus()) {
				logger.debug(func.getIdentifier() + " has a delegation policy");
				if (currentDate != null) {
					if (!(currentDate.before(expirationDate) && currentDate.after(minDate))) {
						//	if (!(currentDate.after(new Date()) && currentDate.before(maxDate))) {
						logger.warn("the current value " + currentDate.toString()
								+ " is out of range : " + func.toString());
						return minDate;
					}
					return currentDate;
				} else {
					return minDate;
				}
			} else {
				// there is no delegation, the current value should be the
				// system value or null
				logger.debug(func.getIdentifier()
						+ " does not have a delegation policy");
				if (currentDate != null) {
					if (!currentDate.equals(minDate)) {
						logger.warn("the current value "
								+ currentDate.toString()
								+ " is different than system value " + minDate);
					}
				}
				return minDate;
			}
		} else {
			logger.debug(func.getIdentifier() + " is not activated");
			if (currentDate != null) {
				logger.warn("the current value " + currentDate.toString()
						+ " should be null for the functionality "
						+ func.toString());
			}
			return null;
		}
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
		groupRac.checkUpdatePermission(authUser, actor, UploadRequestGroup.class,
				BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, uploadRequestGroup);
		uploadRequestGroup = uploadRequestGroupBusinessService.updateStatus(uploadRequestGroup, status);
		for (UploadRequest uploadRequest : uploadRequestGroup.getUploadRequests()) {
			if (status.compareTo(uploadRequest.getStatus()) < 0) {
				uploadRequestService.updateStatus(authUser, actor, uploadRequest.getUuid(), status, copy);
			}
		}
		// TODO add audit
		return uploadRequestGroup;
	}

	public UploadRequestGroup update(User authUser, User actor, UploadRequestGroup uploadRequestGroup) {
		checkUpdatePermission(authUser, actor, UploadRequestGroup.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
				null);
		UploadRequestGroup group = uploadRequestGroupBusinessService.findByUuid(uploadRequestGroup.getUuid());
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
			if (!uploadRequest.getDirty()) {
				setUploadRequest(uploadRequest, group);
				uploadRequestService.updateRequest(authUser, actor, uploadRequest);
			}
		}
		uploadRequestGroup = uploadRequestGroupBusinessService.update(group);
		UploadRequestGroupAuditLogEntry groupLog = new UploadRequestGroupAuditLogEntry(new AccountMto(authUser),
				new AccountMto(actor), LogAction.UPDATE, AuditLogEntryType.UPLOAD_REQUEST_GROUP,
				uploadRequestGroup.getUuid(), uploadRequestGroup);
		logEntryService.insert(groupLog);
		return group;
	}

	private void setUploadRequest(UploadRequest uploadRequest, UploadRequestGroup group) {
		uploadRequest.setModificationDate(new Date());
		uploadRequest.setMaxFileCount(group.getMaxFileCount());
		uploadRequest.setMaxDepositSize(group.getMaxDepositSize());
		uploadRequest.setMaxFileSize(group.getMaxFileSize());
		uploadRequest.setNotificationDate(group.getNotificationDate());
		uploadRequest.setExpiryDate(group.getExpiryDate());
		uploadRequest.setCanDelete(group.getCanDelete());
		uploadRequest.setCanClose(group.getCanClose());
		uploadRequest.setCanEditExpiryDate(group.getCanEditExpiryDate());
		uploadRequest.setLocale(group.getLocale());
		uploadRequest.setEnableNotification(group.getEnableNotification());
		uploadRequest.setActivationDate(group.getActivationDate());
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
			if (uploadRequestGroup.getRestricted()) {
				container = uploadRequestService.create(authUser, actor, uploadRequest, container);
				uploadRequestGroup.getUploadRequests().add(uploadRequest);
			} else {
				uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
			}
			container = uploadRequestUrlService.create(uploadRequest, contact, container);
		}
		notifierService.sendNotification(container.getMailContainers());
		return uploadRequestGroup;
	}
}
