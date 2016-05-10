package org.linagora.linshare.mongo.repository;

import java.util.List;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.mongo.entities.AuditLogEntry;
import org.linagora.linshare.mongo.entities.ShareAuditLogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShareAuditMongoRepository extends MongoRepository<ShareAuditLogEntry, String>{

	List<AuditLogEntry> findByAction(String action);

	List<AuditLogEntry> findByDomainUuid(String domainUuid);

	List<AuditLogEntry> findByActorUuid(String actorUuid);

	List<AuditLogEntry> findByType(AuditLogEntryType type);
}
