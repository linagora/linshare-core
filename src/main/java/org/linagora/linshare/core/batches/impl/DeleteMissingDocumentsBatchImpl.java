/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
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
import java.util.Set;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.WorkGroupDocumentService;

import com.google.common.collect.Lists;

public class DeleteMissingDocumentsBatchImpl extends GenericBatchImpl {

	private final DocumentRepository documentRepository;

	private final DocumentEntryService documentEntryService;

	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final ShareService shareService;

	private final FileDataStore fileDataStore;

	private final WorkGroupDocumentService threadEntryService;

	public DeleteMissingDocumentsBatchImpl(
			AccountRepository<Account> accountRepository,
			DocumentRepository documentRepository,
			DocumentEntryService service,
			DocumentEntryBusinessService documentEntryBusinessService,
			ShareService shareService,
			WorkGroupDocumentService threadEntryService,
			FileDataStore fileDataStore) {
		super(accountRepository);
		this.documentRepository = documentRepository;
		this.documentEntryService = service;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.shareService = shareService;
		this.fileDataStore = fileDataStore;
		this.threadEntryService = threadEntryService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = documentRepository.findAllIdentifiers();
		logger.info(entries.size()
				+ " document(s) have been found to be checked");
		// FIXME
		return Lists.newArrayList();
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		Document resource = documentRepository.findByUuid(identifier);
		if (resource == null) {
			return null;
		}
		logDebug(batchRunContext, total, position,
				"processing document : " + resource.getRepresentation());
		ResultContext context = new BatchResultContext<Document>(resource);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, resource);
		if (!fileDataStore.exists(metadata)) {
			Set<DocumentEntry> documentEntries = resource.getDocumentEntries();
			for (DocumentEntry documentEntry : documentEntries) {
				if (documentEntry != null) {
					logger.debug("Document entry : {}", documentEntry);
					Account owner = documentEntry.getEntryOwner();
					if (documentEntryService.getRelatedEntriesCount(actor, owner,
							documentEntry) > 0) {
						shareService.deleteAllShareEntries(actor, owner,
								documentEntry.getUuid(), LogActionCause.INCONSISTENCY);
					}
					documentEntryService.deleteInconsistentDocumentEntry(actor,
							documentEntry);
					logWarn(total,
							position,
							"The inconsistent document (document entry related){} has been successfully deleted.",
							batchRunContext, resource.getRepresentation());
				}
			}
			// TODO:FMA:Workgroups
//			Set<ThreadEntry> threadEntries = resource.getThreadEntries();
//			for (ThreadEntry threadEntry : threadEntries) {
//				if (threadEntry != null) {
//					threadEntryService.deleteInconsistentThreadEntry(actor,
//							threadEntry);
//					logWarn(total,
//							position,
//							"The inconsistent document (thread entry related) {} has been successfully deleted.",
//							resource.getRepresentation());
//				}
//			}
			try {
				documentEntryBusinessService.deleteDocument(resource);
			} catch (org.springmodules.jcr.JcrSystemException e) {
				logger.error("Probably an invalid or missing uuid : ", e);
				documentRepository.delete(resource);
			}
			logWarn(total,
					position,
					"Removing a document unrelated to an entry  {} because of inconsistency",
					batchRunContext, resource.getRepresentation());
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		// Nothing to notify.
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Document> context = (BatchResultContext<Document>) exception
				.getContext();
		Document entry = context.getResource();
		logError(total, position, "cleaning document has failed : {}",
				batchRunContext, entry.getRepresentation(), exception);
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors,
			long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " document(s) have been checked.");
		if (errors > 0) {
			logger.error(errors + " document(s) failed to be checked.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " document(s) failed to be checked (unhandled error).");
		}
		logger.info(getClass().toString() + " job terminated.");
	}
}
