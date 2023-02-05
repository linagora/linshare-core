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
import org.linagora.linshare.core.domain.constants.UpgradeLogCriticity;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.mongo.entities.UpgradeTaskLog;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class UpgradeTaskConsoleImpl extends BatchConsoleImpl implements BatchConsole {

	protected UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository;

	public UpgradeTaskConsoleImpl(Class<? extends GenericBatch> clazz,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository
			) {
		super(clazz);
		this.upgradeTaskLogMongoRepository = upgradeTaskLogMongoRepository;
	}

	@Override
	public void logDebug(BatchRunContext batchRunContext, long total, long position, String message, Object... args) {
		super.logDebug(batchRunContext, total, position, message, args);
		String msg = getStringPosition(total, position) + message;
		UpgradeTaskLog log = new UpgradeTaskLog(batchRunContext, UpgradeLogCriticity.DEBUG, msg);
		upgradeTaskLogMongoRepository.insert(log);
	}

	@Override
	public void logInfo(BatchRunContext batchRunContext, String message, Object... args) {
		super.logInfo(batchRunContext, message, args);
		UpgradeTaskLog log = new UpgradeTaskLog(batchRunContext, UpgradeLogCriticity.INFO, message);
		upgradeTaskLogMongoRepository.insert(log);
	}

	@Override
	public void logInfo(BatchRunContext batchRunContext, long total, long position, String message, Object... args) {
		super.logInfo(batchRunContext, total, position, message, args);
		String msg = getStringPosition(total, position) + message;
		UpgradeTaskLog log = new UpgradeTaskLog(batchRunContext, UpgradeLogCriticity.INFO, msg);
		upgradeTaskLogMongoRepository.insert(log);
	}

	@Override
	public void logWarn(BatchRunContext batchRunContext, String message, Object... args) {
		super.logWarn(batchRunContext, message, args);
		UpgradeTaskLog log = new UpgradeTaskLog(batchRunContext, UpgradeLogCriticity.WARN, message);
		upgradeTaskLogMongoRepository.insert(log);
	}

	@Override
	public void logWarn(BatchRunContext batchRunContext, long total, long position, String message, Object... args) {
		super.logWarn(batchRunContext, total, position, message, args);
		String msg = getStringPosition(total, position) + message;
		UpgradeTaskLog log = new UpgradeTaskLog(batchRunContext, UpgradeLogCriticity.WARN, msg);
		upgradeTaskLogMongoRepository.insert(log);
	}

	@Override
	public void logError(BatchRunContext batchRunContext, long total, long position, String message, Object... args) {
		super.logError(batchRunContext, total, position, message, args);
		String msg = getStringPosition(total, position) + message;
		UpgradeTaskLog log = new UpgradeTaskLog(batchRunContext, UpgradeLogCriticity.ERROR, msg);
		upgradeTaskLogMongoRepository.insert(log);
	}

	@Override
	public void logError(BatchRunContext batchRunContext, String message, Object... args) {
		super.logError(batchRunContext, message, args);
		UpgradeTaskLog log = new UpgradeTaskLog(batchRunContext, UpgradeLogCriticity.ERROR, message);
		upgradeTaskLogMongoRepository.insert(log);
	}
}
