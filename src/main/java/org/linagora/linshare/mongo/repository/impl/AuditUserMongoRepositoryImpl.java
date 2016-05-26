///*
// * LinShare is an open source filesharing software, part of the LinPKI software
// * suite, developed by Linagora.
// * 
// * Copyright (C) 2016 LINAGORA
// * 
// * This program is free software: you can redistribute it and/or modify it under
// * the terms of the GNU Affero General Public License as published by the Free
// * Software Foundation, either version 3 of the License, or (at your option) any
// * later version, provided you comply with the Additional Terms applicable for
// * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
// * Public License, subsections (b), (c), and (e), pursuant to which you must
// * notably (i) retain the display of the “LinShare™” trademark/logo at the top
// * of the interface window, the display of the “You are using the Open Source
// * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
// * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
// * e-mails sent with the Program, (ii) retain all hypertext links between
// * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
// * refrain from infringing Linagora intellectual property rights over its
// * trademarks and commercial brands. Other Additional Terms apply, see
// * <http://www.linagora.com/licenses/> for more details.
// * 
// * This program is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
// * details.
// * 
// * You should have received a copy of the GNU Affero General Public License and
// * its applicable Additional Terms for LinShare along with this program. If not,
// * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
// * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
// * applicable to LinShare software.
// */
//package org.linagora.linshare.mongo.repository.impl;
//
//import java.util.Date;
//import java.util.List;
//
//import javax.inject.Inject;
//
//import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
//import org.linagora.linshare.mongo.entities.AuditLogEntryUser;
//import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
//import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
//import org.springframework.data.repository.NoRepositoryBean;
//import org.springframework.stereotype.Repository;
//
//@NoRepositoryBean
//public class AuditUserMongoRepositoryImpl extends SimpleMongoRepository<AuditLogEntryUser, String>
//		implements AuditUserMongoRepository {
//
//	@Autowired
//	private MongoTemplate operations;
//
//	@Autowired
//	@Inject
//	public AuditUserMongoRepositoryImpl(MongoEntityInformation<AuditLogEntryUser, String> metadata,
//			MongoTemplate mongoOperations) {
//		super(metadata, mongoOperations);
//		this.operations = mongoOperations;
//	}
//
//	@Override
//	public List<AuditLogEntryUser> findByAction(String action) {
//		Query query = new Query();
//		query.addCriteria(Criteria.where("action").is(action));
//		return operations.find(query, AuditLogEntryUser.class);
//	}
//
//	@Override
//	public List<AuditLogEntryUser> findByOwnerUuid(String ownerUuid) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<AuditLogEntryUser> findByActorUuid(String actor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<AuditLogEntryUser> findByType(AuditLogEntryType type) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<AuditLogEntryUser> findByActorUuidAndAction(String actorUuid, String action) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<AuditLogEntryUser> findByActorUuidOrOwnerUuid(String actorUuid, String ownerUuid) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<AuditLogEntryUser> findForUser(String ownerUuid, String action, String type, boolean forceAll,
//			Date beginDate, Date endDate) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}
