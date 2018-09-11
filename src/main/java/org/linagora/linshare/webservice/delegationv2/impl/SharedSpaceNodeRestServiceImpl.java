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
package org.linagora.linshare.webservice.delegationv2.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceNodeFacade;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.delegationv2.SharedSpaceNodeRestService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{actorUuid}/shared_space_nodes")
@Api(value = "/rest/delegation/v2/{actorUuid}/shared_space_nodes", description = "requests API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SharedSpaceNodeRestServiceImpl implements SharedSpaceNodeRestService {

	private final SharedSpaceNodeFacade nodeFacade;

	public SharedSpaceNodeRestServiceImpl(SharedSpaceNodeFacade nodeFacade) {
		super();
		this.nodeFacade = nodeFacade;
	}
	
	@Path("/")
	@GET
	@ApiOperation(value = "Get all shared space nodes.", response = SharedSpaceNodeNested.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<SharedSpaceNodeNested> findAll(
			@ApiParam(value = "The actor uuid.", required = true)
				@PathParam("actorUuid")String actorUuid)
			throws BusinessException {
		return nodeFacade.findAllMyNodes(actorUuid);
	}
	
	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a shared space node.", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceNode find(
			@ApiParam(value = "The actor uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "The shared space node uuid", required = true) 
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return nodeFacade.find(actorUuid, uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a shared space node.", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceNode create(
			@ApiParam(value = "The actor uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "The shared space node to create.", required = true) SharedSpaceNode node)
			throws BusinessException {
		return nodeFacade.create(actorUuid, node);
	}
	
	@Path("/{uuid : .*}")
	@DELETE
	@ApiOperation(value = "Delete a shared space node.", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SharedSpaceNode delete(
			@ApiParam(value="The actor uuid.")
				@PathParam(value="actorUuid")String actorUuid,
			@ApiParam(value = "sharedSpaceNode to delete. ", required = true)SharedSpaceNode node,
			@ApiParam(value = "shared space node's uuid.", required = false)
				@PathParam(value="uuid")String uuid) 
			throws BusinessException {
		return nodeFacade.delete(actorUuid, node, uuid);
	}
	
	@Path("/{uuid : .*}")
	@PUT
	@ApiOperation(value = "Update a shared space node.", response = SharedSpaceNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error"),})
	@Override
	public SharedSpaceNode update(
			@ApiParam(value="The actor uuid.")
				@PathParam(value="actorUuid")String actorUuid, 
			@ApiParam(value = "The sharedSpaceNode to update. ", required = true)SharedSpaceNode node,
			@ApiParam(value = "The shared space node's uuid to update.", required = false)
				@PathParam(value="uuid")String uuid)
			throws BusinessException {
		return nodeFacade.update(actorUuid, node, uuid);
	}

}
