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
package org.linagora.linshare.webservice.admin.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.batches.GenericUpgradeTask;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskStatus;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UpgradeTaskFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.UpgradeTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.user.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.user.BatchRunnerAsyncFacade;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.mongo.entities.UpgradeTaskLog;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.linagora.linshare.webservice.admin.UpgradeTaskRestService;
import org.linagora.linshare.webservice.userv1.task.BatchRunnerAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.BatchTaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;




@Api(value = "upgrade_tasks", description = "UpgradeTask administration service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Path("/upgrade_tasks")
public class UpgradeTaskRestServiceImpl implements UpgradeTaskRestService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected UpgradeTaskFacade facade ;

	protected BatchRunner batchRunner;

	protected BatchRunnerAsyncFacade batchRunnerAsyncFacade;

	protected AsyncTaskFacade asyncTaskFacade;

	protected UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository;

	protected org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	protected Map<UpgradeTaskType, GenericUpgradeTask> tasks;

	public UpgradeTaskRestServiceImpl(
			UpgradeTaskFacade facade,
			BatchRunner batchRunner,
			BatchRunnerAsyncFacade batchRunnerAsyncFacade,
			AsyncTaskFacade asyncTaskFacade,
			List<GenericUpgradeTask> tasks,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			ThreadPoolTaskExecutor taskExecutor) {
		super();
		this.facade = facade;
		this.batchRunner = batchRunner;
		this.batchRunnerAsyncFacade = batchRunnerAsyncFacade;
		this.asyncTaskFacade = asyncTaskFacade;
		this.taskExecutor = taskExecutor;
		this.upgradeTaskLogMongoRepository= upgradeTaskLogMongoRepository;
		this.tasks = Maps.newHashMap();
		for (GenericUpgradeTask gut : tasks) {
			this.tasks.put(gut.getUpgradeTaskType(), gut);
		}
	}

	@Path("/{identifier}")
	@GET
	@ApiOperation(value = "find upgrade task", response = UpgradeTaskDto.class)
	@Override
	public UpgradeTaskDto find(
			@ApiParam(value = "upgrade task Uuid", required = true)
				@PathParam("identifier") UpgradeTaskType identifier) throws BusinessException {
		return facade.find(identifier);
	}

	@Path("/")
	@GET
	@ApiOperation(value = "find all upgrade tasks", response = UpgradeTaskDto.class, responseContainer = "List")
	@Override
	public List<UpgradeTaskDto> findAll() throws BusinessException {
		return facade.findAll();
	}

	@Path("/{identifier}")
	@PUT
	@ApiOperation(value = "update an upgrade task", response = UpgradeTaskDto.class)
	@Override
	public UpgradeTaskDto trigger(
			@ApiParam(value = "upgrade task to update.", required = true) UpgradeTaskDto upgradeTaskDto,
			@ApiParam(value = "upgrade task uuid", required = true)
				@PathParam("identifier") UpgradeTaskType identifier,
			@ApiParam(value = "Force running the task even it may be alreadys running. Be careful.")
				@DefaultValue(value="false") @QueryParam("force") Boolean force
			) throws BusinessException {
		AccountDto actorDto = facade.getAuthenticatedAccountDto();
		UpgradeTaskDto taskDto = facade.find(identifier);

		if (!isAllowed(taskDto, force)) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "The current upgrade task can not be launch, current status : " + taskDto.getStatus());
		}

		// Check if previous task was successful
		if (taskDto.getParentIdentifier() != null) {
			UpgradeTaskDto parentTaskDto = facade.find(taskDto.getParentIdentifier());
			if (!parentTaskDto.getStatus().equals(UpgradeTaskStatus.SUCCESS)) {
				throw new BusinessException(BusinessErrorCode.FORBIDDEN,
						"The current upgrade task can not be launch, parent task not complete : " + parentTaskDto.getIdentifier() + " : " +parentTaskDto.getStatus());
			}
		}

		// Finding the good one
		GenericUpgradeTask upgradeTask = this.tasks.get(taskDto.getIdentifier());
		if (upgradeTask == null) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Can not find the current upgrade task");
		}

		// new asynchronous task in the database
		AsyncTaskDto asyncTask = asyncTaskFacade.create(taskDto, AsyncTaskType.UPGRADE_TASK);
		taskDto.setStatus(UpgradeTaskStatus.PENDING);
		taskDto.setAsyncTaskUuid(asyncTask.getUuid());
		taskDto = facade.update(taskDto);

		// new running context (what to run)
		BatchTaskContext batchTaskContext = new BatchTaskContext(actorDto, actorDto.getUuid(), upgradeTask);

		// new task to run the task in asynchronous mode (taskScheduler)
		BatchRunnerAsyncTask task = new BatchRunnerAsyncTask(batchRunnerAsyncFacade, batchTaskContext, asyncTask,
				actorDto.getUuid(), taskDto.getIdentifier().name(), batchRunner);

		// adding task to the pool
		taskExecutor.execute(task);

		return taskDto;
	}

	protected boolean isAllowed(UpgradeTaskDto taskDto, boolean force) {
		logger.info("test: " + taskDto.toString());
		UpgradeTaskStatus status = taskDto.getStatus();
		boolean  result = false;
		if (status.equals(UpgradeTaskStatus.NEW)
				|| status.equals(UpgradeTaskStatus.FAILED)
				) {
			result = true;
		} else if (force) {
			logger.warn("Overring upgrade task current status: {} ", taskDto.toString());
			result = true;
		}
		if (result) {
			UpgradeTaskType parentIdentifier = taskDto.getParentIdentifier();
			if (parentIdentifier != null) {
				UpgradeTaskDto parentTaskDto = facade.find(parentIdentifier);
				if (parentTaskDto.getStatus().equals(UpgradeTaskStatus.SUCCESS)) {
					result = true;
				}
			}
		}
		return result;
	}

	@Path("/{upgradeTaskUuid}/async_tasks")
	@GET
	@ApiOperation(value = "Get all async tasks created by an upgrade task.", response = AsyncTaskDto.class, responseContainer = "List")
	@Override
	public List<AsyncTaskDto> findAllAsyncTask(
			@ApiParam(value = "The upgrade tasks uuid.", required = true)
				@PathParam("upgradeTaskUuid") UpgradeTaskType upgradeTaskIdentifier) throws BusinessException {
		Validate.notNull(upgradeTaskIdentifier, "Missing upgradeTaskIdentifier");
		return asyncTaskFacade.findAll(upgradeTaskIdentifier);
	}

	@Path("/{upgradeTaskIdentifier}/async_tasks/{uuid}")
	@GET
	@ApiOperation(value = "Get one async task created by an upgrade task.", response = AsyncTaskDto.class)
	@Override
	public AsyncTaskDto findAsyncTask(
			@ApiParam(value = "The upgrade task identifier.", required = true)
				@PathParam("upgradeTaskIdentifier") UpgradeTaskType upgradeTaskIdentifier,
			@ApiParam(value = "The async task uuid.", required = true)
				@PathParam("uuid") String  uuid) throws BusinessException {
		Validate.notNull(upgradeTaskIdentifier, "Missing upgradeTaskUuid");
		Validate.notEmpty(uuid, "Missing async task uuid");
		// Just check if exists.
		facade.find(upgradeTaskIdentifier);
		return asyncTaskFacade.find(uuid);
	}

	@Path("/{upgradeTaskIdentifier}/async_tasks/{uuid}/console")
	@GET
	@ApiOperation(value = "Get one async task created by an upgrade task.", response = UpgradeTaskLog.class, responseContainer = "List")
	@Override
	public List<UpgradeTaskLog> console(
			@ApiParam(value = "The upgrade task identifier.", required = true)
				@PathParam("upgradeTaskIdentifier") UpgradeTaskType upgradeTaskIdentifier,
				@ApiParam(value = "The async task uuid.", required = true)
				@PathParam("uuid") String  asyncTaskUuid,
				@QueryParam("fromDate") String fromDate
			) throws BusinessException {
		UpgradeTaskDto dto = facade.find(upgradeTaskIdentifier);
		if (fromDate == null) {
			return upgradeTaskLogMongoRepository.findAllByUpgradeTaskAndAsyncTask(dto.getIdentifier().name(), asyncTaskUuid);
		} else {
			return upgradeTaskLogMongoRepository.findAllByUpgradeTaskAndAsyncTaskAndCreationDateAfter(dto.getIdentifier().name(), asyncTaskUuid, getDate(fromDate));
		}
	}

	protected Date getDate(String fromDate) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(fromDate);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(BusinessErrorCode.BAD_REQUEST, "Can not convert begin date.");
		}
	}
}
