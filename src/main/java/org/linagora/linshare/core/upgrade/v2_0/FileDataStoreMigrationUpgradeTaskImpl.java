/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.upgrade.v2_0;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.impl.MigrationFileDataStoreImpl;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.ThreadEntryRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class FileDataStoreMigrationUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected DocumentRepository repository;

	protected DocumentEntryRepository documentEntryRepository;

	protected ThreadEntryRepository threadEntryRepository;

	protected FileDataStore fileDataStore;

	private boolean deduplication;

	public FileDataStoreMigrationUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository, DocumentRepository repository,
			DocumentEntryRepository documentEntryRepository, ThreadEntryRepository threadEntryRepository,
			FileDataStore fileDataStore, boolean deduplication) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.repository = repository;
		this.documentEntryRepository = documentEntryRepository;
		this.threadEntryRepository = threadEntryRepository;
		this.fileDataStore = fileDataStore;
		this.deduplication = deduplication;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_0_UPGRADE_STORAGE;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> documents = repository.findAllDocumentsToUpgrade();
		return documents;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		if (!(fileDataStore instanceof MigrationFileDataStoreImpl)) {
			throw new TechnicalException("Wrong fileDatastore ! Check spring profiles.");
		}
		Document document = repository.findByUuid(identifier);
		if (document == null) {
			return null;
		}
		BatchResultContext<Document> res = new BatchResultContext<Document>(document);
		res.setProcessed(false);
		console.logDebug(batchRunContext, total, position, "Processing document : " + document.toString());

		if (!deduplication) {
			upgrade(batchRunContext, document, res);
		} else {
			List<Document> documents = repository.findBySha256Sum(document.getSha256sum());
			if (documents.isEmpty()) {
				upgrade(batchRunContext, document, res);
			} else {
				Document selected = null;
				for (Document d : documents) {
					if (d.getToUpgrade()) {
						continue;
					}
					selected = d;
					break;
				}
				if (selected == null) {
					upgrade(batchRunContext, document, res);
				} else {
					Set<DocumentEntry> documentEntries = document.getDocumentEntries();
					for (DocumentEntry documentEntry : documentEntries) {
						documentEntry.setDocument(selected);
						documentEntryRepository.update(documentEntry);
					}
					Set<ThreadEntry> threadEntries = document.getThreadEntries();
					for (ThreadEntry threadEntry : threadEntries) {
						threadEntry.setDocument(selected);
						threadEntryRepository.update(threadEntry);
					}
					repository.delete(document);
					res.setProcessed(true);
				}
			}
		}
		return res;
	}

	protected void upgrade(BatchRunContext batchRunContext, Document document, BatchResultContext<Document> res) {
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, document);
		FileMetaData metadataTmb = new FileMetaData(FileMetaDataKind.THUMBNAIL, document);
		if (document.getThmbUuid() != null) {
			try (InputStream stream = fileDataStore.get(metadata);
					InputStream streamTmb = fileDataStore.get(metadataTmb)) {
				metadata = fileDataStore.add(stream, metadata);
				if (!fileDataStore.exists(metadataTmb)) {
					metadataTmb = fileDataStore.add(streamTmb, metadataTmb);
				}
				document.setBucketUuid(metadata.getBucketUuid());
				document.setToUpgrade(false);
				repository.update(document);
				res.setProcessed(true);
			} catch (Exception e) {
				String msg = String.format("Can not copy the current document to the new file data store : %1$s.", document.toString());
				console.logError(batchRunContext, msg);
				logger.error(e.getMessage(), e);
				throw new BatchBusinessException(res, msg);
			}
		} else {
			try (InputStream stream = fileDataStore.get(metadata)) {
				metadata = fileDataStore.add(stream, metadata);
				document.setBucketUuid(metadata.getBucketUuid());
				document.setToUpgrade(false);
				repository.update(document);
				res.setProcessed(true);
			} catch (Exception e) {
				String msg = String.format("Can not copy the current document to the new file data store : %1$s.", document.toString());
				console.logError(batchRunContext, msg);
				logger.error(e.getMessage(), e);
				throw new BatchBusinessException(res, msg);
			}
		}
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Document> res = (BatchResultContext<Document>) context;
		Document resource = res.getResource();
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, resource + " has been updated.");
		} else {
			logInfo(batchRunContext, total, position, resource + " has been skipped.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Document> res = (BatchResultContext<Document>) exception.getContext();
		Document resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource.toString() + " failed.");
		logger.error("Error occured while updating the document : "
				+ resource +
				". BatchBusinessException", exception);
	}
}
