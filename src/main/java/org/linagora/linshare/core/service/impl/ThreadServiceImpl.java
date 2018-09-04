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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.ThreadResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.ThreadAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.ThreadMto;

public class ThreadServiceImpl extends GenericServiceImpl<Account, WorkGroup> implements ThreadService {

	private final ThreadRepository threadRepository;

	private final ThreadMemberRepository threadMemberRepository;

	private final LogEntryService logEntryService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	private final ContainerQuotaBusinessService containerQuotaBusinessService;

	private final WorkGroupNodeService workGroupNodeService;

	public ThreadServiceImpl(
			ThreadRepository threadRepository,
			ThreadMemberRepository threadMemberRepository,
			LogEntryService logEntryService,
			ThreadResourceAccessControl rac,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			AccountQuotaBusinessService accountQuotaBusinessService,
			ContainerQuotaBusinessService containerQuotaBusinessService,
			WorkGroupNodeService workGroupNodeService) {
		super(rac);
		this.threadRepository = threadRepository;
		this.threadMemberRepository = threadMemberRepository;
		this.logEntryService = logEntryService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
		this.workGroupNodeService = workGroupNodeService;
	}

	@Override
	public WorkGroup find(Account actor, Account owner, String uuid) {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Missing thread uuid");
		WorkGroup workGroup = threadRepository.findByLsUuid(uuid);

		if (workGroup == null) {
			logger.error("Can't find thread  : " + uuid);
			logger.error("Current actor " + actor.getAccountRepresentation()
					+ " is looking for a misssing thread (" + uuid
					+ ") owned by : " + owner.getAccountRepresentation());
			String message = "Can not find thread with uuid : " + uuid;
			throw new BusinessException(
					BusinessErrorCode.THREAD_NOT_FOUND, message);
		}
		return workGroup;
	}

	@Override
	public WorkGroup findByLsUuidUnprotected(String uuid) {
		WorkGroup workGroup = threadRepository.findByLsUuid(uuid);
		if (workGroup == null) {
			logger.error("Can't find thread  : " + uuid);
		}
		return workGroup;
	}

	@Override
	public WorkGroup create(Account actor, Account owner, String name) throws BusinessException {
		logger.debug("User " + owner.getAccountRepresentation() + " trying to create new thread named " + name);
		WorkGroup workGroup = new WorkGroup(owner.getDomain(), owner, name);
		threadRepository.create(workGroup);
		createQuotaThread(workGroup);
		workGroup = threadRepository.update(workGroup);
		return workGroup;
	}

	@Override
	public void deleteThread(Account actor, Account owner, WorkGroup workGroup)
			throws BusinessException {
		WorkGroupNode rootFolder = workGroupNodeService.getRootFolder(actor, owner, workGroup);
		workGroupNodeService.delete(actor, owner, workGroup, rootFolder.getUuid());
		workGroup.setEntries(null);
		threadRepository.update(workGroup);
		threadRepository.delete(workGroup);
	}

	@Override
	public WorkGroup update(Account actor, Account owner, String threadUuid,
			String threadName) throws BusinessException {
		WorkGroup workGroup = find(actor, owner, threadUuid);
		ThreadAuditLogEntry log = new ThreadAuditLogEntry(actor, owner, LogAction.UPDATE, AuditLogEntryType.WORKGROUP,
				new ThreadMto(workGroup, true));
		workGroup.setName(threadName);
		addMembersToLog(workGroup, log);
		WorkGroup update = threadRepository.update(workGroup);
		User owner2 = (User) owner;
		WorkGroupNode rootFolder = workGroupNodeService.getRootFolder(actor, owner2, workGroup);
		rootFolder.setName(threadName);
		workGroupNodeService.update(actor, owner2, workGroup, rootFolder);
		log.setResourceUpdated(new ThreadMto(update, true));
		logEntryService.insert(log);
		return update;
	}

	private void createQuotaThread(WorkGroup workGroup) throws BusinessException {
		Validate.notNull(workGroup, "Thread must be set.");
		ContainerQuota containerQuota = containerQuotaBusinessService.find(workGroup.getDomain(), ContainerQuotaType.WORK_GROUP);
		if (containerQuota == null) {
			throw new BusinessException("Missing container quota entity for current work_group");
		}
		AccountQuota threadQuota = new AccountQuota(
				workGroup.getDomain(),
				workGroup.getDomain().getParentDomain(),
				workGroup, containerQuota);
		threadQuota.setDomainShared(containerQuota.getDomainQuota().getDomainShared());
		threadQuota.setDomainSharedOverride(containerQuota.getDomainQuota().getDomainSharedOverride());
		accountQuotaBusinessService.create(threadQuota);
	}

    /* ***********************************************************
     *                   Helpers
     ************************************************************ */

	protected void addMembersToLog(WorkGroup workGroup, AuditLogEntryUser log) {
		List<String> members = threadMemberRepository.findAllAccountUuidForThreadMembers(workGroup);
		log.addRelatedAccounts(members);
	}

}
