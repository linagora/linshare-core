package org.linagora.linshare.webservice.admin;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AuditLogEntry;

public interface AuditLogEntryRestService {

	List<AuditLogEntry> findAll();

	List<AuditLogEntry> findByAction(String action);

	List<AuditLogEntry> findByDomainUuid(String domainUuid);

	List<AuditLogEntry> findByActorUuid(String actorUuid);

	List<AuditLogEntry> findByType(String type);
}
