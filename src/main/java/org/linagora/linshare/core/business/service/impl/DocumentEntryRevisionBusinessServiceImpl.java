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
package org.linagora.linshare.core.business.service.impl;

import java.io.File;

import org.linagora.linshare.core.business.service.DocumentEntryRevisionBusinessService;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.service.TimeStampingService;
import org.linagora.linshare.core.service.impl.AbstractDocumentBusinessServiceImpl;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class DocumentEntryRevisionBusinessServiceImpl extends AbstractDocumentBusinessServiceImpl
		implements DocumentEntryRevisionBusinessService {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryRevisionBusinessServiceImpl.class);

	private final MongoTemplate mongoTemplate;

	private final WorkGroupNodeMongoRepository repository;

	public DocumentEntryRevisionBusinessServiceImpl(
			FileDataStore fileDataStore,
			TimeStampingService timeStampingService,
			DocumentRepository documentRepository,
			ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService,
			boolean deduplication,
			WorkGroupNodeMongoRepository repository,
			MongoTemplate mongoTemplate) {
		super(fileDataStore, timeStampingService, documentRepository,
				thumbnailGeneratorBusinessService, deduplication);
		this.repository = repository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public WorkGroupDocumentRevision createWorkGroupDocumentRevision(Account actor, WorkGroup workGroup, File myFile,
			Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType,
			WorkGroupNode parentNode) throws BusinessException {
		Document document = createDocument(workGroup, myFile, size, fileName, timeStampingUrl, mimeType);
		WorkGroupDocumentRevision node = new WorkGroupDocumentRevision(actor, fileName, document, workGroup, parentNode);
		setDocumentProperties(actor, node, fileName, parentNode, myFile, checkIfIsCiphered);
		node = repository.insert(node);
		return node;
	}

	@Override
	public WorkGroupNode findMostRecent(WorkGroup workGroup, String parentUuid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("workGroup").is(workGroup.getLsUuid()));
		query.addCriteria(Criteria.where("parent").is(parentUuid));
		query.addCriteria(Criteria.where("nodeType").is(WorkGroupNodeType.DOCUMENT_REVISION));
		query.with(Sort.by(Direction.DESC, "creationDate")).limit(1);
		return mongoTemplate.findOne(query, WorkGroupNode.class);
	}
}
