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
package org.linagora.linshare.mongo.entities;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.webservice.userv1.task.context.WorkGroupEntryTaskContext;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement(name = "SharedSpaceAsyncTask")
public class WorkGroupAsyncTask extends WorkGroupNode {

	@Transient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected AsyncTaskDto async;

	public WorkGroupAsyncTask() {
		super();
	}

	public WorkGroupAsyncTask(AsyncTaskDto asyncTask,
			WorkGroupEntryTaskContext workGroupEntryTaskContext) {
		this.async = asyncTask;
		this.name = workGroupEntryTaskContext.getFileName();
		this.nodeType = WorkGroupNodeType.ASYNC_TASK;
	}

	public AsyncTaskDto getAsync() {
		return async;
	}

	public void setAsync(AsyncTaskDto async) {
		this.async = async;
	}
}
