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

import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;

public class ComputeDocumentMimeTypeBatchImpl extends GenericBatchImpl {

	private final DocumentRepository documentRepository;

	private final FileDataStore fileDataStore;

	private final MimeTypeMagicNumberDao mimeTypeMagicNumberDao;

	public ComputeDocumentMimeTypeBatchImpl(
			AccountRepository<Account> accountRepository,
			DocumentRepository documentRepository,
			MimeTypeMagicNumberDao mimeTypeMagicNumberDao,
			FileDataStore fileDataStore) {
		super(accountRepository);
		this.documentRepository = documentRepository;
		this.fileDataStore = fileDataStore;
		this.mimeTypeMagicNumberDao = mimeTypeMagicNumberDao;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = documentRepository.findAllDocumentWithMimeTypeCheckEnabled();
		logger.info(entries.size()
				+ " document(s) have been found, mime type will be check again for each file.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Document resource = documentRepository.findByUuid(identifier);
		if (resource == null) {
			return null;
		}
		console.logInfo(batchRunContext, total,
				position, "processing document : " + resource.getRepresentation());
		ResultContext context = new BatchResultContext<Document>(resource);
		context.setProcessed(false);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, resource);
		if (fileDataStore.exists(metadata)) {
			try (InputStream stream = fileDataStore.get(metadata).openBufferedStream()) {
				if (stream != null) {
					String type = mimeTypeMagicNumberDao.getMimeType(stream);
					resource.setType(type);
					resource.setCheckMimeType(false);
					documentRepository.update(resource);
					context.setProcessed(true);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Document> cc = (BatchResultContext<Document>) context;
		if (cc.getProcessed()) {
			Document entry = cc.getResource();
			console.logInfo(batchRunContext, total, position, "Document mime type was updated {}.", entry.getRepresentation());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Document> context = (BatchResultContext<Document>) exception
				.getContext();
		Document entry = context.getResource();
		console.logError(batchRunContext, total, position, "Document mime type was not updated, batch has failed : {}",
				entry.getRepresentation(), exception);
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
