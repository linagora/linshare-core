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
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceMemberFacade;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceNodeFacade;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.webservice.admin.SharedSpaceRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/shared_spaces")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SharedSpaceRestServiceImpl implements SharedSpaceRestService {

	private final SharedSpaceNodeFacade ssNodeFacade;
	
	private final SharedSpaceMemberFacade ssMemberFacade;

	public SharedSpaceRestServiceImpl(SharedSpaceNodeFacade ssNodeFacade,
			SharedSpaceMemberFacade ssMemberFacade) {
		super();
		this.ssNodeFacade = ssNodeFacade;
		this.ssMemberFacade = ssMemberFacade;
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a shared space node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceNode find(
			@Parameter(description = "shared space node's uuid.", required = true)
				@PathParam("uuid") String uuid) 
			throws BusinessException {
		return ssNodeFacade.find(null, uuid, false, false);
	}
	
	@Path("/{uuid : .*}")
	@DELETE
	@Operation(summary = "Delete a shared space node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceNode delete(
			@Parameter(description = "sharedSpaceNode to delete. ", required = true)SharedSpaceNode node,
			@Parameter(description = "shared space node's uuid.", required = false)
				@PathParam(value = "uuid") String uuid) 
			throws BusinessException {
		return ssNodeFacade.delete(null, node, uuid);
	}
	
	@Path("/{uuid : .*}")
	@PUT
	@Operation(summary = "Update a shared space node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceNode update(
			@Parameter(description = "sharedSpaceNode to delete. ", required = true)SharedSpaceNode node,
			@Parameter(description = "The shared space node.")
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return ssNodeFacade.update(null, node, uuid);
	}
	
	@Path("/{uuid}")
	@PATCH
	@Operation(summary = "Update a shared space node. If versionning delegation functionality is enabled, the user will be able to update the versionning parameter into a workgroup", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceNode update(
			@Parameter(description = "The Patch that contains the feilds that'll be updated in the node") PatchDto patchNode,
			@Parameter(description = "The uuid of the node that'll be updated.")
				@PathParam("uuid")String uuid) throws BusinessException {
		return ssNodeFacade.updatePartial(null, patchNode, uuid);
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all shared space nodes.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpaceNode> findAll() throws BusinessException {
		return ssNodeFacade.findAll();
	}
	
	@Path("/{uuid}/members")
	@GET
	@Operation(summary = "Get all members for the shared space node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpaceMember> members(
			@Parameter(description = "The members node uuid.")
				@PathParam("uuid")String uuid,
			@Parameter(description = "The uuid of an account within a node")
				@QueryParam("accountUuid")String accountUuid)
			throws BusinessException {
		return ssNodeFacade.members(null, uuid, accountUuid);
	}
	
	@Path("{uuid}/members")
	@POST
	@Operation(summary = "add a shared space member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember addMember(
			@Parameter(description = "The shared space member to add")SharedSpaceMemberDrive member)
					throws BusinessException {
		return ssMemberFacade.create(null, member);
	}
	
	@Path("{uuid}/members/{memberUuid : .*}")
	@DELETE
	@Operation(summary = "Delete a shared space member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember deleteMember(
			@Parameter(description = "The shared space member to delete.")SharedSpaceMember member,
			@Parameter(description = "The shared space member uuid")
				@PathParam(value="memberUuid")String memberUuid)
			throws BusinessException {
		return ssMemberFacade.delete(null, member, memberUuid);
	}
	
	@Path("{uuid}/members/{memberUuid : .*}")
	@PUT
	@Operation(summary = "Update a shared space member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember updateMember(
			@Parameter(description = "The shared space member to update.")SharedSpaceMemberDrive member,
			@Parameter(description = "The shared space member uuid")
				@PathParam(value="memberUuid")String memberUuid,
			@Parameter(description = "If force parameter is false, the role will be updated just in the current node, else if it is true we will force the new updated role in all nested nodes")
				@QueryParam("force") @DefaultValue("false") boolean force,
			@Parameter(description = "Propagate parameter is true by default, the role will be updated in the current node and propagated on nested workgroups which are not updated manually, else if it is false the role will be updated just in current node")
				@QueryParam("propagate") @DefaultValue("true") Boolean propagate)
			throws BusinessException {
		return ssMemberFacade.update(null, member, memberUuid, force, propagate);
	}
	
	@Path("{uuid}/members/{memberUuid}")
	@GET
	@Operation(summary = "Get a shared space member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember findMember(
			@Parameter(description = "The shared space member uuid")
				@PathParam(value="memberUuid")String memberUuid)
			throws BusinessException {
		return ssMemberFacade.find(null, memberUuid);
	}
}
