/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.admin.SharedSpaceRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/shared_spaces")
@Api(value = "/rest/admin/shared_spaces", description = "shared spaces service.", produces = "application/json,application/xml", consumes = "application/json,application/xml")
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
	@ApiOperation(value = "Find a shared space node.", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceNode find(
			@ApiParam(value = "shared space node's uuid.", required = true)
				@PathParam("uuid") String uuid) 
			throws BusinessException {
		return ssNodeFacade.find(null, uuid, false);
	}
	
	@Path("/{uuid : .*}")
	@DELETE
	@ApiOperation(value = "Delete a shared space node.", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceNode delete(
			@ApiParam(value = "sharedSpaceNode to delete. ", required = true)SharedSpaceNode node,
			@ApiParam(value = "shared space node's uuid.", required = false)
				@PathParam(value = "uuid") String uuid) 
			throws BusinessException {
		return ssNodeFacade.delete(null, node, uuid);
	}
	
	@Path("/{uuid : .*}")
	@PUT
	@ApiOperation(value = "Update a shared space node.", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceNode update(
			@ApiParam(value = "sharedSpaceNode to delete. ", required = true)SharedSpaceNode node,
			@ApiParam("The shared space node.")
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return ssNodeFacade.update(null, node, uuid);
	}
	
	@Path("/{uuid}")
	@PATCH
	@ApiOperation(value = "Update a shared space node. If versionning delegation functionality is enabled, the user will be able to update the versionning parameter into a workgroup", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceNode update(
			@ApiParam(value = "The Patch that contains the feilds that'll be updated in the node") PatchDto patchNode,
			@ApiParam(value = "The uuid of the node that'll be updated.")
				@PathParam("uuid")String uuid) throws BusinessException {
		return ssNodeFacade.updatePartial(null, patchNode, uuid);
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Get all shared space nodes.", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "No permission to list all shared space nodes."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<SharedSpaceNode> findAll() throws BusinessException {
		return ssNodeFacade.findAll();
	}
	
	@Path("/{uuid}/members")
	@GET
	@ApiOperation(value = "Get all members for the shared space node.", response = SharedSpaceMember.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "No permission to list all members for this shared space node."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<SharedSpaceMember> members(
			@ApiParam("The members node uuid.")
				@PathParam("uuid")String uuid,
			@ApiParam("The uuid of an account within a node")
				@QueryParam("accountUuid")String accountUuid)
			throws BusinessException {
		return ssNodeFacade.members(null, uuid, accountUuid);
	}
	
	@Path("{uuid}/members")
	@POST
	@ApiOperation(value = "add a shared space member.", response = SharedSpaceMember.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the required role."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceMember addMember(
			@ApiParam("The shared space member to add")SharedSpaceMemberDrive member)
					throws BusinessException {
		return ssMemberFacade.create(null, member);
	}
	
	@Path("{uuid}/members/{memberUuid : .*}")
	@DELETE
	@ApiOperation(value = "Delete a shared space member.", response = SharedSpaceMember.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the required role."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceMember deleteMember(
			@ApiParam("The shared space member to delete.")SharedSpaceMember member,
			@ApiParam("The shared space member uuid")
				@PathParam(value="memberUuid")String memberUuid)
			throws BusinessException {
		return ssMemberFacade.delete(null, member, memberUuid);
	}
	
	@Path("{uuid}/members/{memberUuid : .*}")
	@PUT
	@ApiOperation(value = "Update a shared space member.", response = SharedSpaceMember.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the required role."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceMember updateMember(
			@ApiParam("The shared space member to update.")SharedSpaceMemberDrive member,
			@ApiParam("The shared space member uuid")
				@PathParam(value="memberUuid")String memberUuid)
			throws BusinessException {
		return ssMemberFacade.update(null, member, memberUuid);
	}
	
	@Path("{uuid}/members/{memberUuid}")
	@GET
	@ApiOperation(value = "Get a shared space member.", response = SharedSpaceMember.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the required role."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceMember findMember(
			@ApiParam("The shared space member uuid")
				@PathParam(value="memberUuid")String memberUuid)
			throws BusinessException {
		return ssMemberFacade.find(null, memberUuid);
	}

	@Path("/{uuid}/workgroups")
	@GET
	@ApiOperation(value = "Get workgroups inside this node.", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "No permission to list all workgroups inside a shared space node."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<SharedSpaceNodeNested> findAllWorkGroupsInsideNode(
			@ApiParam("The node uuid.")
				@PathParam("uuid")String uuid) 
			throws BusinessException {
		return ssNodeFacade.findAllWorkGroupsInsideNode(null, uuid);
	}

}
