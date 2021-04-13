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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestGroupBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.TargetKind;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.UploadRequestContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestActivationEmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestCloseByOwnerEmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestClosedByRecipientEmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestUpdateSettingsEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.UploadRequestResourceAccessControl;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.UploadRequestAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.UploadRequestMto;

import com.google.common.collect.Lists;

public class UploadRequestServiceImpl extends GenericServiceImpl<Account, UploadRequest> implements UploadRequestService {

	private final AccountRepository<Account> accountRepository;

	private final UploadRequestBusinessService uploadRequestBusinessService;

	private final DomainPermissionBusinessService domainPermissionBusinessService;

	private final MailBuildingService mailBuildingService;

	private final NotifierService notifierService;

	private final LogEntryService logEntryService;

	private final UploadRequestEntryService uploadRequestEntryService;

	private final UploadRequestGroupBusinessService uploadRequestGroupBusinessService;

	public UploadRequestServiceImpl(
			final AccountRepository<Account> accountRepository,
			final UploadRequestBusinessService uploadRequestBusinessService,
			final DomainPermissionBusinessService domainPermissionBusinessService,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService,
			final UploadRequestResourceAccessControl rac,
			final LogEntryService logEntryService,
			final UploadRequestEntryService uploadRequestEntryService,
			final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			final UploadRequestGroupBusinessService uploadRequestGroupBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.accountRepository = accountRepository;
		this.uploadRequestBusinessService = uploadRequestBusinessService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.logEntryService = logEntryService;
		this.uploadRequestEntryService = uploadRequestEntryService;
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
	}

	@Override
	public List<UploadRequest> findAll(Account actor, Account owner, UploadRequestGroup uploadRequestGroup, List<UploadRequestStatus> statusList) {
		Validate.notNull(uploadRequestGroup);
		preChecks(actor, owner);
		checkListPermission(actor, owner, UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, null);
		return uploadRequestBusinessService.findAll(uploadRequestGroup, statusList);
	}

