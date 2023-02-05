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
package org.linagora.linshare.core.batches.utils.impl;

import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.batches.utils.BatchConsole;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchConsoleImpl implements BatchConsole {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public BatchConsoleImpl(Class<? extends GenericBatch> clazz) {
		super();
		this.logger = LoggerFactory.getLogger(clazz);
	}

	protected String getStringPosition(long total, long position) {
		return position + "/" + total + ":";
	}

	@Override
	public void logDebug(BatchRunContext batchRunContext, long total, long position, String message, Object... args) {
		logger.debug(getPrefix(total, position, batchRunContext) + message, args);
	}

	@Override
	public void logInfo(BatchRunContext batchRunContext, String message, Object... args) {
		logger.info(message, args);
	}

	@Override
	public void logInfo(BatchRunContext batchRunContext, long total, long position, String message, Object... args) {
		logger.info(getPrefix(total, position, batchRunContext) + message, args);
	}

	@Override
	public void logWarn(BatchRunContext batchRunContext, String message, Object... args) {
		logger.warn(message, args);
	}

	@Override
	public void logWarn(BatchRunContext batchRunContext, long total, long position, String message, Object... args) {
		logger.warn(getPrefix(total, position, batchRunContext) + message, args);
	}

	@Override
	public void logError(BatchRunContext batchRunContext, String message, Object... args) {
		logger.error(message, args);
	}

	@Override
	public void logError(BatchRunContext batchRunContext, long total, long position, String message, Object... args) {
		logger.error(getPrefix(total, position, batchRunContext) + message, args);
	}

	protected String getPrefix(long total, long position, BatchRunContext batchRunContext) {
		return getStringPosition(total, position);
	}
}
