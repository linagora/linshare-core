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
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestHistory;
import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;

import com.google.common.collect.Lists;

public class UploadRequestServiceImpl implements UploadRequestService {

	private final AccountRepository<Account> accountRepository;
	private final UploadRequestBusinessService uploadRequestBusinessService;
	private final UploadRequestGroupBusinessService uploadRequestGroupBusinessService;
	private final UploadRequestHistoryBusinessService uploadRequestHistoryBusinessService;
	private final UploadRequestTemplateBusinessService uploadRequestTemplateBusinessService;
	private final UploadRequestUrlService uploadRequestUrlService;
	private final DomainPermissionBusinessService domainPermissionBusinessService;
	private final MailBuildingService mailBuildingService;
	private final NotifierService notifierService;

	public UploadRequestServiceImpl(
			final AccountRepository<Account> accountRepository,
			final UploadRequestBusinessService uploadRequestBusinessService,
			final UploadRequestGroupBusinessService uploadRequestGroupBusinessService,
			final UploadRequestHistoryBusinessService uploadRequestHistoryBusinessService,
			final UploadRequestTemplateBusinessService uploadRequestTemplateBusinessService,
			final UploadRequestUrlService uploadRequestUrlService,
			final DomainPermissionBusinessService domainPermissionBusinessService,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService) {
		this.accountRepository = accountRepository;
		this.uploadRequestBusinessService = uploadRequestBusinessService;
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
		this.uploadRequestHistoryBusinessService = uploadRequestHistoryBusinessService;
		this.uploadRequestTemplateBusinessService = uploadRequestTemplateBusinessService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
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
	public UploadRequest createRequest(Account actor, UploadRequest req, Contact contact) throws BusinessException {
		return createRequest(actor, req, Lists.newArrayList(contact));
	}
	@Override
	public UploadRequest createRequest(Account actor, UploadRequest req, List<Contact> contacts)
			throws BusinessException {
		req.setStatus(UploadRequestStatus.STATUS_CREATED);
		UploadRequestHistory hist = new UploadRequestHistory(req,
				UploadRequestHistoryEventType.EVENT_CREATED);
		req.getUploadRequestHistory().add(hist);
		req.setOwner(actor);
		req.setAbstractDomain(actor.getDomain());
		req = uploadRequestBusinessService.create(req);
		List<MailContainerWithRecipient> mails = Lists.newArrayList();
		for (Contact c: contacts) {
			UploadRequestUrl requestUrl = uploadRequestUrlService.create(req, c);
			mails.add(mailBuildingService.buildCreateUploadRequest((User) req.getOwner(), requestUrl));
		}
		notifierService.sendNotification(mails);
		return req;
	}

	@Override
	public UploadRequest updateRequest(Account actor, UploadRequest req)
			throws BusinessException {
		UploadRequestHistory last = Collections
				.max(Lists.newArrayList(req.getUploadRequestHistory()));
		UploadRequestHistory hist = new UploadRequestHistory(req,
				UploadRequestHistoryEventType.fromStatus(req.getStatus()),
				!last.getStatus().equals(req.getStatus()));
		req.getUploadRequestHistory().add(hist);
		return uploadRequestBusinessService.update(req);
	}

	@Override
	public UploadRequest closeRequestByRecipient(UploadRequestUrl url) throws BusinessException {
		Account actor = accountRepository.getUploadRequestSystemAccount();
		UploadRequest req = url.getUploadRequest();
		if (req.getStatus().equals(UploadRequestStatus.STATUS_CLOSED)) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"Closing an already closed upload request url : " + req.getUuid());
		}
		if (!domainPermissionBusinessService.isAdminForThisUploadRequest(actor, req)) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"you do not have the right to close this upload request url : "
							+ req.getUuid());
		}
		req.updateStatus(UploadRequestStatus.STATUS_CLOSED);
		UploadRequest update = updateRequest(actor, req);
		MailContainerWithRecipient mail = mailBuildingService.buildCloseUploadRequestByRecipient((User) req.getOwner(), url);
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
	public UploadRequestGroup createRequestGroup(Account actor,
			UploadRequestGroup group) throws BusinessException {
		UploadRequestGroup requestGroup = uploadRequestGroupBusinessService.create(group);
		List<MailContainerWithRecipient> mails = Lists.newArrayList();
		for (UploadRequest request : requestGroup.getUploadRequests()) {
			for (UploadRequestUrl url : request.getUploadRequestURLs()) {
				mails.add(mailBuildingService.buildCreateUploadRequest((User) request.getOwner(), url));
			}
		}
		notifierService.sendNotification(mails);
		return requestGroup;
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

		if (!domainPermissionBusinessService.isAdminForThisUploadRequest(actor, request)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
					"Upload request history search forbidden");
		}
		return request.getUploadRequestHistory();
	}

	@Override
	public Set<UploadRequest> findAll(Account actor,
			List<UploadRequestStatus> status, Date afterDate, Date beforeDate) throws BusinessException {
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
	public UploadRequestHistory findRequestHistoryByUuid(Account actor, String uuid) {
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
	public void deleteTemplate(Account actor, UploadRequestTemplate template)
			throws BusinessException {
		uploadRequestTemplateBusinessService.delete(template);
	}
}
