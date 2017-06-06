/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016-2017 LINAGORA
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

import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.DocumentBatchResultContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.ThreadEntryRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class Sha256SumUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final DocumentRepository documentRepository;
	private final FileDataStore fileDataStore;
	private final DocumentEntryBusinessService documentEntryBusinessService;
	private final ThreadEntryRepository threadEntryRepository;

	public Sha256SumUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			final DocumentRepository documentRepository,
			final FileDataStore fileDataStore,
			final ThreadEntryRepository threadEntryRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			final DocumentEntryBusinessService documentEntryBusinessService) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.documentRepository = documentRepository;
		this.fileDataStore = fileDataStore;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.threadEntryRepository = threadEntryRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_0_SHA256SUM;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> list = documentRepository.findAllSha256CheckNeededDocuments();
		return list;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Document doc = documentRepository.findByUuid(identifier);
		if (doc == null) {
			return null;
		}
		logInfo(batchRunContext, total, position, "processing document : " + doc.getUuid());
		ResultContext context = new DocumentBatchResultContext(doc);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, doc);
		try (InputStream fileContentByUUID = fileDataStore.get(metadata)) {
			String sha256sum = documentEntryBusinessService.SHA256CheckSumFileStream(fileContentByUUID);
			doc.setSha256sum(sha256sum);
			documentRepository.update(doc);
			for (DocumentEntry documentEntry : doc.getDocumentEntries()) {
				documentEntry.setSha256sum(sha256sum);
				documentEntryBusinessService.update(documentEntry);
			}
			for (ThreadEntry threadEntry : doc.getThreadEntries()) {
				threadEntry.setSha256sum(sha256sum);
				threadEntry.setModificationDate(new GregorianCalendar());
				threadEntryRepository.update(threadEntry);
			}
			context.setProcessed(true);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new BatchBusinessException(context, e.getMessage());
		};
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		DocumentBatchResultContext documentContext = (DocumentBatchResultContext) context;
		Document doc = documentContext.getResource();
		logInfo(batchRunContext, total, position, "The document : "
				+ doc.getUuid() +
				" has been successfully updated ");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		DocumentBatchResultContext documentContext = (DocumentBatchResultContext) exception.getContext();
		Document doc = documentContext.getResource();
		logError(total, position,
				"Updating document has failed : "
				+ doc.getUuid(), batchRunContext);
		logger.error("Error occured while updating the document : "
				+ doc.getUuid() +
				". BatchBusinessException", exception);
	}
}
