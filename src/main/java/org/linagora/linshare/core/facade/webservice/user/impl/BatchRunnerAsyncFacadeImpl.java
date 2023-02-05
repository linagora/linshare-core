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
package org.linagora.linshare.core.facade.webservice.user.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.UpgradeTaskStatus;
import org.linagora.linshare.core.domain.entities.AsyncTask;
import org.linagora.linshare.core.domain.entities.UpgradeTask;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.user.BatchRunnerAsyncFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AsyncTaskService;
import org.linagora.linshare.core.service.UpgradeTaskService;
import org.linagora.linshare.webservice.userv1.task.context.TaskContext;

public class BatchRunnerAsyncFacadeImpl extends GenericAsyncFacadeImpl implements BatchRunnerAsyncFacade {

	protected UpgradeTaskService service;

	public BatchRunnerAsyncFacadeImpl(
			AccountService accountService,
			UpgradeTaskService service,
			AsyncTaskService asyncStatusService) {
		super(accountService, asyncStatusService);
		this.service = service;
	}

	@Override
	public AsyncTaskDto processing(TaskContext taskContext, String asyncTaskUuid) {
		User authUser = checkAuthentication(taskContext);
		User actor = getActor(taskContext);
		AsyncTask task = asyncTaskService.processing(authUser, actor, asyncTaskUuid);
		UpgradeTask upgradeTask = task.getUpgradeTask();
		upgradeTask.setStatus(UpgradeTaskStatus.PROCESSING);
		service.update(authUser, upgradeTask);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto success(TaskContext taskContext, String asyncTaskUuid, String resourceUuid) {
		Validate.notEmpty(asyncTaskUuid, "Missing async task uuid");
		User authUser = checkAuthentication(taskContext);
		User actor = getActor(taskContext);
		AsyncTask task = asyncTaskService.success(authUser, actor, asyncTaskUuid,
				resourceUuid);
		UpgradeTask upgradeTask = task.getUpgradeTask();
		upgradeTask.setStatus(UpgradeTaskStatus.SUCCESS);
		service.update(authUser, upgradeTask);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto fail(TaskContext taskContext, String asyncTaskUuid, String errorMsg) {
		Validate.notEmpty(asyncTaskUuid, "Missing async task uuid");
		User authUser = checkAuthentication(taskContext);
		User actor = getActor(taskContext);
		AsyncTask task = asyncTaskService.fail(authUser, actor, asyncTaskUuid,
				errorMsg);
		UpgradeTask upgradeTask = task.getUpgradeTask();
		upgradeTask.setStatus(UpgradeTaskStatus.FAILED);
		service.update(authUser, upgradeTask);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto fail(TaskContext taskContext, String asyncTaskUuid, Integer errorCode, String errorName,
			String errorMsg) {
		Validate.notEmpty(asyncTaskUuid, "Missing async task uuid");
		User authUser = checkAuthentication(taskContext);
		User actor = getActor(taskContext);
		AsyncTask task = asyncTaskService.fail(authUser, actor, asyncTaskUuid,
				errorCode, errorName, errorMsg);
		UpgradeTask upgradeTask = task.getUpgradeTask();
		upgradeTask.setStatus(UpgradeTaskStatus.FAILED);
		service.update(authUser, upgradeTask);
		return new AsyncTaskDto(task);
	}

}
