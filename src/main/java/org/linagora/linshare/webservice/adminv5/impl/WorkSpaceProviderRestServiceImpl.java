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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.facade.webservice.adminv5.WorkSpaceProviderFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractWorkSpaceProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPWorkSpaceProviderDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.WorkSpaceProviderRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/domains/{domainUuid}/workspace_providers")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class WorkSpaceProviderRestServiceImpl extends WebserviceBase implements
	WorkSpaceProviderRestService {

	private final WorkSpaceProviderFacade workSpaceProviderFacade;

	public WorkSpaceProviderRestServiceImpl(final WorkSpaceProviderFacade groupProviderFacade) {
		this.workSpaceProviderFacade = groupProviderFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "It will return all workSpace providers of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(array = @ArraySchema(
				schema = @Schema(
					oneOf = {LDAPWorkSpaceProviderDto.class},
					type = "object"
				))
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public Set<AbstractWorkSpaceProviderDto> findAll(
			@Parameter(description = "The domain uuid.")
				@PathParam("domainUuid") String domain) {
		return workSpaceProviderFacade.findAll(domain);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "It will return the detail of a workSpace provider of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPWorkSpaceProviderDto.class},
					type = "object"
				)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public AbstractWorkSpaceProviderDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "groupProvider's uuid.", required = true)
				@PathParam("uuid") String uuid
				) {
		return workSpaceProviderFacade.find(domainUuid, uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "It allows root administrator to create a new workSpace provider.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPWorkSpaceProviderDto.class},
					type = "object"
				)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public AbstractWorkSpaceProviderDto create(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@RequestBody(description = "groupProvider dto to create", required = true,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPWorkSpaceProviderDto.class},
						type = "object"
					)
				)
			)
			AbstractWorkSpaceProviderDto dto) {
		return workSpaceProviderFacade.create(domainUuid, dto);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "It allows administrator to update a workSpace Provider.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPWorkSpaceProviderDto.class},
					type = "object"
				)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
			)
	})
	@Override
	public AbstractWorkSpaceProviderDto update(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "workSpaceProvider's uuid to update, if null, object.uuid is used", required = false)
				@PathParam("uuid") String uuid,
			@RequestBody(description = "workSpaceProvider dto with properties to update", required = false,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPWorkSpaceProviderDto.class},
						type = "object"
					)
				)
			)
			AbstractWorkSpaceProviderDto dto) {
		return workSpaceProviderFacade.update(domainUuid, uuid, dto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "It allows administrator to delete a workSpaceProvider.", responses = {
			@ApiResponse(
				responseCode = "200",
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPWorkSpaceProviderDto.class},
						type = "object"
					)
				)
			),
			@ApiResponse(
				responseCode = "40X",
				content = @Content(
					schema = @Schema(
						implementation = ErrorDto.class
					)
				)
				)
		})
	@Override
	public AbstractWorkSpaceProviderDto delete(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "workSpaceProvider uuid to delete, if null, object.uuid is used", required = false)
				@PathParam("uuid") String uuid,
			@RequestBody(description = "workSpaceProvider uuid to delete, if null, object.uuid is used", required = false,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPWorkSpaceProviderDto.class},
						type = "object"
					)
				)
			)
			AbstractWorkSpaceProviderDto dto) {
		return workSpaceProviderFacade.delete(domainUuid, uuid, dto);
	}
}
