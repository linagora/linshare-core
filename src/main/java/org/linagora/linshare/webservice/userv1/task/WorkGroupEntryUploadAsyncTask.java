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

import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupEntryAsyncFacade;
import org.linagora.linshare.webservice.userv1.task.context.WorkGroupEntryTaskContext;

public class WorkGroupEntryUploadAsyncTask extends
		AsyncTask<WorkGroupEntryTaskContext> {

	protected final WorkGroupEntryAsyncFacade asyncFacade;

	public WorkGroupEntryUploadAsyncTask(WorkGroupEntryAsyncFacade asyncFacade,
			WorkGroupEntryTaskContext task, AsyncTaskDto asyncTaskDto) {
		super(asyncFacade, task, asyncTaskDto);
		this.asyncFacade = asyncFacade;
	}

	@Override
	protected String runMyTask(WorkGroupEntryTaskContext task) {
		try {
			final WorkGroupEntryDto dto = asyncFacade.upload(task);
			return dto.getUuid();
		}
		catch (final Throwable e) {
			logger.error("Failed to process upload task: {}", e.getMessage(), e);
			throw e;
		}
	}

}
