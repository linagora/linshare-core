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
package org.linagora.linshare.core.upgrade.v6_0;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.impl.MigrationFileDataStoreImpl;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Thumbnail;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

import com.google.common.io.ByteSource;

public class FileDataStoreMigrationUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected DocumentRepository repository;

	protected FileDataStore fileDataStore;

	public FileDataStoreMigrationUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			DocumentRepository repository,
			FileDataStore fileDataStore) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.repository = repository;
		this.fileDataStore = fileDataStore;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.OPTIONAL_MIGRATE_FILE_DATA_STORAGE_TO_A_NEW_ONE;
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
			console.logError(batchRunContext, total, position, "Wrong fileDatastore ! Check spring profiles.");
			throw new TechnicalException("Wrong fileDatastore ! Check spring profiles.");
		}
		Document document = repository.findByUuid(identifier);
		if (document == null) {
			return null;
		}
		BatchResultContext<Document> res = new BatchResultContext<Document>(document);
		res.setProcessed(false);
		console.logDebug(batchRunContext, total, position, "Processing document : " + document.toString());

		try {
			FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, document);
			ByteSource byteSource = fileDataStore.get(metadata);
			metadata = fileDataStore.add(byteSource, metadata);
			document.setBucketUuid(metadata.getBucketUuid());
			if (document.getHasThumbnail()) {
				console.logDebug(batchRunContext, total, position, "document has thumbnail.");
				Map<ThumbnailType, Thumbnail> thumbnails = document.getThumbnails();
				Set<ThumbnailType> thumbnailTypes = thumbnails.keySet();
				for (ThumbnailType thumbnailType : thumbnailTypes) {
					console.logDebug(batchRunContext, total, position, "thumbnailType: " + thumbnailType);
					FileMetaDataKind metaDataKind = ThumbnailType.toFileMetaDataKind(thumbnailType);
					console.logDebug(batchRunContext, total, position, "metaDataKind: " + metaDataKind);
					FileMetaData metadataTmb = new FileMetaData(metaDataKind, document);
					ByteSource byteSourceTmb = fileDataStore.get(metadataTmb);
					metadata = fileDataStore.add(byteSourceTmb, metadataTmb);
				}
			}
		} catch (IOException e) {
			String msg = String.format("Can not copy the current document to the new file data store : %1$s.", document.toString());
			console.logError(batchRunContext, msg);
			logger.error(e.getMessage(), e);
			return res;
		}
		document.setToUpgrade(false);
		repository.update(document);
		res.setProcessed(true);
		return res;
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
