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

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.AsyncTask;
import org.linagora.linshare.core.domain.entities.UpgradeTask;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.UpgradeTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.user.AsyncTaskFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AsyncTaskService;
import org.linagora.linshare.core.service.UpgradeTaskService;

import com.google.common.collect.Lists;

public class AsyncTaskFacadeImpl extends UserGenericFacadeImp implements
		AsyncTaskFacade {

	protected final AsyncTaskService service;

	protected UpgradeTaskService upgradeTaskService;

	public AsyncTaskFacadeImpl(AccountService accountService,
			UpgradeTaskService upgradeTaskService,
			AsyncTaskService asyncTaskService) {
		super(accountService);
		this.service = asyncTaskService;
		this.upgradeTaskService = upgradeTaskService;
	}

	@Override
	public AsyncTaskDto create(Long size, Long transfertDuration,
			String fileName, Integer frequency, AsyncTaskType taskType) {
		User authUser = checkAuthentication();
		AsyncTask task =  new AsyncTask(size, transfertDuration, fileName, frequency, taskType);
		return new AsyncTaskDto(service.create(authUser, authUser, task));
	}

	@Override
	public AsyncTaskDto create(String fileName, Integer frequency,
			AsyncTaskType taskType) {
		User authUser = checkAuthentication();
		AsyncTask task =  new AsyncTask(fileName, frequency, taskType);
		return new AsyncTaskDto(service.create(authUser, authUser, task));
	}

	@Override
	public AsyncTaskDto create(String fileName, AsyncTaskType taskType) {
		User authUser = checkAuthentication();
		AsyncTask task =  new AsyncTask(fileName, null, taskType);
		return new AsyncTaskDto(service.create(authUser, authUser, task));
	}

	@Override
	public AsyncTaskDto create(UpgradeTaskDto upgradeTaskDto, AsyncTaskType taskType) {
		User authUser = checkAuthentication();
		UpgradeTask upgradeTask = upgradeTaskService.find(authUser, upgradeTaskDto.getIdentifier());
		AsyncTask task =  new AsyncTask(upgradeTask, taskType);
		return new AsyncTaskDto(service.create(authUser, authUser, task));
	}

	@Override
	public AsyncTaskDto find(String uuid) {
		User authUser = checkAuthentication();
		Validate.notEmpty(uuid, "Missing uuid");
		AsyncTask task = service.find(authUser, authUser, uuid, false);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto fail(AsyncTaskDto asyncTask, Exception e) {
		User authUser = checkAuthentication();
		Validate.notNull(asyncTask, "Missing AsyncTask");
		Validate.notEmpty(asyncTask.getUuid(), "Missing AsyncTask uuid");
		AsyncTask task = null;
		if (e instanceof BusinessException) {
			BusinessException eb = (BusinessException) e;
			task = service.fail(authUser, authUser, asyncTask.getUuid(),
					eb.getErrorCode().getCode(), eb.getErrorCode().name(), eb.getMessage());
		} else {
			String message = e.getMessage();
			if (message == null) {
				message = e.toString();
			}
			task = service.fail(authUser, authUser, asyncTask.getUuid(), message);
		}
		return new AsyncTaskDto(task);
	}

	@Override
	public List<AsyncTaskDto> findAll(UpgradeTaskType upgradeTaskIdentifier) {
		User authUser = checkAuthentication();
		Validate.notNull(upgradeTaskIdentifier, "Missing AsyncTask identifier");
		UpgradeTask upgradeTask = upgradeTaskService.find(authUser, upgradeTaskIdentifier);
		List<AsyncTask> findAll = service.findAll(authUser, authUser, upgradeTask);
		List<AsyncTaskDto> res = Lists.newArrayList();
		for (AsyncTask asyncTask : findAll) {
			res.add(new AsyncTaskDto(asyncTask));
		}
		return res;
	}
}
