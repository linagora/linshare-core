/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.webservice.userv2.impl;

import java.util.List;
import java.util.Set;

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
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceMemberFacade;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceNodeFacade;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupFacade;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.userv2.SharedSpaceRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/shared_spaces")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SharedSpaceRestServiceImpl implements SharedSpaceRestService {

	private final SharedSpaceNodeFacade nodeFacade;

	private final SharedSpaceMemberFacade memberFacade;

	private final WorkGroupFacade workGroupFacade;

	public SharedSpaceRestServiceImpl(SharedSpaceNodeFacade nodeFacade,
			SharedSpaceMemberFacade memberFacade,
			WorkGroupFacade workGroupFacade
			) {
		super();
		this.nodeFacade = nodeFacade;
		this.memberFacade = memberFacade;
		this.workGroupFacade = workGroupFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all shared spaces (Drives and workgroups on the top level).", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNodeNested.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpaceNodeNested> findAll(
			@Parameter(description = "Return also the role of the member", required = false)
				@QueryParam("withRole") @DefaultValue("false") boolean withRole) throws BusinessException {
		return nodeFacade.findAllMyNodes(null, withRole);
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
				@QueryParam("withRole") @DefaultValue("false") boolean withRole)
			throws BusinessException {
		return nodeFacade.find(null, uuid, withRole);
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
		return nodeFacade.create(null, node);
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
		return nodeFacade.delete(null, node, uuid);
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
		return nodeFacade.update(null, node, uuid);
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
			@Parameter(description = "The Patch that contains the feilds that'll be updated in the node")PatchDto patchNode,
			@Parameter(description = "The uuid of the node that'll be updated.")
				@PathParam("uuid")String uuid) throws BusinessException {
		return nodeFacade.updatePartial(null, patchNode, uuid);
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
		return nodeFacade.members(null, uuid, accountUuid);
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
		return memberFacade.findByNodeAndMemberUuid(null, uuid, memberUuid);
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
		return memberFacade.create(null, member);
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
		return memberFacade.delete(null, member, memberUuid);
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
				@QueryParam("force") @DefaultValue("false") boolean force)
			throws BusinessException {
		return memberFacade.update(null, member, memberUuid, force);
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
			@Parameter(description = "Choose the specific node which you like to list the audits ", required = true)
				@QueryParam("nodeUuid") String nodeUuid) {
		return workGroupFacade.findAll(sharedSpaceUuid, actions, types, beginDate, endDate, nodeUuid);
	}

	@Path("/{uuid}/workgroups")
	@GET
	@Operation(summary = "Get workgroups inside this node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpaceNodeNested> findAllWorkGroupsInsideNode(
			@Parameter(description = "The node uuid.")
				@PathParam("uuid")String uuid) 
			throws BusinessException {
		return nodeFacade.findAllWorkGroupsInsideNode(null, uuid);
	}

}
