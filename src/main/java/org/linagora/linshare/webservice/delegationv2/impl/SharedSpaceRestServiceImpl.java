/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.webservice.delegationv2.impl;

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
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceNodeFacade;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.delegationv2.SharedSpaceRestService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/{actorUuid}/shared_spaces")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SharedSpaceRestServiceImpl implements SharedSpaceRestService {

	private final SharedSpaceNodeFacade nodeFacade;

	public SharedSpaceRestServiceImpl(SharedSpaceNodeFacade nodeFacade) {
		super();
		this.nodeFacade = nodeFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all shared space nodes.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceNodeNested.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpaceNodeNested> findAll(
			@Parameter(description = "The actor uuid.", required = true)
				@PathParam("actorUuid")String actorUuid,
			@Parameter(description = "Return also the role of the member", required = false)
				@QueryParam("withRole") @DefaultValue("false") boolean withRole)
			throws BusinessException {
		return nodeFacade.findAllMyNodes(actorUuid, withRole);
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
			@Parameter(description = "The actor uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The shared space node uuid", required = true) 
				@PathParam("uuid") String uuid,
			@Parameter(description = "Return also the role of the actor", required = false)
				@QueryParam("withRole") @DefaultValue("false") boolean withRole)
			throws BusinessException {
		return nodeFacade.find(actorUuid, uuid, withRole);
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
			@Parameter(description = "The actor uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The shared space node to create.", required = true) SharedSpaceNode node)
			throws BusinessException {
		return nodeFacade.create(actorUuid, node);
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
			@Parameter(description = "The actor uuid.")
				@PathParam(value="actorUuid")String actorUuid,
			@Parameter(description = "sharedSpaceNode to delete. ", required = true)SharedSpaceNode node,
			@Parameter(description = "shared space node's uuid.", required = false)
				@PathParam(value="uuid")String uuid) 
			throws BusinessException {
		return nodeFacade.delete(actorUuid, node, uuid);
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
			@Parameter(description = "The actor uuid.")
				@PathParam(value="actorUuid")String actorUuid, 
			@Parameter(description = "The sharedSpaceNode to update. ", required = true)SharedSpaceNode node,
			@Parameter(description = "The shared space node's uuid to update.", required = false)
				@PathParam(value="uuid")String uuid)
			throws BusinessException {
		return nodeFacade.update(actorUuid, node, uuid);
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
			@Parameter(description = "The actor uuid.")
				@PathParam(value="actorUuid")String actorUuid,
			@Parameter(description = "The node uuid.")
				@PathParam("uuid")String uuid) 
			throws BusinessException {
		return nodeFacade.findAllWorkGroupsInsideNode(actorUuid, uuid);
	}
}
