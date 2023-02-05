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
package org.linagora.linshare.core.batches;

import java.util.List;

import org.linagora.linshare.core.batches.utils.BatchConsole;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;

public interface GenericBatch {

	String getBatchClassName();

	BatchConsole getConsole();

	void setConsole(BatchConsole  console);

	boolean needToRun();

	void start(BatchRunContext batchRunContext);

	List<String> getAll(BatchRunContext batchRunContext);

	ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException;

	void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position);

	void notifyError(BatchBusinessException exception, String identifier,
			long total, long position, BatchRunContext batchRunContext);

	void terminate(BatchRunContext batchRunContext, List<String> all, long errors,
			long unhandled_errors, long total, long processed);

	void fail(BatchRunContext batchRunContext, List<String> all, long errors,
			long unhandled_errors, long total, long processed);

}
