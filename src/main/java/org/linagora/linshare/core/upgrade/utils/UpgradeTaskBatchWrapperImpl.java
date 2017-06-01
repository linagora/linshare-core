/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.upgrade.utils;

import java.util.List;

import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class UpgradeTaskBatchWrapperImpl extends GenericUpgradeTaskImpl {

	protected GenericBatch genericBatch;

	protected UpgradeTaskType wrappedTask;

	public UpgradeTaskBatchWrapperImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			UpgradeTaskType wrappedTask,
			GenericBatch genericBatch
			) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.genericBatch = genericBatch;
		// overriding current console batch with the upgrade task console.
		this.genericBatch.setConsole(this.getConsole());
		this.wrappedTask = wrappedTask;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return wrappedTask;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return genericBatch.getAll(batchRunContext);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		return genericBatch.execute(batchRunContext, identifier, total, position);
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		genericBatch.notify(batchRunContext, context, total, position);
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		genericBatch.notifyError(exception, identifier, total, position, batchRunContext);
	}

	@Override
	public boolean needToRun() {
		return genericBatch.needToRun();
	}

	@Override
	public void start(BatchRunContext batchRunContext) {
		console.logInfo(batchRunContext, genericBatch.getBatchClassName() + " is starting ...");
		genericBatch.start(batchRunContext);
	}

	@Override
	public void fail(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total,
			long processed) {
		genericBatch.fail(batchRunContext, all, errors, unhandled_errors, total, processed);
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors,
			long total, long processed) {
		genericBatch.terminate(batchRunContext, all, errors, unhandled_errors, total, processed);
		console.logInfo(batchRunContext, genericBatch.getBatchClassName() + " is completed");
	}

}
