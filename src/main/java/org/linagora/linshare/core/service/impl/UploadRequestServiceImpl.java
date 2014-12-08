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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestGroupBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestHistoryBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestTemplateBusinessService;
import org.linagora.linshare.core.domain.constants.UploadRequestHistoryEventType;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestHistory;
import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class UploadRequestServiceImpl implements UploadRequestService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadRequestServiceImpl.class);

	private final AccountRepository<Account> accountRepository;
	private final UploadRequestBusinessService uploadRequestBusinessService;
	private final UploadRequestGroupBusinessService uploadRequestGroupBusinessService;
	private final UploadRequestHistoryBusinessService uploadRequestHistoryBusinessService;
	private final UploadRequestTemplateBusinessService uploadRequestTemplateBusinessService;
	private final UploadRequestUrlService uploadRequestUrlService;
	private final DomainPermissionBusinessService domainPermissionBusinessService;
	private final MailBuildingService mailBuildingService;
	private final NotifierService notifierService;
	private final FunctionalityReadOnlyService functionalityService;

	public UploadRequestServiceImpl(
			final AccountRepository<Account> accountRepository,
			final UploadRequestBusinessService uploadRequestBusinessService,
			final UploadRequestGroupBusinessService uploadRequestGroupBusinessService,
			final UploadRequestHistoryBusinessService uploadRequestHistoryBusinessService,
			final UploadRequestTemplateBusinessService uploadRequestTemplateBusinessService,
			final UploadRequestUrlService uploadRequestUrlService,
			final DomainPermissionBusinessService domainPermissionBusinessService,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService,
			final FunctionalityReadOnlyService functionalityReadOnlyService) {
		this.accountRepository = accountRepository;
		this.uploadRequestBusinessService = uploadRequestBusinessService;
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
		this.uploadRequestHistoryBusinessService = uploadRequestHistoryBusinessService;
		this.uploadRequestTemplateBusinessService = uploadRequestTemplateBusinessService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.functionalityService = functionalityReadOnlyService;
	}

	@Override
	public List<UploadRequest> findAllRequest(User actor) {
		return uploadRequestBusinessService.findAll(actor);
	}

	@Override
	public UploadRequest findRequestByUuid(Account actor, String uuid)
			throws BusinessException {
		UploadRequest ret = uploadRequestBusinessService.findByUuid(uuid);

		if (ret == null) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_NOT_FOUND,
					"Upload request not found. Uuid: " + uuid);
		}
		return ret;
	}

	@Override
	public UploadRequest createRequest(Account actor, User owner,
			UploadRequest req, Contact contact, String subject, String body)
			throws BusinessException {
		return createRequest(actor, owner, req, Lists.newArrayList(contact),
				subject, body);
	}

	@Override
	public UploadRequest createRequest(Account actor, User owner,
			UploadRequest req, List<Contact> contacts, String subject,
			String body) throws BusinessException {
		UploadRequestGroup group = uploadRequestGroupBusinessService
				.create(new UploadRequestGroup(subject, body));
		AbstractDomain domain = owner.getDomain();
		req.setOwner(owner);
		req.setStatus(UploadRequestStatus.STATUS_CREATED);
		req.setAbstractDomain(domain);
		req.setUploadRequestGroup(group);

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

		UploadRequestHistory hist = new UploadRequestHistory(req,
				UploadRequestHistoryEventType.EVENT_CREATED);
		req.getUploadRequestHistory().add(hist);
		req = uploadRequestBusinessService.create(req);

		List<MailContainerWithRecipient> mails = Lists.newArrayList();
		for (Contact c : contacts) {
			UploadRequestUrl requestUrl = uploadRequestUrlService
					.create(req, c);
			if (!DateUtils.isSameDay(req.getActivationDate(), req.getCreationDate())) {
				mails.add(mailBuildingService.buildCreateUploadRequest(
						(User) req.getOwner(), requestUrl));
			}
		}
		notifierService.sendNotification(mails);
		mails.clear();
		req = uploadRequestBusinessService.findByUuid(req.getUuid());
		if (req.getActivationDate().before(new Date())) {
			try {
				req.updateStatus(UploadRequestStatus.STATUS_ENABLED);
				updateRequest(actor, req);
				for (UploadRequestUrl u: req.getUploadRequestURLs()) {
					mails.add(mailBuildingService.buildActivateUploadRequest((User) req.getOwner(), u));
				}
				notifierService.sendNotification(mails);
			} catch (BusinessException e) {
				logger.error("Fail to update upload request status of the request : " + req.getUuid());
			}
		}
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
		Date notifDate =  checkDate(funcNotify, req.getExpiryDate()); // Must have a setted value in order to return a value not null
		req.setNotificationDate(notifDate);
	}

	private void checkNotificationLanguage(AbstractDomain domain,
			UploadRequest req) {
		StringValueFunctionality func = functionalityService
				.getUploadRequestNotificationLanguageFunctionality(domain);
		String checkString = checkString(func, req.getLocale());
		req.setLocale(checkString);
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

	private String checkString(StringValueFunctionality func,
			String current) {
		if (func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is activated");
			String defaultValue = func.getValue();
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
						logger.warn("the current value "
								+ current.toString()
								+ " is different than system value " + defaultValue);
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

	private Date checkDate(TimeUnitValueFunctionality func, Date currentDate) {
		if (func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is activated");
			@SuppressWarnings("deprecation")
			Date maxDate = DateUtils.add(new Date(),
					func.toCalendarUnitValue(), func.getValue());
			if (func.getDelegationPolicy() != null
					&& func.getDelegationPolicy().getStatus()) {
				logger.debug(func.getIdentifier() + " has a delegation policy");
				if(currentDate!=null) {
					if (!(currentDate.before(maxDate))) {
						//	if (!(currentDate.after(new Date()) && currentDate.before(maxDate))) {
						logger.warn("the current value " + currentDate.toString()
								+ " is out of range : " + func.toString());
						return maxDate;
					}
				}
				return currentDate;
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

	@Override
	public UploadRequest updateRequest(Account actor, UploadRequest req)
			throws BusinessException {
		if(req.getUploadRequestHistory() != null && Lists.newArrayList(req
				.getUploadRequestHistory()) != null && ! Lists.newArrayList(req
						.getUploadRequestHistory()).isEmpty()) {
			UploadRequestHistory last = Collections.max(Lists.newArrayList(req
					.getUploadRequestHistory()));
		UploadRequestHistory hist = new UploadRequestHistory(req,
				UploadRequestHistoryEventType.fromStatus(req.getStatus()),
				!last.getStatus().equals(req.getStatus()));
		req.getUploadRequestHistory().add(hist);
		}
		else {
			UploadRequestHistory hist = new UploadRequestHistory(req,
					UploadRequestHistoryEventType.fromStatus(req.getStatus()),
					false);
			req.getUploadRequestHistory().add(hist);
		}
		return uploadRequestBusinessService.update(req);
	}

	@Override
	public UploadRequest closeRequestByRecipient(UploadRequestUrl url)
			throws BusinessException {
		Account actor = accountRepository.getUploadRequestSystemAccount();
		UploadRequest req = url.getUploadRequest();
		if (req.getStatus().equals(UploadRequestStatus.STATUS_CLOSED)) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"Closing an already closed upload request url : "
							+ req.getUuid());
		}
		if (!domainPermissionBusinessService.isAdminForThisUploadRequest(actor,
				req)) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"you do not have the right to close this upload request url : "
							+ req.getUuid());
		}
		req.updateStatus(UploadRequestStatus.STATUS_CLOSED);
		UploadRequest update = updateRequest(actor, req);
		MailContainerWithRecipient mail = mailBuildingService
				.buildCloseUploadRequestByRecipient((User) req.getOwner(), url);
		notifierService.sendNotification(mail);
		return update;
	}

	@Override
	public void deleteRequest(Account actor, UploadRequest req)
			throws BusinessException {
		uploadRequestBusinessService.delete(req);
	}

	@Override
	public UploadRequestGroup findRequestGroupByUuid(Account actor, String uuid) {
		return uploadRequestGroupBusinessService.findByUuid(uuid);
	}

	@Override
	public UploadRequestGroup updateRequestGroup(Account actor,
			UploadRequestGroup group) throws BusinessException {
		return uploadRequestGroupBusinessService.update(group);
	}

	@Override
	public void deleteRequestGroup(Account actor, UploadRequestGroup group)
			throws BusinessException {
		uploadRequestGroupBusinessService.delete(group);
	}

	@Override
	public Set<UploadRequestHistory> findAllRequestHistory(Account actor,
			String uploadRequestUuid) throws BusinessException {
		UploadRequest request = findRequestByUuid(actor, uploadRequestUuid);

		if (!domainPermissionBusinessService.isAdminForThisUploadRequest(actor,
				request)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
					"Upload request history search forbidden");
		}
		return request.getUploadRequestHistory();
	}

	@Override
	public Set<UploadRequest> findAll(Account actor,
			List<UploadRequestStatus> status, Date afterDate, Date beforeDate)
			throws BusinessException {
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
					"Upload request history search forbidden");
		}
		if (afterDate == null) {
			afterDate = DateUtils.addMonths(new Date(), -1);
		}
		if (beforeDate == null) {
			beforeDate = new Date();
		}
		if (afterDate.after(beforeDate)) {
			throw new BusinessException(BusinessErrorCode.BAD_REQUEST,
					"Min date limit after max date limit");
		}
		List<AbstractDomain> myAdministredDomains = domainPermissionBusinessService
				.getMyAdministredDomains(actor);
		return new HashSet<UploadRequest>(uploadRequestBusinessService.findAll(
				myAdministredDomains, status, afterDate, beforeDate));
	}

	@Override
	public UploadRequestHistory findRequestHistoryByUuid(Account actor,
			String uuid) {
		return uploadRequestHistoryBusinessService.findByUuid(uuid);
	}

	@Override
	public UploadRequestHistory createRequestHistory(Account actor,
			UploadRequestHistory history) throws BusinessException {
		return uploadRequestHistoryBusinessService.create(history);
	}

	@Override
	public UploadRequestHistory updateRequestHistory(Account actor,
			UploadRequestHistory history) throws BusinessException {
		return uploadRequestHistoryBusinessService.update(history);
	}

	@Override
	public void deleteRequestHistory(Account actor, UploadRequestHistory history)
			throws BusinessException {
		uploadRequestHistoryBusinessService.delete(history);
	}

	@Override
	public UploadRequestTemplate findTemplateByUuid(Account actor,
			String uuid) throws BusinessException {
		UploadRequestTemplate ret = uploadRequestTemplateBusinessService
				.findByUuid(uuid);
		if (ret == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"UploadRequestTemplate with uuid: " + uuid);
		}
		return ret;
	}

	@Override
	public UploadRequestTemplate createTemplate(Account actor,
			UploadRequestTemplate template) throws BusinessException {
		return uploadRequestTemplateBusinessService.create(actor, template);
	}

	@Override
	public UploadRequestTemplate updateTemplate(Account actor,
			UploadRequestTemplate template) throws BusinessException {
		return uploadRequestTemplateBusinessService.update(template);
	}

	@Override
	public void deleteTemplate(Account actor, UploadRequestTemplate template) throws BusinessException {
		uploadRequestTemplateBusinessService.delete(template);
	}
}
