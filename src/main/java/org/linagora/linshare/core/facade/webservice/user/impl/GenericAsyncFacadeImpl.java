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

package org.linagora.linshare.core.facade.webservice.user.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AsyncTask;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.user.GenericAsyncFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AsyncTaskService;
import org.linagora.linshare.webservice.userv1.task.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericAsyncFacadeImpl implements
		GenericAsyncFacade {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final AccountService accountService;

	protected final AsyncTaskService asyncTaskService;

	public GenericAsyncFacadeImpl(AccountService accountService,
			AsyncTaskService asyncStatusService) {
		super();
		this.accountService = accountService;
		this.asyncTaskService = asyncStatusService;
	}

	protected User checkAuthentication(TaskContext taskContext) throws BusinessException {
		Validate.notNull(taskContext, "Missing asyncDto");
		AccountDto actorDto = taskContext.getActorDto();
		return checkAuthentication(actorDto);
	}

	protected User getOwner(TaskContext taskContext) throws BusinessException {
		Validate.notNull(taskContext, "Missing asyncDto");
		Validate.notEmpty(taskContext.getOwnerUuid(), "Missing owner uuid");
		Account owner = accountService.findByLsUuid(taskContext.getOwnerUuid());
		if (owner == null)
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		if (!(owner.hasSimpleRole() || owner.hasAdminRole() || owner.hasSuperAdminRole())) {
			logger.error("Current owner is trying to access to a forbbiden api : "
					+ owner.getAccountRepresentation());
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return (User) owner;
	}

	protected User checkAuthentication(AccountDto actorDto) throws BusinessException {
		Validate.notNull(actorDto, "Missing actorDto");
		Validate.notEmpty(actorDto.getUuid(), "Missing actorDto uuid");
		Account actor = accountService.findByLsUuid(actorDto.getUuid());
		if (actor == null)
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		if (!(actor.hasSimpleRole() || actor.hasAdminRole() || actor.hasSuperAdminRole()) || actor.hasDelegationRole()) {
			logger.error("Current actor is trying to access to a forbbiden api : "
					+ actor.getAccountRepresentation());
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return (User) actor;
	}

	@Override
	public AsyncTaskDto processing(TaskContext taskContext,
			String asyncTaskUuid) {
		User actor = checkAuthentication(taskContext);
		User owner = getOwner(taskContext);
		AsyncTask task = asyncTaskService.processing(actor, owner, asyncTaskUuid);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto success(TaskContext taskContext, String asyncTaskUuid,
			String resourceUuid) {
		Validate.notEmpty(asyncTaskUuid, "Missing async task uuid");
		User actor = checkAuthentication(taskContext);
		User owner = getOwner(taskContext);
		AsyncTask task = asyncTaskService.success(actor, owner, asyncTaskUuid,
				resourceUuid);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto fail(TaskContext taskContext, String asyncTaskUuid,
			String errorMsg) {
		Validate.notEmpty(asyncTaskUuid, "Missing async task uuid");
		User actor = checkAuthentication(taskContext);
		User owner = getOwner(taskContext);
		AsyncTask task = asyncTaskService.fail(actor, owner, asyncTaskUuid,
				errorMsg);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto fail(TaskContext taskContext, String asyncTaskUuid,
			Integer errorCode, String errorName, String errorMsg) {
		Validate.notEmpty(asyncTaskUuid, "Missing async task uuid");
		User actor = checkAuthentication(taskContext);
		User owner = getOwner(taskContext);
		AsyncTask task = asyncTaskService.fail(actor, owner, asyncTaskUuid,
				errorCode, errorName, errorMsg);
		return new AsyncTaskDto(task);
	}
}
