/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

import org.apache.commons.lang3.Validate;
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
		AccountDto authUserDto = taskContext.getAuthUserDto();
		return checkAuthentication(authUserDto);
	}

	protected User getActor(TaskContext taskContext) throws BusinessException {
		Validate.notNull(taskContext, "Missing asyncDto");
		Validate.notEmpty(taskContext.getActorUuid(), "Missing actor uuid");
		Account actor = accountService.findByLsUuid(taskContext.getActorUuid());
		if (actor == null)
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		if (!(actor.hasSimpleRole() || actor.hasAdminRole() || actor.hasSuperAdminRole())) {
			logger.error("Current actor is trying to access to a forbbiden api : "
					+ actor.getAccountRepresentation());
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return (User) actor;
	}

	protected User checkAuthentication(AccountDto authUserDto) throws BusinessException {
		Validate.notNull(authUserDto, "Missing authUserDto");
		Validate.notEmpty(authUserDto.getUuid(), "Missing authUserDto uuid");
		Account authUser = accountService.findByLsUuid(authUserDto.getUuid());
		if (authUser == null)
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		if (!(authUser.hasSimpleRole() || authUser.hasAdminRole() || authUser.hasSuperAdminRole()) || authUser.hasDelegationRole()) {
			logger.error("Current authUser is trying to access to a forbbiden api : "
					+ authUser.getAccountRepresentation());
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return (User) authUser;
	}

	@Override
	public AsyncTaskDto processing(TaskContext taskContext,
			String asyncTaskUuid) {
		User authUser = checkAuthentication(taskContext);
		User actor = getActor(taskContext);
		AsyncTask task = asyncTaskService.processing(authUser, actor, asyncTaskUuid);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto success(TaskContext taskContext, String asyncTaskUuid,
			String resourceUuid) {
		Validate.notEmpty(asyncTaskUuid, "Missing async task uuid");
		User authUser = checkAuthentication(taskContext);
		User actor = getActor(taskContext);
		AsyncTask task = asyncTaskService.success(authUser, actor, asyncTaskUuid,
				resourceUuid);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto fail(TaskContext taskContext, String asyncTaskUuid,
			String errorMsg) {
		Validate.notEmpty(asyncTaskUuid, "Missing async task uuid");
		User authUser = checkAuthentication(taskContext);
		User actor = getActor(taskContext);
		AsyncTask task = asyncTaskService.fail(authUser,actor, asyncTaskUuid,
				errorMsg);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto fail(TaskContext taskContext, String asyncTaskUuid,
			Integer errorCode, String errorName, String errorMsg) {
		Validate.notEmpty(asyncTaskUuid, "Missing async task uuid");
		User authUser = checkAuthentication(taskContext);
		User actor = getActor(taskContext);
		AsyncTask task = asyncTaskService.fail(authUser, actor, asyncTaskUuid,
				errorCode, errorName, errorMsg);
		return new AsyncTaskDto(task);
	}
}
