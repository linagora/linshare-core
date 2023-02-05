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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainPolicyFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPolicyDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.DomainPolicyRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/domain_policies")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class DomainPolicyRestServiceImpl extends WebserviceBase implements
		DomainPolicyRestService {

	private final DomainPolicyFacade domainPolicyFacade;

	public DomainPolicyRestServiceImpl(
			final DomainPolicyFacade domainPolicyFacade) {
		this.domainPolicyFacade = domainPolicyFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all the domain policies.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<DomainPolicyDto> findAll() throws BusinessException {
		return domainPolicyFacade.findAll();
	}

	@Path("/{policyId}")
	@GET
	@Operation(summary = "Find a domain policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainPolicyDto find(
			@Parameter(description = "Identifier of the domain policy to search for.", required = true) @PathParam("policyId") String policyId)
			throws BusinessException {
		return domainPolicyFacade.find(policyId);
	}

	@Path("/{policyId}")
	@HEAD
	@Operation(summary = "Check if a domain policy exists.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void head(
			@Parameter(description = "Identifier of the domain policy to search for.", required = true) @PathParam("policyId") String policyId)
			throws BusinessException {
		domainPolicyFacade.find(policyId);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a domain policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainPolicyDto create(
			@Parameter(description = "Policy to create.", required = true) DomainPolicyDto policy)
			throws BusinessException {
		return domainPolicyFacade.create(policy);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a domain policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainPolicyDto update(
			@Parameter(description = "Policy to update.", required = true) DomainPolicyDto policy)
			throws BusinessException {
		return domainPolicyFacade.update(policy);
	}

	@Path("/{policyId}")
	@DELETE
	@Operation(summary = "Delete a domain policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainPolicyDto delete(
			@Parameter(description = "Identifier of the domain policy to delete.", required = true) @PathParam("policyId") String policyId)
			throws BusinessException {
		return domainPolicyFacade.delete(policyId);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a domain policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainPolicyDto delete(
			@Parameter(description = "Policy to delete.", required = true) DomainPolicyDto policy)
			throws BusinessException {
		return domainPolicyFacade.delete(policy.getIdentifier());
	}
}
