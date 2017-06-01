/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
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
package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.EntryBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class ComputeDocumentExpirationBatchImpl extends GenericBatchImpl {
	
	private final FunctionalityReadOnlyService functionalityService;

	private final DocumentEntryService documentEntryService;

	public ComputeDocumentExpirationBatchImpl(final AccountRepository<Account> accountRepository,
			final FunctionalityReadOnlyService functionalityService,
			final DocumentEntryService documentEntryService) {
		super(accountRepository);
		this.functionalityService = functionalityService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public List<String> getAll() {
		SystemAccount actor = getSystemAccount();
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = documentEntryService.findAllEntriesWithoutExpirationDate(actor, actor);
		logger.info("Found " + entries.size() + " document(s) without expiration date.");
		return entries;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		DocumentEntry resource = documentEntryService.find(actor, actor, identifier);
		Context context = new EntryBatchResultContext(resource);
		if (resource == null) {
			return null;
		}
		// Don't expire a document if it still have shares associated.
		if (documentEntryService.getRelatedEntriesCount(actor, resource.getEntryOwner(), resource) > 0) {
			// TODO calling documentEntryService.getRelatedEntriesCount so often is not optimal.
			// If possible, we should ensure that #getAll() returns a list already filtered.
			return null;
		}
		try {
			logInfo(total, position,
					"processing share : " + resource.getRepresentation());
			AbstractDomain domain = resource.getEntryOwner().getDomain();
			TimeUnitValueFunctionality func = functionalityService
					.getDefaultFileExpiryTimeFunctionality(domain);
			context.setProcessed(false);
			if (func.getActivationPolicy().getStatus()) {
				documentEntryService.deleteOrComputeExpiryDate(actor, domain, resource);
				context.setProcessed(true);
			}
		} catch (BusinessException businessException) {
				logError(total, position,
						"Error while trying to compute expiration date : " + resource.getRepresentation());
				logger.info("Error occured while cleaning expired shares",
						businessException);
				BatchBusinessException exception = new BatchBusinessException(
						context, "Error while trying to delete expired shares.");
				exception.setBusinessException(businessException);
				throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		EntryBatchResultContext documentContext = (EntryBatchResultContext) context;
		Entry entry = documentContext.getResource();
		logInfo(total, position, "Expiration date succcessfully computed for "
				+ entry.getRepresentation());
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		EntryBatchResultContext documentContext = (EntryBatchResultContext) exception
				.getContext();
		Entry entry = documentContext.getResource();
		logError(total, position,
				"computing expiration date failed for " + entry.getRepresentation());
		logger.error(
				"Error occured while computing expiration date for "
						+ entry.getRepresentation() + ". BatchBusinessException ",
				exception);
	}

}
