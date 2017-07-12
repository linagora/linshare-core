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
package org.linagora.linshare.mongo.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AuditUserMongoRepository extends MongoRepository<AuditLogEntryUser, String> {

	List<AuditLogEntryUser> findByAction(String action);

	@Query("{ 'actor.uuid' : ?0 }")
	List<AuditLogEntryUser> findByActorUuid(String actor);

	@Query("{ 'authUser.uuid' : ?0 }")
	List<AuditLogEntryUser> findByAuthUserUuid(String authUser);

	List<AuditLogEntryUser> findByType(AuditLogEntryType type);

//	@Query("{'$or' : [ {'authUser.uuid' : ?0}, {'actor.uuid' : ?0} ], 'action' : {'$in' : ?1 }, 'type' : { '$in' : ?2 } , 'creationDate' : { '$gt' : '?3' , '$lt' : '?4'} }")
	@Query("{'relatedAccounts': {'$elemMatch' : { '$eq' : ?0 }}, 'action' : {'$in' : ?1 }, 'type' : { '$in' : ?2 } , 'creationDate' : { '$gt' : '?3' , '$lt' : '?4'} }")
	Set<AuditLogEntryUser> findForUser(String ownerUuid, List<LogAction> actions, List<AuditLogEntryType> types, Date beginDate,
			Date endDate);

	@Query("{ 'relatedAccounts': {'$elemMatch' : { '$eq' : ?0 }}, 'action' : {'$in' : ?1 }, 'type' : { '$in' : ?2 } }")
	Set<AuditLogEntryUser> findForUser(String ownerUuid, List<LogAction> actions, List<AuditLogEntryType> types);

	// workgroups.
	@Query("{  $or: [ {'resourceUuid' : ?0} , {'workGroup.uuid' : ?0} ], 'action' : {'$in' : ?1 }, 'type' : { '$in' : ?2 } , 'creationDate' : { '$gt' : '?3' , '$lt' : '?4'} }")
	Set<AuditLogEntryUser> findWorgGroupHistoryForUser(String workGroupUuid, List<LogAction> actions,
			List<AuditLogEntryType> types, Date beginDate, Date endDate, Sort sort);

	@Query("{ 'workGroup.uuid' : ?0, 'resourceUuid' : ?1, 'action' : {'$in' : ?2 }, 'type' : { '$in' : ?3 } , 'creationDate' : { '$gt' : '?4' , '$lt' : '?5'} }")
	Set<AuditLogEntryUser> findWorgGroupNodeHistoryForUser(String workGroupUuid, String workGroupNodeUuid,
			List<LogAction> actions, List<AuditLogEntryType> types, Date beginDate, Date endDate, Sort sort);

	@Query("{ 'relatedAccounts': {'$elemMatch' : { '$eq' : ?0 }}, 'action' : {'$in' : ?2 }, 'type' : { '$in' : ?3 } , $or: [ {'resourceUuid' : ?1} , { 'relatedResources': {'$elemMatch' : { '$eq' : ?1 }} } ] }")
	Set<AuditLogEntryUser> findDocumentHistoryForUser(String ownerUuid, String entryUuid,
			List<LogAction> actions, List<AuditLogEntryType> types, Sort sort);

	@Query("{ $or: [ {'resourceUuid' : ?0 } , { 'list.uuid' : ?0 } ], 'type' : { '$in' : ?1 } }")
	Set<AuditLogEntryUser> findContactListsActivity(String entryUuid,
			List<AuditLogEntryType> types, Sort sort);
}
