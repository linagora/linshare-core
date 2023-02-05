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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainQuotaFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainQuotaDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.DomainQuotaRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Produces({MediaType.APPLICATION_JSON })
@Consumes({MediaType.APPLICATION_JSON })
@Path("/domains/{domainUuid}/domain_quotas")
public class DomainQuotaRestServiceImpl extends WebserviceBase implements DomainQuotaRestService {

	private DomainQuotaFacade facade;

	public DomainQuotaRestServiceImpl(DomainQuotaFacade facade) {
		super();
		this.facade = facade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Return domain quotas.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<DomainQuotaDto> findAll(
			@Parameter(description = "Domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid) throws BusinessException {
		return facade.findAll(domainUuid);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "find domain quota", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainQuotaDto find(
			@Parameter(description = "Domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "Domain quota Uuid", required = true)
				@PathParam("uuid")
					String uuid, @Parameter(description = "Compute real time quota value. Carefull it could be time consuming.", required = false)
				@QueryParam("realtime") @DefaultValue("false")
					boolean realTime) throws BusinessException {
		return facade.find(uuid, realTime);
	}

	@Path("/{uuid : .*}")
	@PUT
	@Operation(summary = "Update a domain quota", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainQuotaDto update(
			@Parameter(description = "Domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "Domain quota to update. Only quota, override and maintenance field can be updated. If null they will be ignored.", required = true) DomainQuotaDto dto, @Parameter(description = "Domain quota Uuid, if null dto.uuid is used.", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return facade.update(domainUuid, dto, uuid);
	}

}
