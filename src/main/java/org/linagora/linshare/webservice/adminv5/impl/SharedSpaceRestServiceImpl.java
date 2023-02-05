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
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceField;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceMemberField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.SharedSpaceFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceMemberFacade;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.adminv5.SharedSpaceRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/shared_spaces")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SharedSpaceRestServiceImpl implements SharedSpaceRestService {

	private final SharedSpaceFacade sharedSpaceFacade;

	private final SharedSpaceMemberFacade sharedSpaceMemberFacade;

	private PagingResponseBuilder<SharedSpaceNodeNested> pageResponseBuilder= new PagingResponseBuilder<>();

	private PagingResponseBuilder<SharedSpaceMember> memberResponseBuilder= new PagingResponseBuilder<>();

	public SharedSpaceRestServiceImpl(SharedSpaceFacade sharedSpaceFacade,
			SharedSpaceMemberFacade ssMemberFacade) {
		super();
		this.sharedSpaceFacade = sharedSpaceFacade;
		this.sharedSpaceMemberFacade = ssMemberFacade;
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a shared space.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceNode find(
			@Parameter(description = "shared space's uuid.", required = true)
				@PathParam("uuid") String uuid) 
			throws BusinessException {
		return sharedSpaceFacade.find(null, uuid);
	}
	
	@Path("/{uuid : .*}")
	@DELETE
	@Operation(summary = "Delete a shared space.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceNode delete(
			@Parameter(description = "sharedSpace to delete. ", required = true)SharedSpaceNode node,
			@Parameter(description = "shared space's uuid.", required = false)
				@PathParam(value = "uuid") String uuid) 
			throws BusinessException {
		return sharedSpaceFacade.delete(null, node, uuid);
	}
	
	@Path("/{uuid : .*}")
	@PUT
	@Operation(summary = "Update a shared space.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceNode update(
			@Parameter(description = "sharedSpace to update. ", required = true)SharedSpaceNode node,
			@Parameter(description = "The shared space's uuid.")
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return sharedSpaceFacade.update(null, node, uuid);
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
		return sharedSpaceFacade.updatePartial(null, patchNode, uuid);
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all shared spaces.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response findAll(
			@Parameter(description = "If the admin specify the account he will retrieve the list of shared Spaces of the choosen account, else all shared spaces of all accounts domains will be returned.", required = false)
				@QueryParam("account") String accountUuid,
			@Parameter(description = "If the admin choose a list of domains he will retrieve the list of shared Spaces related to choosen domains, else all shared spaces of all domains will be returned.", required = false)
				@QueryParam("domains") List<String> domainUuids,
			@Parameter(description = "The admin can choose the order of sorting the sharedSpace's list to retrieve, if not set the ascending order will be applied by default.", required = false)
				@QueryParam("sortOrder") @DefaultValue("ASC") String sortOrder,
			@Parameter(description = "The admin can choose the field to sort with the sharedSpace's list to retrieve, if not set the modification date order will be choosen by default.",
					required = false,
					schema = @Schema(implementation = SharedSpaceField.class, defaultValue = "modificationDate")
				)
				@QueryParam("sortField") @DefaultValue("modificationDate") String sortField,
			@Parameter(description = "Filter the returned sharedSpaces by their types(WORK_GROUP/WORK_SPACE).",
					required = false,
					schema = @Schema(implementation = NodeType.class)
				)
				@QueryParam("nodeType") Set<String> nodeTypes,
			@Parameter(description = "Filter the returned sharedSpaces by member roles.", required = false)
				@QueryParam("role") Set<String> sharedSpaceRoles,
			@Parameter(description = "Search pattern that contains matching sequence in name of sharedSpace.", required = false)
				@QueryParam("name") String name,
			@Parameter(description = "It allows administrator to retrieve sharedSpaces with members number greater than the entered value (Should be more than 1).", required = false)
				@QueryParam("greaterThanOrEqualTo") Integer greaterThanOrEqualTo,
			@Parameter(description = "It allows administrator to retrieve sharedSpaces with members number less than the entered value (Should be more than 1).", required = false)
				@QueryParam("lessThanOrEqualTo") Integer lessThanOrEqualTo,
			@Parameter(description = "The admin can choose the page number to get.", required = false)
				@QueryParam("page") Integer pageNumber, @Parameter(description = "The admin can choose the number of elements to get.", required = false)
			@QueryParam("size") Integer pageSize) throws BusinessException {
		Set<NodeType> types = Sets.newHashSet();
		nodeTypes.forEach(type -> types.add(NodeType.valueOf(type)));
		PageContainer<SharedSpaceNodeNested> container = sharedSpaceFacade.findAll(null, accountUuid, domainUuids, SortOrder.valueOf(sortOrder),
				SharedSpaceField.valueOf(sortField), types, sharedSpaceRoles, name, greaterThanOrEqualTo, lessThanOrEqualTo, pageNumber, pageSize);
		return pageResponseBuilder.build(container);
	}

	@Path("/{uuid}/members")
	@GET
	@Operation(summary = "Get all members for the choosen shared space.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceMember.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response members(
			@Parameter(description = "The sharedSpace's uuid to retrieve its members.")
				@PathParam("uuid")String uuid,
			@Parameter(description = "The uuid of an account within a sharedSpace")
				@QueryParam("accountUuid")String accountUuid,
			@Parameter(description = "Filter the returned sharedSpace members by member roles.", required = false)
				@QueryParam("roles") Set<String> sharedSpaceRoles,
			@Parameter(description = "Filter the sharedSpace members list by email, if `pattern` is provided `email` won't be used.", required = false)
				@QueryParam("email") String email,
			@Parameter(description = "Filter the sharedSpace members list by first name, if `pattern` is provided `firstName` won't be used.", required = false)
				@QueryParam("firstName") String firstName,
			@Parameter(description = "Filter the sharedSpace members list by last name, if `pattern` is provided `lastName` won't be used.", required = false)
				@QueryParam("lastName") String lastName,
			@Parameter(description = "Filter the sharedSpace members list by pattern, if provided, `firstName`, `lastName` and `email` won't be used.", required = false)
				@QueryParam("pattern") String pattern,
			@Parameter(description = "Filter the sharedSpace members list by accountType.", required = false)
				@QueryParam("type") String type,
			@Parameter(description = "The admin can choose the order of sorting the sharedSpaceMembers' list to retrieve, if not set the ascending order will be applied by default.", required = false)
				@QueryParam("sortOrder") @DefaultValue("ASC") String sortOrder, @Parameter(description = "The admin can choose the field to sort with the sharedSpaceMembers list to retrieve, if not set the modification date order will be choosen by default.", required = false)
				@QueryParam("sortField") @DefaultValue("modificationDate") String sortField, @Parameter(description = "The admin can choose the page number to get.", required = false)
				@QueryParam("page") Integer pageNumber, @Parameter(description = "The admin can choose the number of elements to get.", required = false)
				@QueryParam("size") Integer pageSize) throws BusinessException {
		if (!Strings.isNullOrEmpty(pattern)) {
			firstName = null;
			lastName = null;
			email = null;
		}
		PageContainer<SharedSpaceMember> container = sharedSpaceFacade.members(null, uuid, accountUuid,
				sharedSpaceRoles, email, firstName, lastName,
				pattern, type, SortOrder.valueOf(sortOrder), SharedSpaceMemberField.valueOf(sortField), pageNumber, pageSize);
		return memberResponseBuilder.build(container);
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
			@Parameter(description = "The shared space member to add")SharedSpaceMember member)
					throws BusinessException {
		return sharedSpaceMemberFacade.create(null, member);
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
		return sharedSpaceMemberFacade.delete(null, member, memberUuid);
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
			@Parameter(description = "The shared space member to update.")SharedSpaceMember member,
			@Parameter(description = "The shared space member uuid")
				@PathParam(value="memberUuid")String memberUuid,
			@Parameter(description = "If force parameter is false, the role will be updated just in the current node, else if it is true we will force the new updated role in all nested nodes")
				@QueryParam("force") @DefaultValue("false") boolean force,
			@Parameter(description = "Propagate parameter is true by default, the role will be updated in the current node and propagated on nested workgroups which are not updated manually, else if it is false the role will be updated just in current node")
				@QueryParam("propagate") @DefaultValue("true") Boolean propagate)
			throws BusinessException {
		return sharedSpaceMemberFacade.update(null, member, memberUuid, force, propagate);
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
		return sharedSpaceMemberFacade.find(null, memberUuid);
	}
}
