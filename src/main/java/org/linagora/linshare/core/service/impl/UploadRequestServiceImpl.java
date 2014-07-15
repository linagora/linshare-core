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

import com.google.common.collect.Sets;
import org.linagora.linshare.core.business.service.*;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.*;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.UploadRequestService;

import java.util.*;

public class UploadRequestServiceImpl implements UploadRequestService {

	private final AbstractDomainService abstractDomainService;
	private final UploadRequestBusinessService uploadRequestBusinessService;
	private final UploadRequestEntryBusinessService uploadRequestEntryBusinessService;
	private final UploadRequestGroupBusinessService uploadRequestGroupBusinessService;
	private final UploadRequestHistoryBusinessService uploadRequestHistoryBusinessService;
	private final UploadRequestTemplateBusinessService uploadRequestTemplateBusinessService;
	private final UploadRequestUrlBusinessService uploadRequestUrlBusinessService;
	private final DomainPermissionBusinessService domainPermissionBusinessService;

	public UploadRequestServiceImpl(
			final AbstractDomainService abstractDomainService,
			final UploadRequestBusinessService uploadRequestBusinessService,
			final UploadRequestEntryBusinessService uploadRequestEntryBusinessService,
			final UploadRequestGroupBusinessService uploadRequestGroupBusinessService,
			final UploadRequestHistoryBusinessService uploadRequestHistoryBusinessService,
			final UploadRequestTemplateBusinessService uploadRequestTemplateBusinessService,
			final UploadRequestUrlBusinessService uploadRequestUrlBusinessService,
			final DomainPermissionBusinessService domainPermissionBusinessService) {
		this.abstractDomainService = abstractDomainService;
		this.uploadRequestBusinessService = uploadRequestBusinessService;
		this.uploadRequestEntryBusinessService = uploadRequestEntryBusinessService;
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
		this.uploadRequestHistoryBusinessService = uploadRequestHistoryBusinessService;
		this.uploadRequestTemplateBusinessService = uploadRequestTemplateBusinessService;
		this.uploadRequestUrlBusinessService = uploadRequestUrlBusinessService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
	}

	@Override
	public List<UploadRequest> findAllRequest(User actor) {
		return uploadRequestBusinessService.findAll(actor);
	}

	@Override
	public UploadRequest findRequestByUuid(User actor, String uuid)
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
	public UploadRequest createRequest(User actor, UploadRequest req)
			throws BusinessException {
		req.setOwner(actor);
		req.setAbstractDomain(actor.getDomain());
		return uploadRequestBusinessService.create(req);
	}

	@Override
	public UploadRequest updateRequest(User actor, UploadRequest req)
			throws BusinessException {
		return uploadRequestBusinessService.update(req);
	}

	@Override
	public void deleteRequest(User actor, UploadRequest req)
			throws BusinessException {
		uploadRequestBusinessService.delete(req);
	}

	@Override
	public UploadRequestGroup findRequestGroupByUuid(User actor, String uuid) {
		return uploadRequestGroupBusinessService.findByUuid(uuid);
	}

	@Override
	public UploadRequestGroup createRequestGroup(User actor,
			UploadRequestGroup group) throws BusinessException {
		return uploadRequestGroupBusinessService.create(group);
	}

	@Override
	public UploadRequestGroup updateRequestGroup(User actor,
			UploadRequestGroup group) throws BusinessException {
		return uploadRequestGroupBusinessService.update(group);
	}

	@Override
	public void deleteRequestGroup(User actor, UploadRequestGroup group)
			throws BusinessException {
		uploadRequestGroupBusinessService.delete(group);
	}

