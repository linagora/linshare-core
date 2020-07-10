/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2020 LINAGORA
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
package org.linagora.linshare.core.upgrade.v2_3;

import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;

public class UpgradeDocumentForVersionningImpl extends GenericUpgradeTaskImpl {

	private WorkGroupNodeMongoRepository nodeRepository;

	private MongoTemplate mongoTemplate;

	public UpgradeDocumentForVersionningImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			WorkGroupNodeMongoRepository nodeRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.nodeRepository = nodeRepository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_3_UPDATE_DOCUMENT_STRUCTURE_FOR_VERSIONING;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return findAllIdentifiers();
	}

	private List<String> findAllIdentifiers() {
		Query query = new Query();
		query.addCriteria(Criteria.where("nodeType").is("DOCUMENT"));
		query.addCriteria(Criteria.where("documentUuid").ne(null));
		MongoCollection<Document> node = mongoTemplate.getCollection("work_group_nodes");
		DistinctIterable<String> results = node.distinct("uuid", query.getQueryObject(), String.class);
		return Lists.newArrayList(results);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		WorkGroupDocument wkDocument = (WorkGroupDocument) nodeRepository.findByUuid(identifier);
		BatchResultContext<WorkGroupDocument> res = new BatchResultContext<WorkGroupDocument>(wkDocument);

		WorkGroupDocumentRevision revision = new WorkGroupDocumentRevision();
		revision.setUuid(UUID.randomUUID().toString());
		revision.setName(wkDocument.getName());
		revision.setParent(wkDocument.getUuid());
		revision.setWorkGroup(wkDocument.getWorkGroup());
		revision.setPath(null);
		revision.setSize(wkDocument.getSize());
		revision.setMimeType(wkDocument.getMimeType());
		revision.setDocumentUuid(wkDocument.getDocumentUuid());
		revision.setSha256sum(wkDocument.getSha256sum());
		revision.setHasThumbnail(wkDocument.getHasThumbnail());
		revision.setNodeType(WorkGroupNodeType.DOCUMENT_REVISION);
		revision.setHasRevision(false);
		revision.setLastRevision(0L);
		revision.setCreationDate(wkDocument.getCreationDate());
		revision.setModificationDate(wkDocument.getModificationDate());
		revision.setUploadDate(wkDocument.getUploadDate());
		revision.setPathFromParent(wkDocument);
		revision.setCiphered(wkDocument.getCiphered());
		revision.setLastAuthor(wkDocument.getLastAuthor());

		revision = nodeRepository.insert(revision);
		wkDocument.setDocumentUuid(null);
		nodeRepository.save(wkDocument);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroupDocument> res = (BatchResultContext<WorkGroupDocument>) context;
		WorkGroupDocument resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroupDocument> res = (BatchResultContext<WorkGroupDocument>) exception
				.getContext();
		WorkGroupDocument resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed.",
				batchRunContext);
		logger.error("Error occured while updating the document structure for versioning : " + resource
				+ ". BatchBusinessException", exception);
	}

}
