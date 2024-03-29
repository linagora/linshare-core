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
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.webservice.adminv5.FunctionalityRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("domains/{domainUuid}/functionalities")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class FunctionalityRestServiceImpl implements
		FunctionalityRestService {

	protected final FunctionalityFacade functionalityFacade;

	public FunctionalityRestServiceImpl(
			final FunctionalityFacade functionalityFacade) {
		this.functionalityFacade = functionalityFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "It will return all functionalities of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(array = @ArraySchema(
				schema = @Schema( implementation = FunctionalityDto.class))
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
	public List<FunctionalityDto> findAll(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam(value = "domainUuid") String domainUuid,
			@Parameter(description = "Functionality parent identifier, in order to list nested functionalities", required = false)
				@QueryParam(value = "parentIdentifier") String parentIdentifier,
			@Parameter(description = "Return all functionalities (root and nested ones) in one list", required = false)
				@QueryParam("subs") @DefaultValue("false") boolean withSubFunctionalities)
			throws BusinessException {
		return functionalityFacade.findAll(domainUuid, parentIdentifier, withSubFunctionalities);
	}

	@Path("/{identifier}")
	@GET
	@Operation(summary = "It will return one functionality of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema( implementation = FunctionalityDto.class)
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
	public FunctionalityDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam(value = "domainUuid") String domainUuid,
			@Parameter(description = "Functionality identifier", required = true)
				@PathParam(value = "identifier") String identifier)
			throws BusinessException {
		return functionalityFacade.find(domainUuid, identifier);
	}

	@Path("/{identifier: .*}")
	@PUT
	@Operation(summary = "It allows adminstrator to update a functionality.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
					schema = @Schema( implementation = FunctionalityDto.class)
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
	public FunctionalityDto update(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam(value = "domainUuid") String domainUuid,
			@Parameter(description = "Functionality identifier", required = false)
				@PathParam(value = "identifier") String identifier,
			@Parameter(description = "functionality to update", required = true)
				FunctionalityDto func)
			throws BusinessException {
		return functionalityFacade.update(domainUuid, identifier, func);
	}

	@Path("/{identifier: .*}")
	@DELETE
	@Operation(summary = "It allows adminstrator to reset a functionality.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
					schema = @Schema( implementation = FunctionalityDto.class)
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
	public FunctionalityDto delete(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam(value = "domainUuid") String domainUuid,
			@Parameter(description = "Functionality identifier", required = false)
				@PathParam(value = "identifier") String identifier, 
			@Parameter(description = "functionality to reset (restore parent's values", required = false)
				FunctionalityDto func) throws BusinessException {
		return functionalityFacade.delete(domainUuid, identifier, func);
	}
}
