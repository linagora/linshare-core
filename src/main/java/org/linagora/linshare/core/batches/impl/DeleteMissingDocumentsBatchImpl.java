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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.ThreadEntryService;

public class DeleteMissingDocumentsBatchImpl extends GenericBatchImpl {

	private final DocumentRepository documentRepository;

	private final DocumentEntryService documentEntryService;

	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final ShareService shareService;

	private final FileSystemDao fileSystemDao;

	private final ThreadEntryService threadEntryService;

	public DeleteMissingDocumentsBatchImpl(
			AccountRepository<Account> accountRepository,
			DocumentRepository documentRepository,
			DocumentEntryService service,
			DocumentEntryBusinessService documentEntryBusinessService,
			ShareService shareService,
			ThreadEntryService threadEntryService,
			FileSystemDao fileSystemDao) {
		super(accountRepository);
		this.documentRepository = documentRepository;
		this.documentEntryService = service;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.shareService = shareService;
		this.fileSystemDao = fileSystemDao;
		this.threadEntryService = threadEntryService;
	}

	@Override
	public List<String> getAll() {
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = documentRepository.findAllIdentifiers();
		logger.info(entries.size()
				+ " document(s) have been found to be checked");
		return entries;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		Document resource = documentRepository.findByUuid(identifier);
		logInfo(total, position,
				"processing document : " + resource.getRepresentation());
		Context context = new BatchResultContext<Document>(resource);
		if (!exists(resource)) {
			DocumentEntry documentEntry = resource.getDocumentEntry();
			if (documentEntry != null) {
				logger.debug("Document entry : {}", documentEntry);
				Account owner = documentEntry.getEntryOwner();
				if (documentEntryService.getRelatedEntriesCount(actor, owner,
						documentEntry) > 0) {
					shareService.deleteAllShareEntries(actor, owner,
							documentEntry.getUuid());
				}
				documentEntryService.deleteInconsistentDocumentEntry(actor,
						documentEntry);
				logWarn(total,
						position,
						"The inconsistent document (document entry related){} has been successfully deleted.",
						resource.getRepresentation());
			} else if (resource.getThreadEntry() != null) {
				threadEntryService.deleteInconsistentThreadEntry(actor,
						resource.getThreadEntry());
				logWarn(total,
						position,
						"The inconsistent document (thread entry related) {} has been successfully deleted.",
						resource.getRepresentation());
			} else {
				try {
					documentEntryBusinessService.deleteDocument(resource);
				} catch (org.springmodules.jcr.JcrSystemException e) {
					logger.error("Probably an invalid or missing uuid : ", e);
					documentRepository.delete(resource);
				}
				logWarn(total,
						position,
						"Removing a document unrelated to an entry  {} because of inconsistency",
						resource.getRepresentation());
			}
		}
		return context;
	}

	private boolean exists(Document resource) {
		boolean exist = false;
		InputStream stream = null;
		try {
			stream = fileSystemDao.getFileContentByUUID(resource.getUuid());
			if (stream != null) {
				exist = true;
			}
		} catch (org.springmodules.jcr.JcrSystemException e) {
			// TODO : should never happen ! need to send a notification to
			// admins.
			logger.warn(
					"Document with UID = {} was not found in datastore : {}",
					resource.getUuid(), e.getMessage());
			logger.debug("JcrSystemException : ", e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				logger.debug(
						"Error when closing document stream '{}' during consistency check ",
						resource.getRepresentation());
				logger.debug("IOException : ", e);
			}
		}
		return exist;
	}

	@Override
	public void notify(Context context, long total, long position) {
		// Nothing to notify.
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			String identifier, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Document> context = (BatchResultContext<Document>) exception
				.getContext();
		Document entry = context.getResource();
		logError(total, position, "cleaning document has failed : {}",
				entry.getRepresentation(), exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors,
			long total, long processed) {
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
