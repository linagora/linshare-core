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
package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UploadRequestGroupService;

public class CloseExpiredUploadRequestGroupBatchImpl extends GenericBatchImpl {

	protected final UploadRequestGroupService uploadRequestGroupService;

	public CloseExpiredUploadRequestGroupBatchImpl(AccountRepository<Account> accountRepository,
			final UploadRequestGroupService uploadRequestGroupService) {
		super(accountRepository);
		this.uploadRequestGroupService = uploadRequestGroupService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		SystemAccount account = getSystemAccount();
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = uploadRequestGroupService.findOutdatedRequestsGroup(account);
		logger.info(entries.size()
				+ " Upload Request(s) group have been found");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount account = getSystemAccount();
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.find(account, account, identifier);
		ResultContext context = new BatchResultContext<UploadRequestGroup>(uploadRequestGroup);
		for(UploadRequest ur : uploadRequestGroup.getUploadRequests()) {
			// with this batch, we update just current uploadRequestGroup
			// if there is one uploadRequest enable, we shouldn't  close the group
			if (ur.getStatus().equals(UploadRequestStatus.ENABLED)) {
				context.setProcessed(false);
				return context;
			}
		}
		uploadRequestGroupService.updateStatus(account, account, identifier, UploadRequestStatus.CLOSED, false);
		context.setProcessed(true);
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadRequestGroup> uploadRequestContext = (BatchResultContext<UploadRequestGroup>) context;
		UploadRequestGroup uploadRequestGroup = uploadRequestContext.getResource();
		console.logInfo(batchRunContext, total, position, "processing upload request group : ", uploadRequestGroup.getUuid());
		console.logInfo(batchRunContext, total, position,
				"The Upload Request group " + uploadRequestGroup.getUuid() + " has been successfully closed.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadRequestGroup> context = (BatchResultContext<UploadRequestGroup>) exception
				.getContext();
		UploadRequestGroup uploadrequestgroup = context.getResource();
		console.logError(batchRunContext, total, position, "Closing upload request group has failed  : {}",
				uploadrequestgroup.getUuid(), exception);
	}
}
