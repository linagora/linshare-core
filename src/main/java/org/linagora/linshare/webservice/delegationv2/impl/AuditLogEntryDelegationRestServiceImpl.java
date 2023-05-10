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
package org.linagora.linshare.webservice.delegationv2.impl;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.facade.webservice.delegation.AuditLogEntryUserFacade;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.delegationv2.AuditLogEntryDelegationRestService;


@Path("/audit")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class AuditLogEntryDelegationRestServiceImpl implements AuditLogEntryDelegationRestService {

	private final AuditLogEntryUserFacade auditFacade;

	public AuditLogEntryDelegationRestServiceImpl(final AuditLogEntryUserFacade auditFacade) {
		this.auditFacade = auditFacade;
	}

	@Path("/{actorUuid}")
	@GET
	@Override
	public Set<AuditLogEntryUser> findAll(@PathParam("actorUuid") String actorUuid,
			@QueryParam("action") List<LogAction> action,
			@QueryParam("type") List<AuditLogEntryType> type,
			@QueryParam("forceAll") @DefaultValue("false") boolean forceAll,
			@QueryParam("beginDate") String beginDate,
			@QueryParam("endDate") String endDate) {
		return auditFacade.findAll(actorUuid, action, type, forceAll, beginDate, endDate);
	}
}
