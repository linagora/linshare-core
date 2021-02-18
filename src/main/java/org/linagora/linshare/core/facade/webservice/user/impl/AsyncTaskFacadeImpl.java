/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
		AsyncTask task = service.find(authUser, authUser, uuid);
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