	@Override
	public Set<UploadRequestHistory> findAllRequestHistory(Account actor,
			String uploadRequestUuid) throws BusinessException {
		UploadRequest request = uploadRequestBusinessService
				.findByUuid(uploadRequestUuid);
		if (request == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"upload request not found : " + uploadRequestUuid);
		}
		if (!(domainPermissionBusinessService.isAdminforThisDomain(actor,
				request.getAbstractDomain()) || actor
				.equals(request.getOwner()))) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_UNAUTHORISED,
					"Unauthorized upload request history search");
		}
		return request.getUploadRequestHistory();
	}

	@Override
	public Set<UploadRequest> findAll(Account actor,
			List<UploadRequestStatus> status, Date afterDate, Date beforeDate) throws BusinessException {
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_UNAUTHORISED,
					"Unauthorized upload request history search");
		}
		if (afterDate == null) {
			Date referenceDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(referenceDate);
			c.add(Calendar.MONTH, -1);
			afterDate = c.getTime();
		}
		if (beforeDate == null) {
			beforeDate = new Date();
		}
		if (afterDate.after(beforeDate)) {
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FAULT,
					"min date limit after max date limit");
		};
		List<AbstractDomain> myAdministredDomains = domainPermissionBusinessService
				.getMyAdministredDomains(actor);
		return new HashSet<UploadRequest>(uploadRequestBusinessService.findAll(
				myAdministredDomains, status, afterDate, beforeDate));
	}

	@Override
	public UploadRequestHistory findRequestHistoryByUuid(User actor, String uuid) {
		return uploadRequestHistoryBusinessService.findByUuid(uuid);
	}

	@Override
	public UploadRequestHistory createRequestHistory(Account actor,
			UploadRequestHistory history) throws BusinessException {
		return uploadRequestHistoryBusinessService.create(history);
	}

	@Override
	public UploadRequestHistory updateRequestHistory(User actor,
			UploadRequestHistory history) throws BusinessException {
		return uploadRequestHistoryBusinessService.update(history);
	}

	@Override
	public void deleteRequestHistory(User actor, UploadRequestHistory history)
			throws BusinessException {
		uploadRequestHistoryBusinessService.delete(history);
	}

	@Override
	public UploadRequestTemplate findRequestTemplateByUuid(User actor,
			String uuid) {
		return uploadRequestTemplateBusinessService.findByUuid(uuid);
	}

	@Override
	public UploadRequestTemplate createRequestTemplate(User actor,
			UploadRequestTemplate template) throws BusinessException {
		return uploadRequestTemplateBusinessService.create(template);
	}

	@Override
	public UploadRequestTemplate updateRequestTemplate(User actor,
			UploadRequestTemplate template) throws BusinessException {
		return uploadRequestTemplateBusinessService.update(template);
	}

	@Override
	public void deleteRequestTemplate(User actor, UploadRequestTemplate template)
			throws BusinessException {
		uploadRequestTemplateBusinessService.delete(template);
	}

	@Override
	public UploadRequestUrl findRequestUrlByUuid(User actor, String uuid) {
		return uploadRequestUrlBusinessService.findByUuid(uuid);
	}

	@Override
	public UploadRequestUrl createRequestUrl(User actor, UploadRequestUrl url)
			throws BusinessException {
		return uploadRequestUrlBusinessService.create(url);
	}

	@Override
	public UploadRequestUrl updateRequestUrl(User actor, UploadRequestUrl url)
			throws BusinessException {
		return uploadRequestUrlBusinessService.update(url);
	}

	@Override
	public void deleteRequestUrl(User actor, UploadRequestUrl url)
			throws BusinessException {
		uploadRequestUrlBusinessService.delete(url);
	}

	@Override
	public UploadRequestEntry findRequestEntryByUuid(Account actor, String uuid) {
		return uploadRequestEntryBusinessService.findByUuid(uuid);
	}

	@Override
	public UploadRequestEntry createRequestEntry(Account actor,
			UploadRequestEntry entry) throws BusinessException {
		return uploadRequestEntryBusinessService.create(entry);
	}

	@Override
	public UploadRequestEntry updateRequestEntry(Account actor,
			UploadRequestEntry entry) throws BusinessException {
		return uploadRequestEntryBusinessService.update(entry);
	}

	@Override
	public void deleteRequestEntry(Account actor, UploadRequestEntry entry)
			throws BusinessException {
		uploadRequestEntryBusinessService.delete(entry);
	}

	/**
	 * business methods.
	 */

	@Override
	public UploadRequest setStatusToClosed(Account actor, UploadRequest req)
			throws BusinessException {
		// TODO : notifications, log history
		if (actor.hasSystemAccountRole() || actor.equals(req.getOwner())
				|| actor.hasSuperAdminRole()) {
			req.updateStatus(UploadRequestStatus.STATUS_CLOSED);
			// FIXME : it works without updating the entity. It does not work if
			// we do. ! :(
			// uploadRequestBusinessService.update(req);
			// UploadRequestHistory history = createRequestHistory(actor, new
			// UploadRequestHistory(req,
			// UploadRequestHistoryEventType.EVENT_CLOSED, true));
			// req.getUploadRequestHistory().add(history);
			// uploadRequestBusinessService.update(req);
			return req;
		} else {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"you do not have the right to close this upload request url : "
							+ req.getUuid());
		}
	}
}
