/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

package org.linagora.linshare.core.upgrade.v2_2;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestHistory;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UploadRequestHistoryRepository;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.mongo.entities.logs.UploadRequestAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.UploadRequestMto;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class MigrateHistoryToMongoUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final UploadRequestHistoryRepository uploadRequestHistoryRepository;

	private LogEntryService logEntryService;

	private UploadRequestRepository uploadRequestRepository;

	private AuditUserMongoRepository auditUserMongoRepository;

	public MigrateHistoryToMongoUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			UploadRequestHistoryRepository uploadRequestHistoryRepository,
			LogEntryService logEntryService,
			UploadRequestRepository uploadRequestRepository,
			AuditUserMongoRepository auditUserMongoRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.uploadRequestHistoryRepository = uploadRequestHistoryRepository;
		this.logEntryService = logEntryService;
		this.uploadRequestRepository = uploadRequestRepository;
		this.auditUserMongoRepository = auditUserMongoRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_2_MIGRATE_UPLOAD_REQUEST_HISTORY_TO_MONGO_AUDIT;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> historiesUuids = uploadRequestHistoryRepository.findAllUuid();
		return historiesUuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		UploadRequestHistory uploadRequestHistory = uploadRequestHistoryRepository.findByUuid(identifier);
		BatchResultContext<UploadRequestHistory> res = new BatchResultContext<UploadRequestHistory>(uploadRequestHistory);
		console.logDebug(batchRunContext, total, position,
				"Processing uploadRequestHistory : " + uploadRequestHistory.toString());
		UploadRequest uploadRequest = uploadRequestRepository.findByUuid(uploadRequestHistory.getUploadRequest().getUuid());
		if (uploadRequestHistory.getStatus().equals(UploadRequestStatus.CREATED)) {
			uploadRequest.setStatus(uploadRequestHistory.getStatus());
			UploadRequestMto requestMto = new UploadRequestMto(uploadRequest);
			UploadRequestAuditLogEntry log = new UploadRequestAuditLogEntry(requestMto.getOwner(),
					requestMto.getOwner(), LogAction.CREATE, AuditLogEntryType.UPLOAD_REQUEST, requestMto.getUuid(),
					requestMto);
			logEntryService.insert(log);
		} else if (uploadRequestHistory.isStatusUpdated()) {
			UploadRequestAuditLogEntry lastLog = auditUserMongoRepository.findTopByOrderByCreationDateDesc(uploadRequest.getUuid());
			UploadRequestMto last = lastLog.getResourceUpdated();
			if (last == null) {
				last = lastLog.getResource();
			}
			UploadRequestMto requestMto = new UploadRequestMto(uploadRequest);
			UploadRequestAuditLogEntry log = new UploadRequestAuditLogEntry(requestMto.getOwner(),
					requestMto.getOwner(), LogAction.UPDATE, AuditLogEntryType.UPLOAD_REQUEST, requestMto.getUuid(),
					requestMto);
			uploadRequest.setStatus(uploadRequestHistory.getStatus());
			log.setResourceUpdated(new UploadRequestMto(uploadRequest, true));
			logEntryService.insert(log);
		} else {
			uploadRequest.setActivationDate(uploadRequestHistory.getActivationDate());
			uploadRequest.setCanDelete(uploadRequestHistory.isCanDelete());
			uploadRequest.setExpiryDate(uploadRequestHistory.getExpiryDate());
			uploadRequest.setSecured(uploadRequestHistory.isSecured());
			uploadRequest.setCanClose(uploadRequestHistory.isCanClose());
			uploadRequest.setCanEditExpiryDate(uploadRequestHistory.isCanEditExpiryDate());
			uploadRequest.setNotificationDate(uploadRequestHistory.getNotificationDate());
			uploadRequest.setMaxDepositSize(uploadRequestHistory.getMaxDepositSize());
			uploadRequest.setMaxFileCount(uploadRequestHistory.getMaxFileCount());
			uploadRequest.setMaxFileSize(uploadRequestHistory.getMaxFileSize());
			uploadRequest.setLocale(uploadRequestHistory.getLocale());
			uploadRequest.setModificationDate(uploadRequestHistory.getModificationDate());
			uploadRequest.setMailMessageId(uploadRequestHistory.getMailMessageID());
			uploadRequest.setStatus(uploadRequestHistory.getStatus());
			UploadRequestMto requestMto = new UploadRequestMto(uploadRequest);
			UploadRequestAuditLogEntry lastLog = auditUserMongoRepository.findTopByOrderByCreationDateDesc(requestMto.getUuid());
			UploadRequestMto last = lastLog.getResourceUpdated();
			if (last == null) {
				last = lastLog.getResource();
			}
			UploadRequestAuditLogEntry log = new UploadRequestAuditLogEntry(requestMto.getOwner(),
					requestMto.getOwner(), LogAction.UPDATE, AuditLogEntryType.UPLOAD_REQUEST, requestMto.getUuid(),
					requestMto);
			log.setResource(last);
			log.setResourceUpdated(new UploadRequestMto(uploadRequest, true));
			logEntryService.insert(log);
		}
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadRequestHistory> res = (BatchResultContext<UploadRequestHistory>) context;
		UploadRequestHistory resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadRequestHistory> res = (BatchResultContext<UploadRequestHistory>) exception
				.getContext();
		UploadRequestHistory resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed.",
				batchRunContext);
		logger.error("Error occured while updating the UploadRequestHistory : " + resource, exception);
	}
}
