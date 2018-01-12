/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
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

package org.linagora.linshare.core.upgrade.v2_1;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ThreadEntryRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class RemoveAllThreadEntriesUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected ThreadEntryRepository threadEntryRepository;

	public RemoveAllThreadEntriesUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			ThreadEntryRepository threadEntryRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.threadEntryRepository = threadEntryRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_1_REMOVE_ALL_THREAD_ENTRIES;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job is starting ...");
		List<String> entries = threadEntryRepository.findAllThreadEntries();
		logger.info(entries.size() + " ThreadEntry(s) have been found.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		ThreadEntry threadEntry = threadEntryRepository.findByUuid(identifier);
		BatchResultContext<ThreadEntry> res = new BatchResultContext<ThreadEntry>(threadEntry);
		res.setProcessed(false);
		if (threadEntry == null) {
			res.setIdentifier(identifier);
			return res;
		}
		threadEntryRepository.delete(threadEntry);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ThreadEntry> res = (BatchResultContext<ThreadEntry>) context;
		ThreadEntry resource = res.getResource();
		if (resource != null) {
			logInfo(batchRunContext, total, position, "ThreadEntry was deleted : " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "ThreadEntry upgrade skipped: " + res.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ThreadEntry> res = (BatchResultContext<ThreadEntry>) exception.getContext();
		ThreadEntry resource = res.getResource();
		console.logError(batchRunContext, total, position, "error when trying to delete threadEntry", batchRunContext);
		logger.error("Error occured while processing domain : "
				+ resource +
				". BatchBusinessException", exception);
	}

}
