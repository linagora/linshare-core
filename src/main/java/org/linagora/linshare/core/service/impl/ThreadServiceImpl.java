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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.ThreadResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

public class ThreadServiceImpl extends GenericServiceImpl<Account, WorkGroup> implements ThreadService {

	private final ThreadRepository threadRepository;

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	private final ContainerQuotaBusinessService containerQuotaBusinessService;

	private final WorkGroupNodeService workGroupNodeService;

	public ThreadServiceImpl(
			ThreadRepository threadRepository,
			ThreadMemberRepository threadMemberRepository,
			LogEntryService logEntryService,
			ThreadResourceAccessControl rac,
			AccountQuotaBusinessService accountQuotaBusinessService,
			ContainerQuotaBusinessService containerQuotaBusinessService,
			WorkGroupNodeService workGroupNodeService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.threadRepository = threadRepository;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
		this.workGroupNodeService = workGroupNodeService;
	}

	@Override
	public WorkGroup find(Account actor, Account owner, String uuid) {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Missing shared space uuid");
		WorkGroup workGroup = threadRepository.findByLsUuid(uuid);

		if (workGroup == null) {
			logger.error("Can't find shared space  : " + uuid);
			logger.error("Current actor " + actor.getAccountRepresentation()
					+ " is looking for a misssing shared space (" + uuid
					+ ") owned by : " + owner.getAccountRepresentation());
			String message = "Can not find shared space with uuid : " + uuid;
			throw new BusinessException(
					BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, message);
		}
		return workGroup;
	}

	@Override
	public WorkGroup findByLsUuidUnprotected(String uuid) {
		WorkGroup workGroup = threadRepository.findByLsUuid(uuid);
		if (workGroup == null) {
			logger.error("Can't find shared space  : " + uuid);
		}
		return workGroup;
	}

	@Override
	public WorkGroup create(Account actor, Account owner, String name) throws BusinessException {
		logger.debug("User " + owner.getAccountRepresentation() + " trying to create new shared space named " + name);
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
		threadRepository.purge(workGroup);
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

}
