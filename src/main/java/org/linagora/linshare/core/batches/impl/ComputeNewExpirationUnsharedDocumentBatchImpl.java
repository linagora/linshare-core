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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.EntryBatchResultContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class ComputeNewExpirationUnsharedDocumentBatchImpl extends GenericBatchImpl {

	private final DocumentEntryRepository documentEntryRepository;

	private final AbstractDomainRepository domainRepository;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	public ComputeNewExpirationUnsharedDocumentBatchImpl(
			AccountRepository<Account> accountRepository,
			DocumentEntryRepository documentEntryRepository,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			AbstractDomainRepository domainRepository) {
		super(accountRepository);
		this.documentEntryRepository = documentEntryRepository;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.domainRepository = domainRepository;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("{} job starting ...", getClass().toString());
		List<AbstractDomain> domains = new ArrayList<>();
		for (AbstractDomain domain : domainRepository.findAllDomain()) {
			TimeUnitValueFunctionality functionality = functionalityReadOnlyService
					.getDefaultFileExpiryTimeFunctionality(domain);
			if (functionality.getActivationPolicy().getStatus()) {
				domains.add(domain);
			}
		}
		List<String> documents = documentEntryRepository.findDocumentsWithNullExpiration(domains);
		logger.info(" {} document(s) have been found, expiration date will be computed for each one .",
				documents.size());
		return documents;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		DocumentEntry documentEntry = documentEntryRepository.findById(identifier);
		ResultContext context = new EntryBatchResultContext(documentEntry);
		context.setProcessed(false);
		long relatedEntriesCount = documentEntryRepository.getRelatedEntriesCount(documentEntry);
		if (relatedEntriesCount == 0) {
			console.logInfo(batchRunContext, total, position,
					"processing document entry : " + documentEntry.getRepresentation());
			AbstractDomain domain = documentEntry.getEntryOwner().getDomain();
			TimeUnitValueFunctionality functionality = functionalityReadOnlyService
					.getDefaultFileExpiryTimeFunctionality(domain);
			Calendar calendar = (Calendar) documentEntry.getCreationDate().clone();
			calendar.add(functionality.toCalendarValue(), functionality.getValue());
			documentEntry.setExpirationDate(calendar);
			documentEntryRepository.update(documentEntry);
			context.setProcessed(true);
		} else {
			documentEntry.setShared(relatedEntriesCount);
			documentEntryRepository.update(documentEntry);
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		EntryBatchResultContext shareContext = (EntryBatchResultContext) context;
		Entry entry = shareContext.getResource();
		if (context.getProcessed()) {
			console.logInfo(batchRunContext, total, position, "The document entry " + entry.getRepresentation()
					+ " has been successfully updated with new expiration date.");
		} else {
			console.logInfo(batchRunContext, total, position,
					"The document entry " + entry.getRepresentation() + " has been ignored.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		EntryBatchResultContext context = (EntryBatchResultContext) exception.getContext();
		Entry entry = context.getResource();
		console.logError(batchRunContext, total, position,
				"updating document entry has failed : " + entry.getRepresentation());
	}

}
