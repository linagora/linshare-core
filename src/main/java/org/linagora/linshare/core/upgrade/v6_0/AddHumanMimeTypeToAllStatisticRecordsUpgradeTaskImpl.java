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
package org.linagora.linshare.core.upgrade.v6_0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.batches.utils.FakeContext;
import org.linagora.linshare.core.domain.constants.AuditGroupLogEntryType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.utils.DocumentUtils;
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.collect.Lists;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

public class AddHumanMimeTypeToAllStatisticRecordsUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	public final HibernateTemplate hibernateTemplate;

	public AddHumanMimeTypeToAllStatisticRecordsUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			HibernateTemplate hibernateTemplate,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.hibernateTemplate = hibernateTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_6_0_ADD_HUMAN_MIME_TYPE_TO_EXISTING_STAT_RECORDS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return Arrays.asList("fakeUuid");
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		BatchResultContext<FakeContext> res = new BatchResultContext<>(new FakeContext(identifier));
		res.setProcessed(false);

		updateAllMimeTypeStatistics(batchRunContext, total, position);
		updateHumanMimeTypeStatistics(batchRunContext);

		updateAllWorkGroupNodes(batchRunContext, total, position);
		updateHumanWorkGroupNodes(batchRunContext);

		updateAllAuditLogEntries(batchRunContext, total, position);
		updateHumanAuditLogEntries(batchRunContext);
		updateAllAuditLogEntries2(batchRunContext, total, position);
		updateHumanAuditLogEntries2(batchRunContext);

		bulkUpdatesOfHumanMimeTypesForDocuments();
		bulkUpdatesOfHumanMimeTypesForDocumentEntries();
		bulkUpdatesOfHumanMimeTypesForUploadRequestEntries();

		res.setProcessed(true);
		return res;
	}

	private void updateHumanMimeTypeStatistics(BatchRunContext batchRunContext) {
		MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(MimeTypeStatistic.class));
		DistinctIterable<String> distincts = collection.distinct("mimeType", String.class);
		ArrayList<String> mimeTypes = Lists.newArrayList(distincts);
		int mimeTypeSize = mimeTypes.size();
		logger.debug("mimeType:size:" + mimeTypeSize);
		long position = 0;
		for (String mimeType : distincts) {
			logger.debug("mimeType:" + mimeType);
			String humanMimeType = DocumentUtils.getHumanMimeType(mimeType);
			if (!humanMimeType.equals("others")) {
				Query query = new Query();
				query.addCriteria(Criteria.where("mimeType").is(mimeType));
				long localTotal = mongoTemplate.count(query, MimeTypeStatistic.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "Nb elements for mimeType '" + mimeType + "' in MimeTypeStatistic found: " + localTotal);
				Update update = new Update();
				update.set("humanMimeType", humanMimeType);
				UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, MimeTypeStatistic.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "updateMulti: humanMimeType: " + updateMulti);
			}
			position++;
		}
	}

	private void updateAllMimeTypeStatistics(BatchRunContext batchRunContext, long total, long position) {
		Query query = new Query();
		query.addCriteria(Criteria.where("humanMimeType").exists(false));
		long localTotal = mongoTemplate.count(query, MimeTypeStatistic.class);
		console.logInfo(batchRunContext, total, position, "Missing humanMimeType in MimeTypeStatistic found: " + localTotal);
		Update update = new Update();
		update.set("humanMimeType", "others");
		update.set("totalSize", 0);
		UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, MimeTypeStatistic.class);
		console.logInfo(batchRunContext, total, position, "updateMulti: " + updateMulti);
	}

	private void updateAllWorkGroupNodes(BatchRunContext batchRunContext, long total, long position) {
		Query query = new Query();
		query.addCriteria(Criteria.where("humanMimeType").exists(false));
		long localTotal = mongoTemplate.count(query, WorkGroupNode.class);
		console.logInfo(batchRunContext, total, position, "Missing humanMimeType in WorkGroupNode found: " + localTotal);
		Update update = new Update();
		update.set("humanMimeType", "others");
		UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, WorkGroupNode.class);
		console.logInfo(batchRunContext, total, position, "updateMulti: " + updateMulti);
	}

	private void updateHumanWorkGroupNodes(BatchRunContext batchRunContext) {
		MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(WorkGroupNode.class));
		DistinctIterable<String> distincts = collection.distinct("mimeType", String.class);
		ArrayList<String> mimeTypes = Lists.newArrayList(distincts);
		int mimeTypeSize = mimeTypes.size();
		logger.debug("mimeType:size:" + mimeTypeSize);
		long position = 0;
		for (String mimeType : distincts) {
			logger.debug("mimeType:" + mimeType);
			String humanMimeType = DocumentUtils.getHumanMimeType(mimeType);
			if (!humanMimeType.equals("others")) {
				Query query = new Query();
				query.addCriteria(Criteria.where("mimeType").is(mimeType));
				long localTotal = mongoTemplate.count(query, MimeTypeStatistic.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "Nb elements for mimeType '" + mimeType + "' in WorkGroupNode found: " + localTotal);
				Update update = new Update();
				update.set("humanMimeType", humanMimeType);
				UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, WorkGroupNode.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "updateMulti: humanMimeType: " + updateMulti);
			}
			position++;
		}
	}

	private void updateAllAuditLogEntries(BatchRunContext batchRunContext, long total, long position) {
		Query query = new Query();
		query.addCriteria(Criteria.where("resource.humanMimeType").exists(false));
		query.addCriteria(Criteria.where("resource.mimeType").exists(true));
		long localTotal = mongoTemplate.count(query, AuditLogEntry.class);
		console.logInfo(batchRunContext, total, position, "Missing humanMimeType in AuditLogEntry found: " + localTotal);
		Update update = new Update();
		update.set("resource.humanMimeType", "others");
		UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, AuditLogEntry.class);
		console.logInfo(batchRunContext, total, position, "updateMulti: " + updateMulti);
	}

	private void updateHumanAuditLogEntries(BatchRunContext batchRunContext) {
		MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(AuditLogEntry.class));
		DistinctIterable<String> distincts = collection.distinct("resource.mimeType", String.class);
		ArrayList<String> mimeTypes = Lists.newArrayList(distincts);
		int mimeTypeSize = mimeTypes.size();
		logger.debug("resource.mimeType:size:" + mimeTypeSize);
		long position = 0;
		for (String mimeType : distincts) {
			logger.debug("resource.mimeType:" + mimeType);
			String humanMimeType = DocumentUtils.getHumanMimeType(mimeType);
			if (!humanMimeType.equals("others")) {
				Query query = new Query();
				query.addCriteria(Criteria.where("resource.mimeType").is(mimeType));
				long localTotal = mongoTemplate.count(query, AuditLogEntry.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "Nb elements for resource.mimeType '" + mimeType + "' in AuditLogEntry found: " + localTotal);
				Update update = new Update();
				update.set("resource.humanMimeType", humanMimeType);
				UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, AuditLogEntry.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "updateMulti: resource.humanMimeType: " + updateMulti);
			}
			position++;
		}
	}

	private void updateAllAuditLogEntries2(BatchRunContext batchRunContext, long total, long position) {
		Query query = new Query();
		query.addCriteria(Criteria.where("resource.humanMimeType").exists(false));
		query.addCriteria(Criteria.where("resource.type").exists(true));
		// just to be sure that we are going to add the field to the proper resources.
		query.addCriteria(
			Criteria.where("type").in(
				AuditGroupLogEntryType.toAuditLogEntryTypes.get(AuditGroupLogEntryType.MY_SPACE)));
		long localTotal = mongoTemplate.count(query, AuditLogEntry.class);
		console.logInfo(batchRunContext, total, position, "Missing humanMimeType in AuditLogEntry found: " + localTotal);
		Update update = new Update();
		update.set("resource.humanMimeType", "others");
		UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, AuditLogEntry.class);
		console.logInfo(batchRunContext, total, position, "updateMulti: " + updateMulti);
	}

	private void updateHumanAuditLogEntries2(BatchRunContext batchRunContext) {
		MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(AuditLogEntry.class));
		DistinctIterable<String> distincts = collection.distinct("resource.type", String.class);
		ArrayList<String> mimeTypes = Lists.newArrayList(distincts);
		int mimeTypeSize = mimeTypes.size();
		logger.debug("resource.type:size:" + mimeTypeSize);
		long position = 0;
		for (String mimeType : distincts) {
			logger.debug("resource.mimeType:" + mimeType);
			String humanMimeType = DocumentUtils.getHumanMimeType(mimeType);
			if (!humanMimeType.equals("others")) {
				Query query = new Query();
				query.addCriteria(Criteria.where("resource.type").is(mimeType));
				// just to be sure that we are going to add the field to the proper resources.
				query.addCriteria(
					Criteria.where("type").in(
						AuditGroupLogEntryType.toAuditLogEntryTypes.get(AuditGroupLogEntryType.MY_SPACE)));
				long localTotal = mongoTemplate.count(query, AuditLogEntry.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "Nb elements for resource.type '" + mimeType + "' in AuditLogEntry found: " + localTotal);
				Update update = new Update();
				update.set("resource.humanMimeType", humanMimeType);
				UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, AuditLogEntry.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "updateMulti: resource.humanMimeType: " + updateMulti);
			}
			position++;
		}
	}

	public List<String> getMimeTypes(Class<?> clazz) {
		DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
		criteria.setProjection(Projections.distinct(Projections.property("type")));
		@SuppressWarnings("unchecked")
		List<String> mimeTypes = (List<String>) hibernateTemplate.findByCriteria(criteria);
		return mimeTypes;
	}

	public void bulkUpdatesOfHumanMimeTypesForDocuments() {
		Class<Document> clazz = Document.class;
		Session currentSession = hibernateTemplate.getSessionFactory().getCurrentSession();
		for (String mimeType : getMimeTypes(clazz)) {
			logger.debug("mimeType:" + mimeType);
			String humanMimeType = DocumentUtils.getHumanMimeType(mimeType);
			logger.debug("humanMimeType:" + humanMimeType);
			if (!humanMimeType.equals("others")) {
				CriteriaBuilder cb = currentSession.getCriteriaBuilder();
				CriteriaUpdate<Document> criteriaUpdate = cb.createCriteriaUpdate(clazz);
				Root<Document> root = criteriaUpdate.from(clazz);
				criteriaUpdate.set("humanMimeType", humanMimeType);
				criteriaUpdate.where(cb.equal(root.get("type"), mimeType));
				int executeUpdate = currentSession.createQuery(criteriaUpdate).executeUpdate();
				logger.debug("executeUpdate:" + executeUpdate);
			}
		}
	}

	public void bulkUpdatesOfHumanMimeTypesForDocumentEntries() {
		Class<DocumentEntry> clazz = DocumentEntry.class;
		Session currentSession = hibernateTemplate.getSessionFactory().getCurrentSession();
		for (String mimeType : getMimeTypes(clazz)) {
			logger.debug("mimeType:" + mimeType);
			String humanMimeType = DocumentUtils.getHumanMimeType(mimeType);
			logger.debug("humanMimeType:" + humanMimeType);
			if (!humanMimeType.equals("others")) {
				CriteriaBuilder cb = currentSession.getCriteriaBuilder();
				CriteriaUpdate<DocumentEntry> criteriaUpdate = cb.createCriteriaUpdate(clazz);
				Root<DocumentEntry> root = criteriaUpdate.from(clazz);
				criteriaUpdate.set("humanMimeType", humanMimeType);
				criteriaUpdate.where(cb.equal(root.get("type"), mimeType));
				int executeUpdate = currentSession.createQuery(criteriaUpdate).executeUpdate();
				logger.debug("executeUpdate:" + executeUpdate);
			}
		}
	}

	public void bulkUpdatesOfHumanMimeTypesForUploadRequestEntries() {
		Class<UploadRequestEntry> clazz = UploadRequestEntry.class;
		Session currentSession = hibernateTemplate.getSessionFactory().getCurrentSession();
		for (String mimeType : getMimeTypes(clazz)) {
			logger.debug("mimeType:" + mimeType);
			String humanMimeType = DocumentUtils.getHumanMimeType(mimeType);
			logger.debug("humanMimeType:" + humanMimeType);
			if (!humanMimeType.equals("others")) {
				CriteriaBuilder cb = currentSession.getCriteriaBuilder();
				CriteriaUpdate<UploadRequestEntry> criteriaUpdate = cb.createCriteriaUpdate(clazz);
				Root<UploadRequestEntry> root = criteriaUpdate.from(clazz);
				criteriaUpdate.set("humanMimeType", humanMimeType);
				criteriaUpdate.where(cb.equal(root.get("type"), mimeType));
				int executeUpdate = currentSession.createQuery(criteriaUpdate).executeUpdate();
				logger.debug("executeUpdate:" + executeUpdate);
			}
		}
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<FakeContext> res = (BatchResultContext<FakeContext>) context;
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "All Statistics were updated with statisticDate information.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while adding statisticDate information to all Statistics", exception);
	}
}
