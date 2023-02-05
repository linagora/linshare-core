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
package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DomainQuotaRepository;

public class ComputeSubDomainQuotaBatchImpl extends GenericBatchImpl {

	private final AbstractDomainRepository domainRepository;
	
	private final DomainQuotaRepository domainQuotaRepository;

	public ComputeSubDomainQuotaBatchImpl(
			AccountRepository<Account> accountRepository,
			AbstractDomainRepository domainRepository, DomainQuotaRepository domainQuotaRepository) {
		super(accountRepository);
		this.domainRepository = domainRepository;
		this.domainQuotaRepository = domainQuotaRepository;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		// First all top domains (aka root domain children.
		List<String> entries = domainRepository.getAllSubDomainIdentifiers(LinShareConstants.rootDomainIdentifier);
		// Then the root domain.
		entries.add(LinShareConstants.rootDomainIdentifier);
		logger.info(entries.size() + " domain(s) have been found.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain domain = domainRepository.findById(identifier);
		ResultContext context = new DomainBatchResultContext(domain);
		try {
			console.logDebug(batchRunContext, total, position, "processing domain : " + domain.toString());
			Long subdomains = domainQuotaRepository.sumOfCurrentValueForSubdomains(domain);
			DomainQuota quota = domainQuotaRepository.find(domain);
			quota.setCurrentValueForSubdomains(subdomains);
			domainQuotaRepository.update(quota);
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to updating domain.");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to update domain: " + domain.toString(), exception);
			throw exception;
		}
		
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		console.logDebug(batchRunContext, total, position, "Subdomains used space updated for domain : " + domainContext.getResource().toString());
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) exception.getContext();
		String ressource = domainContext.getResource().toString();
		console.logError(batchRunContext, total, position, "Subdomains used space update failed for domain : " + ressource);
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " domain(s) have been updated.");
		if (errors > 0) {
			logger.error(errors + " domain(s) failed to be updated.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " domain(s) failed to be updated (unhandled error).");
		}
		logger.info(getClass().toString() + " job terminated.");
	}
}
