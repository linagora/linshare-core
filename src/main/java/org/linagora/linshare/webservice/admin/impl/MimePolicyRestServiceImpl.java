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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MimePolicyFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MimePolicyDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MimePolicyRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/mime_policies")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MimePolicyRestServiceImpl extends WebserviceBase implements
		MimePolicyRestService {

	private final MimePolicyFacade mimePolicyFacade;

	public MimePolicyRestServiceImpl(final MimePolicyFacade mimePolicyFacade) {
		this.mimePolicyFacade = mimePolicyFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all the mime policies by domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MimePolicyDto> findAll(
			@Parameter(description = "Identifier of the domain which you are looking into.", required = true) @QueryParam("domainId") String domainId,
			@Parameter(description = "Return current and parent domain's mime policies,"
					+ " or only current domain's if onlyCurrentDomain is true.") @QueryParam("onlyCurrentDomain") @DefaultValue("false") boolean onlyCurrentDomain)
			throws BusinessException {
		return mimePolicyFacade.findAll(domainId, onlyCurrentDomain);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto find(
			@Parameter(description = "Uuid of the mime policy to search for.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Return mime policy with mime types.") @QueryParam("full") @DefaultValue("false") boolean full)
			throws BusinessException {
		MimePolicyDto find = mimePolicyFacade.find(uuid, full);
		return find;
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a mime policy.")
	@Override
	public void head(
			@Parameter(description = "Uuid of the mime policy to search for.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		mimePolicyFacade.find(uuid, false);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto create(
			@Parameter(description = "Policy to create.", required = true) MimePolicyDto policy)
			throws BusinessException {
		return mimePolicyFacade.create(policy);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto update(
			@Parameter(description = "Policy to update.", required = true) MimePolicyDto policy)
			throws BusinessException {
		return mimePolicyFacade.update(policy);
	}

	@Path("/{uuid}/enable_all")
	@PUT
	@Operation(summary = "Set all mime types to enable for the current mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto enableAllMimeTypes(
			@Parameter(description = "Uuid of the mime policy.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimePolicyFacade.enableAllMimeTypes(uuid);
	}

	@Path("/{uuid}/disable_all")
	@PUT
	@Operation(summary = "Set all mime types to disable for the current mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto disableAllMimeTypes(
			@Parameter(description = "Uuid of the mime policy.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimePolicyFacade.disableAllMimeTypes(uuid);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto delete(
			@Parameter(description = "Identifier of the mime policy to delete.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimePolicyFacade.delete(uuid);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto delete(
			@Parameter(description = "Policy to delete.", required = true) MimePolicyDto policy)
			throws BusinessException {
		return mimePolicyFacade.delete(policy.getUuid());
	}
}
