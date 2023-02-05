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

	AuditLogEntryUser findByUuid(String uuid);

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

	// shared spaces.
	/**
	 * Returns traces of the Shared space (WorkSpace/nested workgroup/ workgroup) and the related resource traces (members/folders/documents/revisions) that belongs to this shared space.
	 * All operations performed by members on a given resource of the shared space are included.
	 * @param sharedSpaceUuid String uuid of the shared space. Could be a WorkSpace, nested workgroup or workgroup.
	 * @param actions {@link List} of {@link LogAction} list of audit action types. Should not be null neither empty.
	 * @param types {@link List} of {@link AuditLogEntryType} list of audit entry types. Should not be null neither empty.
	 * @param beginDate {@link Date} Begin of the date range.
	 * @param endDate {@link Date} End of the date range.
	 * @param sort {@link Sort} contains direction and sort field used to sort resulting data.
	 * @return {@link Set} of {@link AuditLogEntryUser}
	 */
	@Query("{  $or: [ {'resourceUuid' : ?0} , { 'relatedResources': {'$elemMatch' : { '$eq' : ?0 }} } ], 'action' : {'$in' : ?1 }, 'type' : { '$in' : ?2 } 'creationDate' : { '$gt' : ?3 , '$lt' : ?4} }")
	Set<AuditLogEntryUser> findAllSharedSpaceAuditsForUser(String sharedSpaceUuid, List<LogAction> actions,
			 List<AuditLogEntryType> types, Date beginDate, Date endDate, Sort sort);
	/**
	 * Returns traces of a given Workgroup node (folder/document/revision) and all traces of related resources (folders/documents/revisions) that belongs to a shared space with uuid sharedSpaceUuid
	 * All operations performed by shared space members on a given workgroup node and its related resources are included.
	 * @param String sharedSpaceUuid uuid of the shared space. Could be a WorkSpace, nested workgroup or workgroup.
	 * @param String workGroupNodeUuid uuid of workgroup node.
	 * @param actions {@link List} of {@link LogAction} list of audit action types. Should not be null neither empty.
	 * @param types {@link List} of {@link AuditLogEntryType} list of audit entry types. Should not be null neither empty.
	 * @param beginDate {@link Date} Begin of the date range.
	 * @param endDate {@link Date} End of the date range.
	 * @param sort {@link Sort} contains direction and sort field used to sort resulting data.
	 * @return {@link Set} of {@link AuditLogEntryUser}
	 */
	@Query("{ 'relatedResources': {'$elemMatch' : { '$eq' : ?0 }}  , $or: [ {'resourceUuid' : ?1} , { 'relatedResources': {'$elemMatch' : { '$eq' : ?1 }} } ] , 'action' : {'$in' : ?2 }, 'type' : { '$in' : ?3 } , 'creationDate' : { '$gt' : ?4 , '$lt' : ?5} }")
	Set<AuditLogEntryUser> findWorkGroupNodeHistoryForUser(String sharedSpaceUuid, String workGroupNodeUuid,
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
	
	/**
	 * Get audit traces of a given upload request entry with ability to filter by actions
	 * @param actorUuid 
	 * @param uploadRequestEntryUuid
	 * @param actions
	 * @param sort by default with creation Date in DESC order
	 * @return a Set of AuditLogEntryUser
	 */
	@Query("{'actor.uuid' : ?0 , 'resourceUuid' : ?1 , 'action' : {'$in' : ?2}}")
	Set<AuditLogEntryUser> findAllUploadRequestEntryAuditTraces(String actorUuid, String uploadRequestEntryUuid,
			List<LogAction> actions, Sort sort);

	@Query("{$or: [ {'actor.uuid' : ?0 } , { 'relatedAccounts': {'$elemMatch' : { '$eq' : ?0 }} } ], 'resourceUuid' : ?1 , 'action' : {'$in' : ?2},'type' :  {'$in' : ?3}} ")
	Set<AuditLogEntryUser> findAllModeratorTraces(String actorUuid, String moderatorUuid,
			List<LogAction> createUpdateDeletetActions, List<AuditLogEntryType> entryTypes, Sort by);

}
