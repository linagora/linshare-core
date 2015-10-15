/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.core.facade.webservice.delegation.impl;

import org.apache.commons.lang.Validate;
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
	public AsyncTaskDto find(String ownerUuid, String uuid) {
		User actor = checkAuthentication();
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		User owner = getOwner(ownerUuid);
		Validate.notEmpty(uuid, "Missing uuid");
		AsyncTask task = service.find(actor, owner, uuid);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto create(String ownerUuid, Long size,
			Long transfertDuration, String fileName, Integer frequency,
			AsyncTaskType taskType) {
		User actor = checkAuthentication();
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		User owner = getOwner(ownerUuid);
		AsyncTask task =  new AsyncTask(size, transfertDuration, fileName, frequency, taskType);
		return new AsyncTaskDto(service.create(actor, owner, task));
	}

	@Override
	public AsyncTaskDto create(String ownerUuid, String fileName,
			Integer frequency, AsyncTaskType taskType) {
		User actor = checkAuthentication();
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		User owner = getOwner(ownerUuid);
		AsyncTask task =  new AsyncTask(fileName, frequency, taskType);
		return new AsyncTaskDto(service.create(actor, owner, task));
	}

	@Override
	public AsyncTaskDto create(String ownerUuid, String fileName,
			AsyncTaskType taskType) {
		User actor = checkAuthentication();
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		User owner = getOwner(ownerUuid);
		AsyncTask task =  new AsyncTask(fileName, null, taskType);
		return new AsyncTaskDto(service.create(actor, owner, task));
	}

	@Override
	public AsyncTaskDto fail(String ownerUuid, AsyncTaskDto asyncTask, Exception e) {
		User actor = checkAuthentication();
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(asyncTask, "Missing AsyncTask");
		Validate.notEmpty(asyncTask.getUuid(), "Missing AsyncTask uuid");
		User owner = getOwner(ownerUuid);
		AsyncTask task = null;
		if (e instanceof BusinessException) {
			BusinessException eb = (BusinessException) e;
			task = service.fail(actor, owner, asyncTask.getUuid(),
					eb.getErrorCode().getCode(), eb.getErrorCode().name(), eb.getMessage());
		} else {
			String message = e.getMessage();
			if (message == null) {
				message = e.toString();
			}
			task = service.fail(actor, owner, asyncTask.getUuid(), message);
		}
		return new AsyncTaskDto(task);
	}
}
