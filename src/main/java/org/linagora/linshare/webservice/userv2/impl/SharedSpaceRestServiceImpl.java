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
package org.linagora.linshare.webservice.userv2.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceMemberFacade;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceNodeFacade;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.userv2.SharedSpaceRestService;

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

	private final SharedSpaceNodeFacade nodeFacade;

	private final SharedSpaceMemberFacade memberFacade;

	public SharedSpaceRestServiceImpl(SharedSpaceNodeFacade nodeFacade,
			SharedSpaceMemberFacade memberFacade
			) {
		super();
		this.nodeFacade = nodeFacade;
		this.memberFacade = memberFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all shared spaces (WorkSpaces and workgroups on the top level).", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNodeNested.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpaceNodeNested> findAll(
			@Parameter(description = "Return also the role of the member", required = false)
				@QueryParam("withRole") @DefaultValue("false") boolean withRole,
			@Parameter(description = "The parent uuid.", required = false)
				@QueryParam("parent") String parent) throws BusinessException {
		List<SharedSpaceNodeNested> all = nodeFacade.findAllMyNodes(null, withRole, parent);
		return all.stream().map(ssr -> applySupportOfDriveForOutput(ssr)) .collect(Collectors.toUnmodifiableList());
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
				@PathParam("uuid") String uuid,
			@Parameter(description = "Return also the role of the current user", required = false)
				@QueryParam("withRole") @DefaultValue("false") boolean withRole,
			@Parameter(description = "IF true, the last updater of this resource will be returned as long as metadata. See lastAuditEntry property.", required = false)
				@QueryParam("lastUpdater") @DefaultValue("false") boolean lastUpdater)
			throws BusinessException {
		SharedSpaceNode node = nodeFacade.find(null, uuid, withRole, lastUpdater);
		return applySupportOfDriveForOutput(node);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a shared space node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceNode create(
			@Parameter(description = "shared space node to create", required = true) SharedSpaceNode node)
			throws BusinessException {
		applySupportOfDriveForInput(node);
		node = nodeFacade.create(null, node);
		return applySupportOfDriveForOutput(node);
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
		applySupportOfDriveForInput(node);
		node = nodeFacade.delete(null, node, uuid);
		return applySupportOfDriveForOutput(node);
	}
	
	@Path("/{uuid : .*}")
	@PUT
	@Operation(summary = "Update a shared space node. If versionning delegation functionality is enabled, the user will be able to update the versionning parameter into a workgroup", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceNode update(
			@Parameter(description = "The shared space node to update.")SharedSpaceNode node,
			@Parameter(description = "Ths shared space node uuid to update.")
				@PathParam("uuid") String uuid)
			throws BusinessException {
		applySupportOfDriveForInput(node);
		node = nodeFacade.update(null, node, uuid);
		return applySupportOfDriveForOutput(node);
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
		SharedSpaceNode node = nodeFacade.updatePartial(null, patchNode, uuid);
		return applySupportOfDriveForOutput(node);
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
			@Parameter(description = "It allows to filter the list of SSM by an account uuid")
				@QueryParam("accountUuid")String accountUuid)
			throws BusinessException {
		List<SharedSpaceMember> all = nodeFacade.members(null, uuid, accountUuid);
		return all.stream().map(member -> applySupportOfDriveForOutput(member)) .collect(Collectors.toUnmodifiableList());
	}

	@Path("/{uuid}/members/{memberUuid}")
	@GET
	@Operation(summary = "Get member for the shared space node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember findMemberByNodeAndUuid(
			@Parameter(description = "The members node uuid.")
				@PathParam("uuid")String uuid,
			@Parameter(description = "The uuid of a member within a node")
				@PathParam("memberUuid")String memberUuid)
			throws BusinessException {
		SharedSpaceMember member = memberFacade.findByNodeAndMemberUuid(null, uuid, memberUuid);
		return applySupportOfDriveForOutput(member);
	}

	@Path("/{uuid}/members")
	@POST
	@Operation(summary = "Add a shared space member to workgroup or drive.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceMember addMember(
			@Parameter(description = "The shared space member to add")SharedSpaceMember member)
					throws BusinessException {
		applySupportOfDriveForInput(member);
		member = memberFacade.create(null, member);
		return applySupportOfDriveForOutput(member);
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
			@Parameter(description = "The shared space member to delete.") SharedSpaceMember member,
			@Parameter(description = "The shared space member uuid")
				@PathParam(value="memberUuid")String memberUuid)
			throws BusinessException {
		applySupportOfDriveForInput(member);
		member = memberFacade.delete(null, member, memberUuid);
		return applySupportOfDriveForOutput(member);
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
			@Parameter(description = "The shared space member to update.") SharedSpaceMember member,
			@Parameter(description = "The shared space member uuid")
				@PathParam(value="memberUuid")String memberUuid,
			@Parameter(description = "If force parameter is false, the role will be updated just in the current node, else if it is true we will force the new updated role in all nested nodes")
				@QueryParam("force") @DefaultValue("false") boolean force,
			@Parameter(description = "Propagate parameter is true by default, the role will be updated in the current node and propagated on nested workgroups which are not updated manually, else if it is false the role will be updated just in current node")
				@QueryParam("propagate") @DefaultValue("true") Boolean propagate)
			throws BusinessException {
		applySupportOfDriveForInput(member);
		member = memberFacade.update(null, member, memberUuid, force, propagate);
		return applySupportOfDriveForOutput(member);
	}

	@Path("/{uuid}/audit")
	@GET
	@Operation(summary = "Get all traces for a sharedSpace.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAllAudits(
			@Parameter(description = "The sharedSpace uuid.", required = true)
				@PathParam("uuid") String sharedSpaceUuid,
			@Parameter(description = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions,
			@Parameter(description = "Filter by type of resource's types.", required = false)
				@QueryParam("types") List<AuditLogEntryType> types,
				@QueryParam("beginDate") String beginDate,
				@QueryParam("endDate") String endDate,
			@Parameter(description = "Choose the specific node which you like to list the audits ", required = false)
				@QueryParam("resourceUuid") String resourceUuid) {
		return nodeFacade.findAllSharedSpaceAudits(sharedSpaceUuid, actions, types, beginDate, endDate, resourceUuid);
	}

	/**
	 * Workarounds to support DRIVE in linshare 5.0
	 */
	private void applySupportOfDriveForInput(SharedSpaceMember member) {
		if (member != null) {
			if (NodeType.DRIVE.equals(member.getType())) {
				member.setType(NodeType.WORK_SPACE);
			}
		}
	}

	private SharedSpaceMember applySupportOfDriveForOutput(SharedSpaceMember member) {
		if (member != null) {
			if (NodeType.WORK_SPACE.equals(member.getType())) {
				member.setType(NodeType.DRIVE);
			}
		}
		return member;
	}

	private void applySupportOfDriveForInput(SharedSpaceNode node) {
		if (node != null) {
			if (NodeType.DRIVE.equals(node.getNodeType())) {
				node.setNodeType(NodeType.WORK_SPACE);
			}
		}
	}

	private SharedSpaceNodeNested applySupportOfDriveForOutput(SharedSpaceNodeNested node) {
		if (node != null) {
			if (NodeType.WORK_SPACE.equals(node.getNodeType())) {
				node.setNodeType(NodeType.DRIVE);
			}
		}
		return node;
	}

	private SharedSpaceNode applySupportOfDriveForOutput(SharedSpaceNode node) {
		if (node != null) {
			if (NodeType.WORK_SPACE.equals(node.getNodeType())) {
				node.setNodeType(NodeType.DRIVE);
			}
		}
		return node;
	}
}
