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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AsyncTaskBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.AsyncTaskStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AsyncTask;
import org.linagora.linshare.core.domain.entities.UpgradeTask;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AsyncTaskResourceAccessControl;
import org.linagora.linshare.core.service.AsyncTaskService;

public class AsyncTaskServiceImpl extends
		GenericServiceImpl<Account, AsyncTask> implements AsyncTaskService {

	protected AsyncTaskBusinessService businessService;

	public AsyncTaskServiceImpl(AsyncTaskBusinessService businessService,
			AsyncTaskResourceAccessControl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.businessService = businessService;
	}

	@Override
	public AsyncTask find(Account actor, Account owner, String uuid, boolean retry) {
		preChecks(actor, owner);
		AsyncTask task = businessService.find(uuid);
		if (task == null && retry) {
			try {
				logger.warn("Async task not found : " + uuid);
				Thread.sleep(500);
				task = businessService.find(uuid);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (task == null) {
			logger.error("Async task not found : " + uuid);
			throw new BusinessException(
					BusinessErrorCode.ASYNC_TASK_NOT_FOUND,
					"Async task not found : " + uuid);
		}
		checkReadPermission(actor, owner, AsyncTask.class, BusinessErrorCode.ASYNC_TASK_FORBIDDEN, task);
		return task;
	}

	@Override
	public void delete(Account actor, Account owner, String uuid)
			throws BusinessException {
		preChecks(actor, owner);
		AsyncTask task = find(actor, owner, uuid, false);
		checkDeletePermission(actor, owner, AsyncTask.class, BusinessErrorCode.ASYNC_TASK_FORBIDDEN, task);
		businessService.delete(task);
	}

	@Override
	public AsyncTask processing(Account actor, Account owner,
			String asyncTaskUuid) {
		AsyncTask task = find(actor, owner, asyncTaskUuid, true);
		checkUpdatePermission(actor, owner, AsyncTask.class, BusinessErrorCode.ASYNC_TASK_FORBIDDEN, task);
		task.setStatus(AsyncTaskStatus.PROCESSING);
		task.setStartProcessingDate(new Date());
		task.computeWaitingDuration();
		return businessService.update(task);
	}

	@Override
	public AsyncTask success(Account actor, Account owner,
			String asyncTaskUuid, String resourceUuid) {
		AsyncTask task = find(actor, owner, asyncTaskUuid, false);
		checkUpdatePermission(actor, owner, AsyncTask.class, BusinessErrorCode.ASYNC_TASK_FORBIDDEN, task);
		task.setStatus(AsyncTaskStatus.SUCCESS);
		task.setResourceUuid(resourceUuid);
		task.setEndProcessingDate(new Date());
		task.computeProcessingDuration();
		return businessService.update(task);
	}

	@Override
	public AsyncTask fail(Account actor, Account owner, String asyncTaskUuid, String errorMsg) {
		AsyncTask task = find(actor, owner, asyncTaskUuid, false);
		checkUpdatePermission(actor, owner, AsyncTask.class, BusinessErrorCode.ASYNC_TASK_FORBIDDEN, task);
		task.setStatus(AsyncTaskStatus.FAILED);
		task.setErrorCode(-1);
		task.setErrorMsg(errorMsg);
		task.setEndProcessingDate(new Date());
		task.computeProcessingDuration();
		return businessService.update(task);
	}

	@Override
	public AsyncTask fail(Account actor, Account owner, String asyncTaskUuid,
			Integer errorCode, String errorName, String errorMsg) {
		AsyncTask task = find(actor, owner, asyncTaskUuid, false);
		checkUpdatePermission(actor, owner, AsyncTask.class, BusinessErrorCode.ASYNC_TASK_FORBIDDEN, task);
		task.setStatus(AsyncTaskStatus.FAILED);
		task.setErrorCode(errorCode);
		task.setErrorMsg(errorMsg);
		task.setErrorName(errorName);
		task.setEndProcessingDate(new Date());
		task.computeProcessingDuration();
		return businessService.update(task);
	}

	@Override
	public AsyncTask create(Account actor, Account owner, AsyncTask task) throws BusinessException {
		preChecks(actor, owner);
		checkCreatePermission(actor, owner, AsyncTask.class, BusinessErrorCode.ASYNC_TASK_FORBIDDEN, null);
		Validate.notNull(task, "Task must not be null");
		Validate.notNull(task.getTaskType(), "Task type must be set");
		task.setActor(actor);
		task.setOwner(owner);
		task.setDomain(owner.getDomain());
		return businessService.create(task);
	}

	@Override
	public List<AsyncTask> findAll(Account actor, Account owner, UpgradeTask upgradeTask) {
		preChecks(actor, owner);
		checkListPermission(actor, owner, AsyncTask.class, BusinessErrorCode.ASYNC_TASK_FORBIDDEN, null);
		Validate.notNull(upgradeTask, "Upgrade task must not be null");
		return businessService.findAll(owner, upgradeTask);
	}

}
