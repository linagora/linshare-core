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
package org.linagora.linshare.webservice.userv1.task;

import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.user.GenericAsyncFacade;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.webservice.userv1.task.context.BatchTaskContext;

public class BatchRunnerAsyncTask extends AsyncTask<BatchTaskContext> {

	protected BatchRunner batchRunner;

	protected BatchRunContext batchRunContext;

	protected String upgradeTaskUuid;

	public BatchRunnerAsyncTask(
			GenericAsyncFacade asyncFacade,
			BatchTaskContext taskContext,
			AsyncTaskDto asyncTaskDto,
			String actorUuid,
			String upgradeTaskUuid,
			BatchRunner batchRunner) {
		super(asyncFacade, taskContext, asyncTaskDto);
		this.batchRunner = batchRunner;
		this.batchRunContext = new BatchRunContext(actorUuid, asyncTaskDto.getUuid(), upgradeTaskUuid);
	}

	@Override
	protected String runMyTask(BatchTaskContext task) {
		boolean execute = batchRunner.execute(task.getBatch(), batchRunContext);
		if (!execute) {
			logger.error("asyncTask for batches failed : " + task.getBatch().getBatchClassName());
			throw new BusinessException(BusinessErrorCode.BATCH_INCOMPLETE, "asyncTask for batches failed");
		}
		return batchRunContext.getUuid();
	}
}
