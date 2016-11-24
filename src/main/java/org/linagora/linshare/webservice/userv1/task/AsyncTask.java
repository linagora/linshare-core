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
