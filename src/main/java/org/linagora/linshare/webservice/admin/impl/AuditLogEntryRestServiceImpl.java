package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.entities.AuditLogEntry;
import org.linagora.linshare.mongo.repository.ShareAuditMongoRepository;
import org.linagora.linshare.webservice.admin.AuditLogEntryRestService;

@Path("/audit")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class AuditLogEntryRestServiceImpl implements AuditLogEntryRestService {

	ShareAuditMongoRepository repository;

	public AuditLogEntryRestServiceImpl(ShareAuditMongoRepository repository) {
		super();
		this.repository = repository;
	}

	@Path("/")
	@GET
	@Override
	public List<AuditLogEntry> findAll() {
		return repository.findAll();
	}

	@Path("/{action}")
	@GET
	@Override
	public List<AuditLogEntry> findByAction(@PathParam("action") String action) {
		return repository.findByAction(action);
	}

	@Path("/byDomain/{domainUuid}")
	@GET
	@Override
	public List<AuditLogEntry> findByDomainUuid(@PathParam("domainUuid") String domainUuid) {
		return repository.findByDomainUuid(domainUuid);
	}

	@Path("/byActor/{actorUuid}")
	@GET
	@Override
	public List<AuditLogEntry> findByActorUuid(@PathParam("actorUuid") String actorUuid) {
		return repository.findByActorUuid(actorUuid);
	}

	@Path("byType/{type}")
	@GET
	@Override
	public List<AuditLogEntry> findByType(@PathParam("type") String type) {
		return repository.findByType(AuditLogEntryType.fromString(type));
	}
}
