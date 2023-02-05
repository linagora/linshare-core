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
package org.linagora.linshare.core.runner.impl;

import java.util.List;

import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.batches.utils.BatchConsole;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.runner.BatchRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchRunnerImpl implements BatchRunner {

	private static final Logger logger = LoggerFactory.getLogger(BatchRunnerImpl.class);

	public BatchRunnerImpl() {
		super();
	}

	@Override
	public boolean execute(GenericBatch batch) {
		BatchRunContext batchRunContext = new BatchRunContext();
		return execute(batch, batchRunContext);
	}

	@Override
	public boolean execute(List<GenericBatch> batchs) {
		logger.debug("number of batches : " + batchs.size());
		boolean finalResult = true;
		for (GenericBatch batch : batchs) {
			BatchRunContext batchRunContext = new BatchRunContext();
			boolean execute = execute(batch, batchRunContext);
			if (!execute) {
				finalResult = false;
			}
		}
		return finalResult;
	}

	@Override
	public boolean execute(GenericBatch batch, BatchRunContext batchRunContext) {
		boolean finalResult = true;
		BatchConsole console = batch.getConsole();
		if (!batch.needToRun()) {
			console.logInfo(batchRunContext, "batch skipped.");
			return finalResult;
		}
		batch.start(batchRunContext);
		List<String> all = batch.getAll(batchRunContext);
		logger.debug(all.size() + " resources(s) have been found.");
		long position = 1;
		long errors = 0;
		long processed = 0;
		long unhandled_errors = 0;
		long total = all.size();
		for (String resource : all) {
			try {
				console.logDebug(batchRunContext, total, position, "processing resource '" + resource + "' ...");
				ResultContext batchResult = batch.execute(batchRunContext, resource, total, position);
				// null if resource was skipped (nothing to do or not found)
				if (batchResult != null) {
					if (batchResult.getProcessed() != null) {
						if (batchResult.getProcessed().equals(true)) {
							processed++;
						}
					}
					batch.notify(batchRunContext, batchResult, total, position);
				} else {
					console.logDebug(batchRunContext, total, position, "Resource not found, skipped : " + resource);
				}
			} catch (BatchBusinessException ex) {
				errors++;
				batch.notifyError(ex, resource, total, position, batchRunContext);
			} catch (BusinessException ex) {
				unhandled_errors++;
				console.logError(batchRunContext, total, position, "Unhandled BusinessException in batch :  " + batch.getBatchClassName());
				logger.error(ex.getMessage(), ex);
				logger.error("Cannot process resource '{}' ", resource);
				finalResult = false;
			} catch (TechnicalException ex) {
				unhandled_errors++;
				console.logError(batchRunContext, total, position, "Unhandled TechnicalException in batch : " + batch.getBatchClassName());
				logger.error(ex.getMessage(), ex);
				logger.error("Cannot process resource '{}' ", resource);
				logger.error("CRITICAL ERROR : Batch stopped !");
				finalResult = false;
				break;
			} catch (Exception ex) {
				unhandled_errors++;
				console.logError(batchRunContext, total, position, "Unhandled Exception in batch : " + batch.getBatchClassName());
				logger.error(ex.getMessage(), ex);
				logger.error("Cannot process resource '{}' ", resource);
				logger.error("CRITICAL ERROR : Batch stopped !");
				finalResult = false;
				break;
			}
			position++;
		}
		if (finalResult) {
			batch.terminate(batchRunContext, all, errors, unhandled_errors, total, processed);
		} else {
			batch.fail(batchRunContext, all, errors, unhandled_errors, total, processed);
		}
		return finalResult;
	}

}
