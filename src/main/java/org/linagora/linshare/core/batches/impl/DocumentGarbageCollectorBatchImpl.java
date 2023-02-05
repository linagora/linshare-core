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
