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
import org.linagora.linshare.core.facade.webservice.admin.WelcomeMessagesFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.WelcomeMessagesDto;
import org.linagora.linshare.webservice.admin.WelcomeMessagesRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/welcome_messages")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WelcomeMessagesRestServiceImpl implements
		WelcomeMessagesRestService {

	private final WelcomeMessagesFacade welcomeMessagesFacade;

	public WelcomeMessagesRestServiceImpl(final WelcomeMessagesFacade wlcmFacade) {
		this.welcomeMessagesFacade = wlcmFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all welcome message entries.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WelcomeMessagesDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<WelcomeMessagesDto> findAll(
			@Parameter(description = "If not set, actor domain will be use.", required = true) @QueryParam("domainId") String domainId,
			@QueryParam("parent") @DefaultValue("false") Boolean parent)
			throws BusinessException {
		return welcomeMessagesFacade.findAll(domainId, parent);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a welcome message entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WelcomeMessagesDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WelcomeMessagesDto find(
			@Parameter(description = "Welcome message uuid", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return welcomeMessagesFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a welcome message entry.")
	@Override
	public void head(
			@Parameter(description = "Welcome message uuid", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		welcomeMessagesFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a welcome message entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WelcomeMessagesDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WelcomeMessagesDto create(
			@Parameter(description = "Welcome message to create (uuid is required because we will duplicate an existing WelcomeMessage", required = true) WelcomeMessagesDto customDto)
			throws BusinessException {
		return welcomeMessagesFacade.create(customDto);
	}

	@Path("/{uuid : .*}")
	@PUT
	@Operation(summary = "Update a welcome message entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WelcomeMessagesDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WelcomeMessagesDto update(
			@Parameter(description = "Welcome message updated", required = true)
				WelcomeMessagesDto customDto,
			@Parameter(description = "Welcome messages uuid", required = false)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return welcomeMessagesFacade.update(customDto, uuid);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a welcome message entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WelcomeMessagesDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WelcomeMessagesDto delete(
			@Parameter(description = "Welcome message uuid", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return welcomeMessagesFacade.delete(uuid);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a welcome message entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WelcomeMessagesDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WelcomeMessagesDto delete(
			@Parameter(description = "Welcome message to delete", required = true) WelcomeMessagesDto customDto)
			throws BusinessException {
		return welcomeMessagesFacade.delete(customDto);
	}
}