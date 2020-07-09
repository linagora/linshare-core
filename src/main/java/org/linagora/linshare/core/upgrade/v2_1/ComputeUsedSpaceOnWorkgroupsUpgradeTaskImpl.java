/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

package org.linagora.linshare.core.upgrade.v2_1;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;

public class ComputeUsedSpaceOnWorkgroupsUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected WorkGroupNodeMongoRepository workGroupNodeMongoRepository;

	protected ThreadRepository threadRepository;

	protected AccountQuotaRepository accountQuotaRepository;

	protected OperationHistoryRepository operationHistoryRepository;

	public ComputeUsedSpaceOnWorkgroupsUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			WorkGroupNodeMongoRepository workGroupNodeMongoRepository,
			ThreadRepository threadRepository,
			AccountQuotaRepository accountQuotaRepository,
			OperationHistoryRepository operationHistoryRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.workGroupNodeMongoRepository = workGroupNodeMongoRepository;
		this.threadRepository = threadRepository;
		this.accountQuotaRepository = accountQuotaRepository;
		this.operationHistoryRepository = operationHistoryRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job is starting ...");
		List<String> entries = threadRepository.findAllThreadUuid();
		logger.info(entries.size() + " workGroup(s) have been found.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		WorkGroup workGroup = threadRepository.findByLsUuid(identifier);
		BatchResultContext<WorkGroup> res = new BatchResultContext<WorkGroup>(workGroup);
		res.setProcessed(false);
		if (workGroup == null)  {
			res.setIdentifier(identifier);
			return res;
		}
		List<WorkGroupDocument> workGroupDocuments = workGroupNodeMongoRepository.findAllWorkGroupDocument(workGroup.getLsUuid(), WorkGroupNodeType.DOCUMENT);
		Long totalSize = 0L;
		for(WorkGroupDocument workGroupDocument : workGroupDocuments) {
			totalSize += workGroupDocument.getSize();
		}
		AccountQuota accountQuota = accountQuotaRepository.find(workGroup);
		accountQuota.setCurrentValue(totalSize);
		accountQuotaRepository.update(accountQuota);
		operationHistoryRepository.deleteBeforeDateByAccount(Calendar.getInstance().getTime(), workGroup);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroup> res = (BatchResultContext<WorkGroup>) context;
		WorkGroup resource = res.getResource();
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, resource + " has been updated.");
		} else {
			logInfo(batchRunContext, total, position, resource + " has been skipped.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroup> res = (BatchResultContext<WorkGroup>) exception.getContext();
		WorkGroup resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed.", batchRunContext);
		logger.error("Error occured while updating the workGroup AccountQuota : "
				+ resource +
				". BatchBusinessException", exception);
	}

}
