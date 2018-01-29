/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;

public class ComputeThumbnailBatchImpl extends GenericBatchImpl {

	protected final DocumentRepository documentRepository;

	protected final FileDataStore fileDataStore;

	protected final DocumentEntryBusinessService documentEntryBusinessService;

	protected final ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService;

	public ComputeThumbnailBatchImpl(AccountRepository<Account> accountRepository,
			DocumentRepository documentRepository, FileDataStore fileDataStore,
			DocumentEntryBusinessService documentEntryBusinessService,
			ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService) {
		super(accountRepository);
		this.documentRepository = documentRepository;
		this.fileDataStore = fileDataStore;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.thumbnailGeneratorBusinessService = thumbnailGeneratorBusinessService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = documentRepository.findAllDocumentWithComputeThumbnailEnabled();
		logger.info(
				entries.size() + " document(s) have been found, compute thumbnail will be check again for each file.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Document document = documentRepository.findByUuid(identifier);
		if (document == null) {
			return null;
		}
		console.logInfo(batchRunContext, total, position, "processing document : " + document.getRepresentation());
		ResultContext context = new BatchResultContext<Document>(document);
		try {
			documentEntryBusinessService.updateThumbnail(document, getSystemAccount());
			console.logInfo(batchRunContext, "document updated ", document.getRepresentation());
			context.setIdentifier(identifier);
			context.setProcessed(true);
		} catch (BusinessException e){
			logger.debug("failed to generate the thumbnails" + document.getRepresentation(), e);
			console.logError(batchRunContext, total, position,
					"Error while trying to update the thumbnail of the document ");
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to update the thumbnails");
			context.setProcessed(false);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Document> cc = (BatchResultContext<Document>) context;
		if (cc.getProcessed()) {
			Document entry = cc.getResource();
			console.logInfo(batchRunContext, total, position, "Document Thumbnail was updated {}.", entry.getRepresentation());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Document> context = (BatchResultContext<Document>) exception.getContext();
		Document entry = context.getResource();
		console.logError(batchRunContext, total, position, "Document Thumbnail was not updated, batch has failed : {}",
				entry.getRepresentation(), exception);
	}
}
