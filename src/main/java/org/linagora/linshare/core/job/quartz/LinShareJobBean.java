/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2015 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.job.quartz;

import java.util.List;

import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class LinShareJobBean extends QuartzJobBean {

	private static final Logger logger = LoggerFactory
			.getLogger(LinShareJobBean.class);

	protected List<GenericBatch> batchs;

	public LinShareJobBean() {
		super();
	}

	public void setBatch(List<GenericBatch> batchs) {
		this.batchs = batchs;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		executeExternal();
	}

	public boolean executeExternal()
			throws JobExecutionException {
		logger.debug("number of batches : " + batchs.size());
		boolean finalResult = true;
		for (GenericBatch batch : batchs) {
			List<String> all = batch.getAll();
			long position = 1;
			long errors = 0;
			long processed = 0;
			long unhandled_errors = 0;
			long total = all.size();
			for (String resource : all) {
				try {
					batch.logDebug(total, position, "processing resource '" + resource + "' ...");
					Context batchResult = batch.execute(resource, total, position);
					if (batchResult.getProcessed() != null) {
						if (batchResult.getProcessed().equals(true)) {
							processed ++;
						}
					}
					batch.notify(batchResult, total, position);
				} catch (BatchBusinessException ex) {
					errors++;
					batch.notifyError(ex, resource, total, position);
				} catch (TechnicalException ex) {
					unhandled_errors++;
					logger.error("Unhandled TechnicalException in batches !");
					logger.error(ex.getMessage(), ex);
					logger.error("Cannot process resource '{}' ", resource);
					logger.error("CRITICAL ERROR : Batch stopped !");
					finalResult = false;
					break;
				} catch (BusinessException ex) {
					unhandled_errors++;
					logger.error("Unhandled business exception in batches !");
					logger.error(ex.getMessage(), ex);
					logger.error("Cannot process resource '{}' ", resource);
					finalResult = false;
				}
				batch.logDebug(total, position, "resource processed.");
				position++;
			}
			batch.terminate(all, errors, unhandled_errors, total, processed);
		}
		return finalResult;
	}
}
