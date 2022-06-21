/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

	@Query("{ 'action' : {'$in' : ?0 }, 'type' : { '$in' : ?1 } }, 'relatedDomains' : { '$in' : ?2 } }")
	Set<AuditLogEntry> findAll(List<LogAction> actions, List<AuditLogEntryType> types,  List<String> domains);

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
