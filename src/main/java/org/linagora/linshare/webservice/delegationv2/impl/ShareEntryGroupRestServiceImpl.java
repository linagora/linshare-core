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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareEntryGroupDto;
import org.linagora.linshare.core.facade.webservice.delegation.ShareEntryGroupFacade;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.delegationv2.ShareEntryGroupRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("{actorUuid}/share_entry_group")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ShareEntryGroupRestServiceImpl implements ShareEntryGroupRestService {

	private final ShareEntryGroupFacade shareEntryGroupFacade;

	public ShareEntryGroupRestServiceImpl(ShareEntryGroupFacade shareEntryGroupFacade) {
		this.shareEntryGroupFacade = shareEntryGroupFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all share entries group for an user.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<ShareEntryGroupDto> findAll(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@QueryParam("full") @DefaultValue("false") boolean full) throws BusinessException {
		return shareEntryGroupFacade.findAll(Version.V2, actorUuid, full);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareEntryGroupDto find(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group's uuid to find.", required = true) @PathParam("uuid") String uuid,
			@QueryParam("full") @DefaultValue("false") boolean full) throws BusinessException {
		return shareEntryGroupFacade.find(Version.V2, actorUuid, uuid, full);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void head(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group's uuid to find.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		shareEntryGroupFacade.find(Version.V2, actorUuid, uuid, false);
	}

	@Path("/{uuid}")
	@PUT
	@Operation(summary = "Update a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareEntryGroupDto update(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group to update.", required = true) @PathParam("shareEntryGroupDto") ShareEntryGroupDto shareEntryGroupDto)
					throws BusinessException {
		return shareEntryGroupFacade.update(actorUuid, shareEntryGroupDto);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareEntryGroupDto delete(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group's uuid to delete.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return shareEntryGroupFacade.delete(actorUuid, uuid);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareEntryGroupDto delete(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group to delete.", required = true) @PathParam("uuid") ShareEntryGroupDto shareEntryGroupDto)
					throws BusinessException {
		Validate.notNull(shareEntryGroupDto);
		return shareEntryGroupFacade.delete(actorUuid, shareEntryGroupDto.getUuid());
	}
}
