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
package org.linagora.linshare.core.facade.webservice.delegation.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.domain.entities.AsyncTask;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.delegation.AsyncTaskFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AsyncTaskService;
import org.linagora.linshare.core.service.UserService;

public class AsyncTaskFacadeImpl extends DelegationGenericFacadeImpl implements AsyncTaskFacade {

	protected final AsyncTaskService service;

	public AsyncTaskFacadeImpl(AccountService accountService,
			UserService userService, AsyncTaskService service) {
		super(accountService, userService);
		this.service = service;
	}

	@Override
	public AsyncTaskDto find(String actorUuid, String uuid) {
		User authUser = checkAuthentication();
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		User actor = getActor(actorUuid);
		Validate.notEmpty(uuid, "Missing uuid");
		AsyncTask task = service.find(authUser, actor, uuid, false);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto create(String actorUuid, Long size,
			Long transfertDuration, String fileName, Integer frequency,
			AsyncTaskType taskType) {
		User authUser = checkAuthentication();
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		User actor = getActor(actorUuid);
		AsyncTask task =  new AsyncTask(size, transfertDuration, fileName, frequency, taskType);
		return new AsyncTaskDto(service.create(authUser, actor, task));
	}

	@Override
	public AsyncTaskDto create(String actorUuid, String fileName,
			Integer frequency, AsyncTaskType taskType) {
		User authUser = checkAuthentication();
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		User actor = getActor(actorUuid);
		AsyncTask task =  new AsyncTask(fileName, frequency, taskType);
		return new AsyncTaskDto(service.create(authUser, actor, task));
	}

	@Override
	public AsyncTaskDto create(String actorUuid, String fileName,
			AsyncTaskType taskType) {
		User authUser = checkAuthentication();
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		User actor = getActor(actorUuid);
		AsyncTask task =  new AsyncTask(fileName, null, taskType);
		return new AsyncTaskDto(service.create(authUser, actor, task));
	}

	@Override
	public AsyncTaskDto fail(String actorUuid, AsyncTaskDto asyncTask, Exception e) {
		User authUser = checkAuthentication();
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notNull(asyncTask, "Missing AsyncTask");
		Validate.notEmpty(asyncTask.getUuid(), "Missing AsyncTask uuid");
		User actor = getActor(actorUuid);
		AsyncTask task = null;
		if (e instanceof BusinessException) {
			BusinessException eb = (BusinessException) e;
			task = service.fail(authUser, actor, asyncTask.getUuid(),
					eb.getErrorCode().getCode(), eb.getErrorCode().name(), eb.getMessage());
		} else {
			String message = e.getMessage();
			if (message == null) {
				message = e.toString();
			}
			task = service.fail(authUser, actor, asyncTask.getUuid(), message);
		}
		return new AsyncTaskDto(task);
	}
}
