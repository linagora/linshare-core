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
