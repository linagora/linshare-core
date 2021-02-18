/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.upgrade.v2_1;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class DocumentGarbageCollectorUpgradeTaskImpl extends GenericUpgradeTaskImpl{

	protected DocumentRepository documentRepository;

	protected DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository;

	public DocumentGarbageCollectorUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			DocumentRepository documentRepository,
			DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.documentRepository = documentRepository;
		this.documentGarbageCollectorRepository = documentGarbageCollectorRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_1_DOCUMENT_GARBAGE_COLLECTOR;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job is starting ...");
		List<String> entries = documentRepository.findAllIdentifiers();
		logger.info(entries.size() + " document(s) have been found.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		DocumentGarbageCollecteur documentGarbageCollector = new DocumentGarbageCollecteur(identifier);
		BatchResultContext<DocumentGarbageCollecteur> res = new BatchResultContext<DocumentGarbageCollecteur>(documentGarbageCollector);
		console.logDebug(batchRunContext, total, position, "Processing documentGarbageCollector : " + documentGarbageCollector.toString());
		documentGarbageCollectorRepository.insert(documentGarbageCollector);
		console.logInfo(batchRunContext, "Document added successfully", identifier);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<DocumentGarbageCollecteur> res = (BatchResultContext<DocumentGarbageCollecteur>) context;
		DocumentGarbageCollecteur resource = res.getResource();
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
		BatchResultContext<String> res = (BatchResultContext<String>) exception.getContext();
		String resource = res.getResource();
		logError(total, position, "The upgrade task : " + resource + " failed.", batchRunContext);
		logger.error("Error occured while updating the document : "
				+ resource +
				". BatchBusinessException", exception);
	}

}
