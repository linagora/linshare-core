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
import org.linagora.linshare.core.facade.webservice.user.DocumentAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;

public class DocumentUploadAsyncTask extends AsyncTask<DocumentTaskContext> {

	protected final DocumentAsyncFacade asyncFacade;

	public DocumentUploadAsyncTask(DocumentAsyncFacade asyncFacade,
			DocumentTaskContext task, AsyncTaskDto asyncTaskDto) {
		super(asyncFacade, task, asyncTaskDto);
		this.asyncFacade = asyncFacade;
	}

	@Override
	protected String runMyTask(DocumentTaskContext task) {
		DocumentDto dto = asyncFacade.upload(task);
		return dto.getUuid();
	}

	@Override
	public String toString() {
		return "DocumentUploadAsyncTask [uuid=" + uuid + ", task=" + task + "]";
	}

}
