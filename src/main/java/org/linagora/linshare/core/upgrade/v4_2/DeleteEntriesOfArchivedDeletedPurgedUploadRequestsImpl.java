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
package org.linagora.linshare.core.upgrade.v4_2;

import java.util.List;
import java.util.Objects;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UploadRequestEntryRepository;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class DeleteEntriesOfArchivedDeletedPurgedUploadRequestsImpl extends GenericUpgradeTaskImpl {

	private final UploadRequestEntryService uploadRequestEntryService;

	private final UploadRequestEntryRepository uploadRequestEntryRepository;

	public DeleteEntriesOfArchivedDeletedPurgedUploadRequestsImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			UploadRequestEntryService uploadRequestEntryService,
			UploadRequestEntryRepository uploadRequestEntryRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.uploadRequestEntryService = uploadRequestEntryService;
		this.uploadRequestEntryRepository = uploadRequestEntryRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_DELETE_ENTRIES_OF_ARCHIVED_DELETED_PURGED_UPLOAD_REQUESTS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("{} job is starting ...", getClass().toString());
		List<String> uuids = uploadRequestEntryRepository.findAllEntriesForArchivedDeletedPurgedUR();
		logger.info("{} uploadRequestEntries has been found.", uuids.size());
		return uuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		UploadRequestEntry entry = uploadRequestEntryService.find(getSystemAccount(), getSystemAccount(), identifier);
		BatchResultContext<UploadRequestEntry> batchResultContext = new BatchResultContext<>(entry);
		if (Objects.isNull(entry)) {
			batchResultContext.setProcessed(false);
			return batchResultContext;
		}
		uploadRequestEntryRepository.delete(entry);
		uploadRequestEntryService.delFromQuota(entry.getEntryOwner(), entry.getSize());
		batchResultContext.setProcessed(true);
		return batchResultContext;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadRequestEntry> batchResultContext = (BatchResultContext<UploadRequestEntry>) context;
		UploadRequestEntry resource = batchResultContext.getResource();
		logInfo(batchRunContext, total, position, "{} has been deleted.", resource);
		if (batchResultContext.getProcessed()) {
			logInfo(batchRunContext, total, position, "The uploadRequestEntry has been successfully deleted : {}", resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "The uploadRequestEntry's deletion has been skipped : {}", resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadRequestEntry> res = (BatchResultContext<UploadRequestEntry>) exception.getContext();
		UploadRequestEntry resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing uploadRequestEntry's deletion : {} . BatchBusinessException", resource,
				exception);
	}

}