	@Override
	public UploadRequest find(Account actor, Account owner, String uuid)
			throws BusinessException {
		UploadRequest ret = uploadRequestBusinessService.findByUuid(uuid);
		if (ret == null) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_NOT_FOUND, "Can not find upload request with uuid : " + uuid);
		}
		if (owner == null) {
			owner = ret.getUploadRequestGroup().getOwner();
		}
		checkReadPermission(actor, owner, UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, ret);
		return ret;
	}

	@Override
	public UploadRequestContainer create(Account authUser, Account owner, UploadRequest uploadRequest, UploadRequestContainer container) {
		uploadRequest = uploadRequestBusinessService.create(uploadRequest);
		container.addUploadRequests(uploadRequest);
		AuditLogEntryUser log = new UploadRequestAuditLogEntry(new AccountMto(owner),
				new AccountMto(owner), LogAction.CREATE, AuditLogEntryType.UPLOAD_REQUEST,
				uploadRequest.getUuid(), uploadRequest);
		container.addLog(log);
		return container;
	}

	@Override
	public UploadRequest updateStatus(Account authUser, Account actor, String uuid, UploadRequestStatus status,
			boolean copy) throws BusinessException {
		Validate.notNull(authUser, "authUser must be set.");
		Validate.notNull(actor, "Actor must be set.");
		UploadRequest req = find(authUser, actor, uuid);
		Validate.notNull(req);
		checkUpdatePermission(authUser, actor, UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, req);
		if (status.equals(req.getStatus())) {
			logger.debug("The new status {} is the same with current one {}, no operation was performed", status,
					req.getStatus());
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_STATUS_NOT_MODIFIED,
					"The new status is the same, no operation was performed");
		} else {
			UploadRequestAuditLogEntry log = new UploadRequestAuditLogEntry(new AccountMto(authUser),
					new AccountMto(actor), LogAction.UPDATE, AuditLogEntryType.UPLOAD_REQUEST, req.getUuid(), req);
			req = uploadRequestBusinessService.updateStatus(req, status);
			log.setResourceUpdated(new UploadRequestMto(req, true));
			logEntryService.insert(log);
			sendNotification(req, actor);
			archiveEntries(req, authUser, actor, status.compareTo(UploadRequestStatus.ARCHIVED) <= 0,
					(status.compareTo(UploadRequestStatus.CLOSED) <= 0) && copy);
			checkAndUpdateCollectiveUploadRequest(req.getUploadRequestGroup(), status);
		}
		return req;
	}

	private void sendNotification(UploadRequest req, Account actor) {
		List<MailContainerWithRecipient> mails = Lists.newArrayList();
		if (UploadRequestStatus.ENABLED.equals(req.getStatus())) {
			List<Contact> recipients = req.getUploadRequestURLs().stream().map(url -> url.getContact()).collect(Collectors.toList()); 

			for (UploadRequestUrl urUrl : req.getUploadRequestURLs()) {
				mails.add(mailBuildingService.build(new UploadRequestActivationEmailContext((User) actor, req, urUrl, recipients)));
			}
		} else if (req.getEnableNotification()) {
			if (UploadRequestStatus.CLOSED.equals(req.getStatus())) {
				for (UploadRequestUrl urUrl : req.getUploadRequestURLs()) {
					mails.add(mailBuildingService
							.build(new UploadRequestCloseByOwnerEmailContext((User) actor, urUrl, req)));
				}
			}
		}
		notifierService.sendNotification(mails);
		mails.clear();
	}

	private void archiveEntries(UploadRequest req, Account authUser, Account actor, Boolean delUploadRequestEntries,
			boolean copy) {
		if (delUploadRequestEntries || copy) {
			for (UploadRequestEntry requestEntry : findAllEntries(authUser, actor, req)) {
				if (!requestEntry.getCopied() && copy) {
					uploadRequestEntryService.copy(authUser, actor,
							new CopyResource(TargetKind.UPLOAD_REQUEST, requestEntry));
				}
				if (delUploadRequestEntries) {
					uploadRequestEntryService.delete(authUser, actor, requestEntry.getUuid());
				}
			}
		}
	}

	/**
	 * @param fromGroup check if the method is used from upload request group service
	 */
	@Override
	public UploadRequest update(Account actor, Account owner, String uuid, UploadRequest object, boolean fromGroup)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(owner, "Owner must be set.");
		Validate.notEmpty(uuid, "Uuid must be set.");
		Validate.notNull(object, "Object must be set.");
		UploadRequest uploadRequest = find(actor, owner, uuid);
		checkUpdatePermission(actor, owner, UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
				uploadRequest);
		UploadRequest oldRequest = uploadRequest.clone();
		UploadRequestAuditLogEntry log = new UploadRequestAuditLogEntry(new AccountMto(actor), new AccountMto(owner),
				LogAction.UPDATE, AuditLogEntryType.UPLOAD_REQUEST, uploadRequest.getUuid(), uploadRequest);
		// if called from upload request service ad the group is collective raise an error
		if (!fromGroup && uploadRequest.getUploadRequestGroup().isCollective()) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_NOT_UPDATABLE_GROUP_MODE,
					"Connot update a collective upload request, try to update the upload request group");
		}
		// when UR updated from uploadRequestService
		if (!fromGroup) {
			uploadRequest.setPristine(false);
		} else {
		// URs that are updated from the group are changed to pristine
			uploadRequest.setPristine(true);
		}
		UploadRequest res = uploadRequestBusinessService.update(uploadRequest, object);
		List<MailContainerWithRecipient> mails = Lists.newArrayList();
		if (res.getEnableNotification() && !res.businessEquals(oldRequest)) {
			for (UploadRequestUrl urUrl : res.getUploadRequestURLs()) {
				EmailContext context = new UploadRequestUpdateSettingsEmailContext((User) res.getUploadRequestGroup().getOwner(), urUrl, res, oldRequest);
				mails.add(mailBuildingService.build(context));
			}
			notifierService.sendNotification(mails);
		}
		log.setResourceUpdated(new UploadRequestMto(res));
		logEntryService.insert(log);
		return res;
	}

	@Override
	public UploadRequest updateRequest(Account actor, Account owner, UploadRequest req) throws BusinessException {
		checkUpdatePermission(actor, owner, UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, req);
		return uploadRequestBusinessService.update(req);
	}

	@Override
	public UploadRequest closeRequestByRecipient(UploadRequestUrl url)
			throws BusinessException {
		if (!url.getUploadRequest().isCanClose()) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_CLOSURE_FORBIDDEN,
					"Upload request can not be closed, please check your closure right.");
		}
		Account actor = accountRepository.getUploadRequestSystemAccount();
		UploadRequest req = url.getUploadRequest();
		if (req.getStatus().equals(UploadRequestStatus.CLOSED)) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_STATUS_NOT_MODIFIED,
					"Could not close an already closed upload request: "
							+ req.getUuid());
		}
		UploadRequestAuditLogEntry log = new UploadRequestAuditLogEntry(new AccountMto(actor),
				new AccountMto(req.getUploadRequestGroup().getOwner()), LogAction.UPDATE, AuditLogEntryType.UPLOAD_REQUEST, req.getUuid(), req);
		checkUpdatePermission(actor, req.getUploadRequestGroup().getOwner(), UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, req);
		req.updateStatus(UploadRequestStatus.CLOSED);
		UploadRequest update = updateRequest(actor, actor, req);
		checkAndUpdateCollectiveUploadRequest(req.getUploadRequestGroup(), update.getStatus());
		EmailContext ctx = new UploadRequestClosedByRecipientEmailContext((User)req.getUploadRequestGroup().getOwner(), req, url);
		MailContainerWithRecipient mail = mailBuildingService.build(ctx);
		notifierService.sendNotification(mail);
		log.setResourceUpdated(new UploadRequestMto(update, true));
		logEntryService.insert(log);
		return update;
	}

	/**
	 * In case of URG in collective mode This URG should be closed
	 * @param requestGroup The parent URG
	 * @param status The input status
	 */
	private void checkAndUpdateCollectiveUploadRequest(UploadRequestGroup requestGroup,
			UploadRequestStatus status) {
		if (requestGroup.isCollective() && !status.equals(requestGroup.getStatus())) {
			logger.debug("The URG {} is in collective mode so it should be changed to the same status of the UR that has been updated", requestGroup);
			uploadRequestGroupBusinessService.updateStatus(requestGroup, status);
		}
	}

	@Override
	public UploadRequest deleteRequest(Account actor, Account owner, String uuid) throws BusinessException {
		return updateStatus(actor, owner, uuid, UploadRequestStatus.DELETED, false);
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
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -1);
			afterDate = c.getTime();
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
	public List<String> findOutdatedRequests(Account actor) {
		checkActorPermission(actor);
		return uploadRequestBusinessService.findOutdatedRequests();
	}

	@Override
	public List<String> findUnabledRequests(Account actor) {
		checkActorPermission(actor);
		return uploadRequestBusinessService.findUnabledRequests();
	}

	@Override
	public List<String> findAllRequestsToBeNotified(Account actor) {
		checkActorPermission(actor);
		return uploadRequestBusinessService.findAllRequestsToBeNotified();
	}

	private void checkActorPermission(Account actor) {
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You have no rights to access this service.");
		}
	}

	@Override
	public List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl requestUrl) throws BusinessException {
		UploadRequest uploadRequest = requestUrl.getUploadRequest();
		List<UploadRequestEntry> uploadRequestEntries = Lists.newArrayList();
		for (UploadRequestUrl uploadRequestUrl : uploadRequest.getUploadRequestURLs()) {
			uploadRequestEntries.addAll(uploadRequestEntryService.findAllExtEntries(uploadRequestUrl));
		}
		return uploadRequestEntries;
	}

	@Override
	public List<UploadRequestEntry> findAllEntries(Account authUser, Account actor, UploadRequest uploadRequest)
			throws BusinessException {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
				null);
		return uploadRequestEntryService.findAllEntries(authUser, actor, uploadRequest);
	}

	@Override
	public Integer countNbrUploadedFiles(UploadRequest uploadRequest) {
		return uploadRequestBusinessService.countNbrUploadedFiles(uploadRequest);
	}

	@Override
	public Long computeEntriesSize(UploadRequest request) {
		return uploadRequestBusinessService.computeEntriesSize(request);
	}

	@Override
	public List<UploadRequest> findUploadRequestsToUpdate(Account authUser, Account actor,
			UploadRequestGroup uploadRequestGroup, List<UploadRequestStatus> listAllowedStatusToUpdate) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, UploadRequest.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, null);
		return uploadRequestBusinessService.findUploadRequestsToUpdate(uploadRequestGroup, listAllowedStatusToUpdate);
	}
}
