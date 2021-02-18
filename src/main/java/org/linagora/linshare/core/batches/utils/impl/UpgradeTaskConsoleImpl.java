/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
