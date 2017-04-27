/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.facade.webservice.admin.AuditLogEntryFacade;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.admin.AuditLogEntryAdminRestService;

@Path("/audit")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class AuditLogEntryAdminRestServiceImpl implements AuditLogEntryAdminRestService {

	private AuditLogEntryFacade auditLogFacade;

	public AuditLogEntryAdminRestServiceImpl(AuditLogEntryFacade facade) {
		super();
		this.auditLogFacade = facade;
	}

	@Path("/")
	@GET
	@Override
	public List<AuditLogEntryAdmin> findAll() {
		return auditLogFacade.findAll();
	}

	@Path("/{action}")
	@GET
	@Override
	public List<AuditLogEntryAdmin> findByAction(@PathParam("action") String action) {
		return auditLogFacade.findByAction(action);
	}

	@Path("/byDomain/{domainUuid}")
	@GET
	@Override
	public List<AuditLogEntryAdmin> findByDomainUuid(@PathParam("domainUuid") String domainUuid) {
		return auditLogFacade.findByDomain(domainUuid);
	}

	@Path("byType/{type}")
	@GET
	@Override
	public List<AuditLogEntryAdmin> findByType(@PathParam("type") String type) {
		return auditLogFacade.findByType(AuditLogEntryType.fromString(type));
	}

	@Path("/user")
	@GET
	@Override
	public List<AuditLogEntryUser> userFindAll() {
		return auditLogFacade.userFindAll();
	}

	@Path("/user/{action}")
	@GET
	@Override
	public List<AuditLogEntryUser> userFindByAction(@PathParam("action") String action) {
		return auditLogFacade.userFindByAction(action);
	}

	@Path("/user/byType/{type}")
	@GET
	@Override
	public List<AuditLogEntryUser> userFindByType(@PathParam("type") String type) {
		return auditLogFacade.userFindByType(AuditLogEntryType.fromString(type));
	}

}