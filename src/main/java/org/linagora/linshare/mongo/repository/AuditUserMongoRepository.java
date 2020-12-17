/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.mongo.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.UploadRequestAuditLogEntry;
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

//	@Query("{'$or' : [ {'authUser.uuid' : ?0}, {'actor.uuid' : ?0} ], 'action' : {'$in' : ?1 }, 'type' : { '$in' : ?2 } , 'creationDate' : { '$gt' : ?3 , '$lt' : ?4} }")
	@Query("{'relatedAccounts': {'$elemMatch' : { '$eq' : ?0 }}, 'action' : {'$in' : ?1 }, 'type' : { '$in' : ?2 } , 'creationDate' : { '$gt' : ?3 , '$lt' : ?4} }")
	Set<AuditLogEntryUser> findForUser(String ownerUuid, List<LogAction> actions, List<AuditLogEntryType> types, Date beginDate,
			Date endDate);

	@Query("{ 'relatedAccounts': {'$elemMatch' : { '$eq' : ?0 }}, 'action' : {'$in' : ?1 }, 'type' : { '$in' : ?2 } }")
	Set<AuditLogEntryUser> findForUser(String ownerUuid, List<LogAction> actions, List<AuditLogEntryType> types);

	// workgroups.
	@Query("{  $or: [ {'resourceUuid' : ?0} , {'workGroup.uuid' : ?0} ], 'action' : {'$in' : ?1 }, 'type' : { '$in' : ?2 } , 'creationDate' : { '$gt' : ?3 , '$lt' : ?4} }")
	Set<AuditLogEntryUser> findWorkGroupHistoryForUser(String workGroupUuid, List<LogAction> actions,
			List<AuditLogEntryType> types, Date beginDate, Date endDate, Sort sort);

	@Query("{ 'workGroup.uuid' : ?0, $or: [ {'resourceUuid' : ?1} , { 'relatedResources': {'$elemMatch' : { '$eq' : ?1 }} } ] , 'action' : {'$in' : ?2 }, 'type' : { '$in' : ?3 } , 'creationDate' : { '$gt' : ?4 , '$lt' : ?5} }")
	Set<AuditLogEntryUser> findWorkGroupNodeHistoryForUser(String workGroupUuid, String workGroupNodeUuid,
			List<LogAction> actions, List<AuditLogEntryType> types, Date beginDate, Date endDate, Sort sort);

	@Query("{ 'relatedAccounts': {'$elemMatch' : { '$eq' : ?0 }}, 'action' : {'$in' : ?2 }, 'type' : { '$in' : ?3 } , $or: [ {'resourceUuid' : ?1} , { 'relatedResources': {'$elemMatch' : { '$eq' : ?1 }} } ] }")
	Set<AuditLogEntryUser> findDocumentHistoryForUser(String ownerUuid, String entryUuid,
			List<LogAction> actions, List<AuditLogEntryType> types, Sort sort);

	@Query("{ $or: [ {'resourceUuid' : ?0 } , { 'list.uuid' : ?0 } ], 'type' : { '$in' : ?1 } }")
	Set<AuditLogEntryUser> findContactListsActivity(String entryUuid,
			List<AuditLogEntryType> types, Sort sort);

	// UploadRequest
	@Query("{ 'actor.uuid' : ?0, $or: [ {'resourceUuid' :  ?1 } , {'resource.uploadRequestGroupUuid' : ?1}], 'action' : {'$in' : ?2 }, 'type' : { '$in' : ?3 } }")
	Set<AuditLogEntryUser> findUploadRequestHistoryForUser(String ownerUuid, String requestUuid, List<LogAction> actions, List<AuditLogEntryType> types, Sort sort);

	UploadRequestAuditLogEntry findTopByOrderByCreationDateDesc(String resourceUuid);

	// jwt LongTime
	@Query("{ $or: [ {'resource.actorUuid' : ?0 } , {'resource.domainUuid' : ?1}], 'action' : { '$in' : ?2 }, 'type' :  ?3 }")
	Set<AuditLogEntryUser> findAll(String actorUuid, String domainUuid, List<LogAction> action, AuditLogEntryType type, Sort sort);
	
	// Get audit traces of the given upload Request and related upload requests urls and upload request entries
	// with ability to filter by actions (CREATE, UPDATE, ...) and types (UPLOAD_REQUEST, UPLOAD_REQUEST_URL, UPLOAD_REQUEST_ENRTY)
	@Query("{'actor.uuid' : ?0, $or: [ {'resourceUuid' : ?1} , { 'relatedResources': {'$elemMatch' : { '$eq' : ?1 }} } ], 'action' : {'$in' : ?2},'type' :  {'$in' : ?3}} ")
	Set<AuditLogEntryUser> findAllUploadRequestAuditTraces(String actorUuid, String uploadRequestUuid,
			List<LogAction> action, List<AuditLogEntryType> types, Sort sort);

}
