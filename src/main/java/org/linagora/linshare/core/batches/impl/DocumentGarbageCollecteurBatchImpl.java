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
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollecteurMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class DocumentGarbageCollecteurBatchImpl extends GenericBatchImpl {

	protected DocumentRepository documentRepository;

	protected DocumentEntryRepository documentEntryRepository;

	protected WorkGroupNodeMongoRepository workGroupNodeMongoRepository;

	protected DocumentEntryBusinessService documentEntryBusinessService;

	protected MongoTemplate mongoTemplate;

	protected DocumentGarbageCollecteurMongoRepository documentGarbageCollecteur;

	public DocumentGarbageCollecteurBatchImpl(
			AccountRepository<Account> accountRepository,
			DocumentRepository documentRepository,
			DocumentEntryRepository documentEntryRepository,
			DocumentEntryBusinessService documentEntryBusinessService,
			MongoTemplate mongoTemplate,
			DocumentGarbageCollecteurMongoRepository documentGarbageCollecteur) {
		super(accountRepository);
		this.documentRepository = documentRepository;
		this.documentEntryRepository = documentEntryRepository;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.mongoTemplate = mongoTemplate;
		this.documentGarbageCollecteur = documentGarbageCollecteur;
		this.operationKind = OperationKind.DELETED;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		Query query = new Query();
		Calendar midnight = Calendar.getInstance();
		midnight.set(Calendar.HOUR, 0);
		midnight.set(Calendar.MINUTE, 0);
		midnight.set(Calendar.SECOND, 0);
		midnight.add(Calendar.DAY_OF_MONTH, 1);
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
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position) throws BatchBusinessException, BusinessException {
		DocumentGarbageCollecteur dgc = documentGarbageCollecteur.findOne(identifier);
		if (dgc == null) {
			return null;
		}
		Document document = documentRepository.findByUuid(dgc.getDocumentUuid());
		if (document == null) {
			// it does not exists anymore. skipped.
			documentGarbageCollecteur.delete(dgc);
			return null;
		}
		DocumentBatchResultContext context = new DocumentBatchResultContext(document);
		if (documentEntryRepository.getRelatedDocumentEntryCount(document) <= 0) {
			Query query = new Query();
			query.addCriteria((Criteria.where("documentUuid").is(document.getUuid())));
			if (mongoTemplate.count(query, WorkGroupDocument.class) <= 0) {
				// document is not referenced now, we need to remove it.
				context.setProcessed(true);
				documentEntryBusinessService.deleteDocument(document);
			}
		}
		documentGarbageCollecteur.delete(dgc);
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
