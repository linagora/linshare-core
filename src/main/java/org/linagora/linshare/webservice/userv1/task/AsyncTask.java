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

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.user.GenericAsyncFacade;
import org.linagora.linshare.webservice.userv1.task.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AsyncTask<R extends TaskContext> implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final GenericAsyncFacade asyncFacade;

	protected final String uuid;

	protected R task;

	public AsyncTask(GenericAsyncFacade asyncFacade, R task,
			AsyncTaskDto asyncTaskDto) {
		super();
		this.asyncFacade = asyncFacade;
		this.uuid = asyncTaskDto.getUuid();
		this.task = task;
	}

	public String getUuid() {
		return uuid;
	}

	/**
	 * process the task and return the uuid of the created resource.
	 * @param task : async tack to run.
	 * @return uuid of created resource.
	 */
	protected abstract String runMyTask(R task);

	@Override
	public void run() {
		logger.info("Begin processing async task : " + getUuid());
		asyncFacade.processing(task, getUuid());
		try {
			String resourceUuid = runMyTask(task);
			asyncFacade.success(task, getUuid(), resourceUuid);
			logger.info("Async task '" + getUuid()
					+ "' processed with final status : SUCCESS");
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug("BusinessException : ", e);
			asyncFacade.fail(task, getUuid(), e.getErrorCode().getCode(), e.getErrorCode().name(), e.getMessage());
			logger.error("Async task '" + getUuid()
					+ "' processed with final status : FAILED");
		} catch (Exception e) {
			String message = e.getMessage();
			if (message == null) {
				message = e.toString();
			}
			logger.error(message);
			logger.debug("Exception : ", e);
			asyncFacade.fail(task, getUuid(),  message);
			logger.error("Async task '" + getUuid()
					+ "' processed with final status : FAILED");
		}
	}

}
