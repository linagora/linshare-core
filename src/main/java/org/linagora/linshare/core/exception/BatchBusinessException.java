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
package org.linagora.linshare.core.exception;

import org.linagora.linshare.core.job.quartz.ResultContext;

public class BatchBusinessException extends RuntimeException {

	private static final long serialVersionUID = -2766223512640837573L;

	protected ResultContext context;

	protected String message;

	protected BusinessException businessException;

	public BatchBusinessException(ResultContext context, String message) {
		super();
		this.context = context;
		this.message = message;
	}

	public ResultContext getContext() {
		return context;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public BusinessException getBusinessException() {
		return businessException;
	}

	public void setBusinessException(BusinessException businessException) {
		this.businessException = businessException;
	}

}
