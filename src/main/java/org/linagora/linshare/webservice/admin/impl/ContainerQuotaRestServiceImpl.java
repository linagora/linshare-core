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
package org.linagora.linshare.webservice.admin.impl;

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
import org.linagora.linshare.core.facade.webservice.admin.ContainerQuotaFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.AccountQuotaDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.ContainerQuotaDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.ContainerQuotaRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Path("/quotas")
public class ContainerQuotaRestServiceImpl extends WebserviceBase implements ContainerQuotaRestService {

	private ContainerQuotaFacade facade;

	public ContainerQuotaRestServiceImpl(ContainerQuotaFacade facade) {
		this.facade = facade;
	}

	@Path("/containers")
	@GET
	@Override
	public List<ContainerQuotaDto> findAll() throws BusinessException {
		// TODO FMA Quota manage containers filters on findAll method.
		return facade.findAll(null, null);
	}

	@Path("/containers/{uuid}")
	@GET
	@Operation(summary = "find container quota", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContainerQuotaDto find(
			@Parameter(description = "Container quota Uuid", required = true)
				@PathParam("uuid")
					String uuid,
			@Parameter(description = "Compute real time quota value. Carefull it could be time consuming.", required = false)
				@QueryParam("realtime") @DefaultValue("false")
					boolean realTime
				) throws BusinessException {
		return facade.find(uuid, realTime);
	}

	@Path("/containers/{uuid : .* }")
	@PUT
	@Operation(summary = "Update a container quota", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContainerQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContainerQuotaDto update(
			@Parameter(description = "Container quota to update. Only quota, maxFileSize, override and maintenance fields can be updated. If null they will be ignored.", required = true) ContainerQuotaDto dto,
			@Parameter(description = "Container quota Uuid, if null dto.uuid is used.", required = false)
				@PathParam("uuid") String  uuid) throws BusinessException {
		return facade.update(dto, uuid);
	}

}
