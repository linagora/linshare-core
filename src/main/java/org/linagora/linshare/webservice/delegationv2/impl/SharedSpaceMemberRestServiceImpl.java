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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceMemberFacade;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.webservice.delegationv2.SharedSpaceMemberRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/{actorUuid}/shared_space_members")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SharedSpaceMemberRestServiceImpl implements SharedSpaceMemberRestService {
	private final SharedSpaceMemberFacade sharedSpaceMemberFacade;

	public SharedSpaceMemberRestServiceImpl(SharedSpaceMemberFacade sharedSpaceMemberFacade) {
		super();
		this.sharedSpaceMemberFacade = sharedSpaceMemberFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all shared space member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpaceMember> findAll(
			@Parameter(description = "The actor uuid") 
				@PathParam("actorUuid") String actorUuid) throws BusinessException {
		return sharedSpaceMemberFacade.findAll(actorUuid);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a shared space member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember find(
			@Parameter(description = "The actor uuid") 
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "shared space member uuid")
				@PathParam("uuid") String uuid)
						throws BusinessException {
		return sharedSpaceMemberFacade.find(null, uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "create a shared space member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember addMember(
			@Parameter(description = "The actor uuid")
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "shared space member uuid") SharedSpaceMemberDrive member)
					throws BusinessException {
		return sharedSpaceMemberFacade.create(null, member);
	}
	
	@Path("/{uuid : .*}")
	@PUT
	@Operation(summary = "Update a shared space member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember update(
			@Parameter(description = "The actor uuid")
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The shared space member to update.") SharedSpaceMemberDrive ssmember,
			@Parameter(description = "The shared space member uuid to update.")
				@PathParam("uuid") String uuid,
			@Parameter(description = "If force parameter is false, the role will be updated just in the current node, else if it is true we will force the new updated role in all nested nodes")
				@QueryParam("force") @DefaultValue("false") boolean force,
			@Parameter(description = "Propagate parameter is true by default, the role will be updated in the current node and propagated on nested workgroups which are not updated manually, else if it is false the role will be updated just in current node")
				@QueryParam("propagate") @DefaultValue("true") Boolean propagate)
			throws BusinessException {
		return sharedSpaceMemberFacade.update(actorUuid, ssmember, uuid, force, propagate);
	}

	@Path("/{uuid : .*}")
	@DELETE
	@Operation(summary = "Delete a shared space member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember delete(
			@Parameter(description = "The actor uuid.")
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The shared space member to delete.") SharedSpaceMember ssmember,
			@Parameter(description = "The shared space member uuid to delete.")
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return sharedSpaceMemberFacade.delete(actorUuid, ssmember, uuid);
	}

}
