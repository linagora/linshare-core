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
package org.linagora.linshare.core.batches.impl;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.batches.utils.OperationKind;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.DocumentBatchResultContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UploadRequestEntryRepository;
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class DocumentGarbageCollectorBatchImpl extends GenericBatchImpl {

	protected DocumentRepository documentRepository;

	protected DocumentEntryRepository documentEntryRepository;

	protected WorkGroupNodeMongoRepository workGroupNodeMongoRepository;

	protected DocumentEntryBusinessService documentEntryBusinessService;

	protected UploadRequestEntryRepository uploadRequestEntryRepository;

	protected MongoTemplate mongoTemplate;

	protected DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository;

	public DocumentGarbageCollectorBatchImpl(
			AccountRepository<Account> accountRepository,
			DocumentRepository documentRepository,
			DocumentEntryRepository documentEntryRepository,
			UploadRequestEntryRepository uploadRequestEntryRepository,
			DocumentEntryBusinessService documentEntryBusinessService,
			MongoTemplate mongoTemplate,
			DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository,
			WorkGroupNodeMongoRepository workGroupNodeMongoRepository) {
		super(accountRepository);
		this.documentRepository = documentRepository;
		this.documentEntryRepository = documentEntryRepository;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.mongoTemplate = mongoTemplate;
		this.documentGarbageCollectorRepository = documentGarbageCollectorRepository;
		this.workGroupNodeMongoRepository = workGroupNodeMongoRepository;
		this.uploadRequestEntryRepository = uploadRequestEntryRepository;
		this.operationKind = OperationKind.DELETED;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		Query query = new Query();
		Calendar midnight = Calendar.getInstance();
		midnight.set(Calendar.HOUR_OF_DAY, 0);
		midnight.set(Calendar.MINUTE, 0);
		midnight.set(Calendar.SECOND, 0);
		logger.debug("midnight : " + midnight.getTime());
		query.addCriteria(Criteria.where("creationDate").lt(midnight.getTime()));
		query.fields().include("documentUuid");
		List<DocumentGarbageCollecteur> entries = mongoTemplate.find(query, DocumentGarbageCollecteur.class);
		logger.info(entries.size() + " document(s) have been found.");
		return Lists.transform(entries, new Function<DocumentGarbageCollecteur, String>() {
			@Override
			public String apply(DocumentGarbageCollecteur dgc) {
				return dgc.getId();
			}
		});
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		DocumentGarbageCollecteur dgc = mongoTemplate.findById(identifier, DocumentGarbageCollecteur.class);
		if (dgc == null) {
			return null;
		}
		Document document = documentRepository.findByUuid(dgc.getDocumentUuid());
		List<WorkGroupDocument> workgroupDocuments = workGroupNodeMongoRepository
				.findByDocumentUuid(dgc.getDocumentUuid());
		if (document == null && workgroupDocuments.isEmpty()) {
			// it does not exists anymore. skipped.
			documentGarbageCollectorRepository.delete(dgc);
			return null;
		}
		DocumentBatchResultContext context = new DocumentBatchResultContext(document);
		if (documentEntryRepository.getRelatedDocumentEntryCount(document) <= 0) {
			Query query = new Query();
			query.addCriteria((Criteria.where("documentUuid").is(document.getUuid())));
			if (mongoTemplate.count(query, WorkGroupDocumentRevision.class) <= 0) {
				// document is not referenced now, we need to remove it.
				if (uploadRequestEntryRepository.getRelatedUploadRequestEntryCount(document) <= 0) {
					context.setProcessed(true);
					documentEntryBusinessService.deleteDocument(document);
				}
			}
		}
		documentGarbageCollectorRepository.delete(dgc);
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		DocumentBatchResultContext ctx = (DocumentBatchResultContext) context;
		console.logInfo(batchRunContext, total, position,
				"Orphan document {} was removed.",
				ctx.getResource().getRepresentation());
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		DocumentBatchResultContext res = (DocumentBatchResultContext) exception.getContext();
		Document resource = res.getResource();
		console.logError(batchRunContext, total, position, exception.getMessage());
	}
}
