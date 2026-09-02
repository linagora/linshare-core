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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.impl.EncryptedFileDataStoreImpl;
import org.linagora.linshare.core.dao.impl.MigrationOutcome;
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

/**
 * Restartable, idempotent legacy-to-encrypted blob migration (docs/ARCH.md
 * 15, docs/CLAUDE.md 26). A no-op ({@link #needToRun()}) unless the
 * configured {@code fileDataStore} is actually the encryption decorator —
 * i.e. unless encryption reads are enabled — so installations that never
 * turn encryption on never pay for a scan.
 *
 * <p>Every document is re-examined on every run rather than tracked via a
 * persisted flag: {@link EncryptedFileDataStoreImpl#isLegacyBlob} is a cheap
 * few-byte peek, so already-migrated documents are inexpensive no-ops, and
 * this sidesteps needing a schema change purely to remember migration state.
 */
public class MigrateLegacyDocumentsBatchImpl extends GenericBatchImpl {

	private final DocumentRepository documentRepository;

	private final FileDataStore fileDataStore;

	public MigrateLegacyDocumentsBatchImpl(AccountRepository<Account> accountRepository,
			DocumentRepository documentRepository, FileDataStore fileDataStore) {
		super(accountRepository);
		this.documentRepository = documentRepository;
		this.fileDataStore = fileDataStore;
	}

	@Override
	public boolean needToRun() {
		return fileDataStore instanceof EncryptedFileDataStoreImpl;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		if (!needToRun()) {
			return Collections.emptyList();
		}
		List<String> entries = documentRepository.findAllIdentifiers();
		logger.info("{} document(s) will be checked for legacy (pre-encryption) storage format.", entries.size());
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Document document = documentRepository.findByUuid(identifier);
		if (document == null) {
			return null;
		}
		ResultContext context = new BatchResultContext<Document>(document);
		context.setProcessed(false);
		if (document.getBucketUuid() == null) {
			console.logWarn(batchRunContext, total, position,
					"Document {} has no bucketUuid, skipping migration.", identifier);
			return context;
		}

		EncryptedFileDataStoreImpl encryptedStore = (EncryptedFileDataStoreImpl) fileDataStore;
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, document);
		try {
			MigrationOutcome outcome = encryptedStore.migrateLegacyBlob(metadata, document.getSha256sum());
			console.logDebug(batchRunContext, total, position, "Document {} migration outcome: {}", identifier,
					outcome);
			context.setProcessed(outcome == MigrationOutcome.MIGRATED);
		} catch (IOException e) {
			throw new BatchBusinessException(context,
					"Failed to migrate document " + identifier + " to encrypted storage: " + e.getMessage());
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Document> cc = (BatchResultContext<Document>) context;
		if (Boolean.TRUE.equals(cc.getProcessed())) {
			console.logInfo(batchRunContext, total, position, "Document {} migrated to encrypted storage.",
					cc.getResource().getUuid());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		console.logError(batchRunContext, total, position, "Document {} migration failed: {}", identifier,
				exception.getMessage());
	}
}
