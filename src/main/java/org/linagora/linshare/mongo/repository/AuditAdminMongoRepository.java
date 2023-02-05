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
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AuditAdminMongoRepository extends MongoRepository<AuditLogEntry, String> {

	List<AuditLogEntry> findByAction(String action);

	@Query("{ 'actor.uuid' : ?0 }")
	List<AuditLogEntry> findByActor(String actor);

	List<AuditLogEntry> findByType(AuditLogEntryType type);

	@Query("{'action' : {'$in' : ?0 }, 'type' : { '$in' : ?1 } , 'creationDate' : { '$gt' : ?2 , '$lt' : ?3}, 'relatedDomains' : { '$in' : ?4 } }")
	Set<AuditLogEntry> findAll(List<LogAction> actions, List<AuditLogEntryType> types, Date beginDate,
		Date endDate, List<String> domains);

	@Query("{'action' : {'$in' : ?0 }, 'type' : { '$in' : ?1 } , 'creationDate' : { '$gt' : ?2 , '$lt' : ?3} }")
	Set<AuditLogEntry> findAll(List<LogAction> actions, List<AuditLogEntryType> types, Date beginDate,
		Date endDate);

	@Query("{ 'action' : {'$in' : ?0 }, 'type' : { '$in' : ?1 } } }")
	Set<AuditLogEntry> findAll(List<LogAction> actions, List<AuditLogEntryType> types);

	@Query("{ 'authUser.domain.uuid' : ?0, 'creationDate' : { '$lt' : ?1}}")
	List<AuditLogEntry> findAllBeforeDateByDomainUuid(String domainUuid, Date date);

	@Query("{ 'creationDate' : { '$gt' : ?0 , '$lt' : ?1} }")
	List<AuditLogEntry> findAllBetweenTwoDates(Date beginDate, Date endDate);

	@Query("{ 'resource.domainUuid' : ?0, 'action' : {'$in' : ?1 }, 'type' : ?2 }")
	Set<AuditLogEntryAdmin> findAll(String domainUuid, List<LogAction> action, AuditLogEntryType type,
			Sort sort);

	@Query("{ 'resource.uuid' : ?0, 'action' : {'$in' : ?1 }, 'type' : ?2 }")
	Set<MailAttachmentAuditLogEntry> findAllAudits(String uuid, List<LogAction> actions,
			AuditLogEntryType mailAttachment, Sort sort);

	@Query("{'targetDomainUuid' : {'$in' : ?0 }, 'action' : {'$in' : ?1 }, 'type' : ?2 }")
	Set<MailAttachmentAuditLogEntry> findAllAuditsByDomain(List<String> domains, List<LogAction> actions,
			AuditLogEntryType mailAttachment, Sort sort);

	MailAttachmentAuditLogEntry findByUuid (String uuid);

	@Query("{ 'action' : {'$in' : ?0 }, 'type' : ?1 }")
	Set<MailAttachmentAuditLogEntry> findAllAuditsByRoot(List<LogAction> actions, AuditLogEntryType mailAttachment,
			Sort sort);
}
