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
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceRoleFacade;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.webservice.userv2.SharedSpaceRoleRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/shared_space_roles")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SharedSpaceRoleRestServiceImpl implements SharedSpaceRoleRestService {

	protected final SharedSpaceRoleFacade sharedSpaceRoleFacade;

	public SharedSpaceRoleRestServiceImpl(SharedSpaceRoleFacade sharedSpaceFacade) {
		super();
		this.sharedSpaceRoleFacade = sharedSpaceFacade;
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a shared space role.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceRole.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceRole find(
			@Parameter(description = "shared space role uuid.", required = true) 
				@PathParam(value = "uuid") String uuid)
						throws BusinessException {
		return supportDrive(sharedSpaceRoleFacade.find(null, uuid));
	}

	@Path("/role/{name}")
	@GET
	@Operation(summary = "Find a shared space role .", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceRole.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceRole findByName(
			@Parameter(description = "shared space role name.", required = true)
				@PathParam(value = "name") String name)
						throws BusinessException {
		return supportDrive(sharedSpaceRoleFacade.findByName(null, name));
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all shared space roles.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceRole.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpaceRole> findAll(
			@Parameter(description = "Filter the roles by node type.", required = false)
				@QueryParam("nodeType") @DefaultValue("WORK_GROUP") NodeType nodeType) throws BusinessException {
		if (NodeType.DRIVE.equals(nodeType)) {
			nodeType = NodeType.WORK_SPACE;
		}
		List<SharedSpaceRole> all = sharedSpaceRoleFacade.findAll(null, nodeType);
		return all.stream().map(ssr -> supportDrive(ssr)) .collect(Collectors.toUnmodifiableList());
	}

	private SharedSpaceRole supportDrive(SharedSpaceRole ssr) {
		if (NodeType.WORK_SPACE.equals(ssr.getType())) {
			ssr.setType(NodeType.DRIVE);
		}
		return ssr;
	}

	private SharedSpacePermission supportDrive(SharedSpacePermission perm) {
		if (SharedSpaceResourceType.WORK_SPACE.equals(perm.getResource())) {
			perm.setResource(SharedSpaceResourceType.DRIVE);
		}
		if (SharedSpaceResourceType.WORK_GROUP.equals(perm.getResource())) {
			perm.setResource(SharedSpaceResourceType.WORKGROUP);
		}
		return perm;
	}

	@Path("/{uuid}/permissions")
	@GET
	@Operation(summary = "Find a shared space role .", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpaceRole.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpacePermission> findAllPermissions(
			@Parameter(description = "shared space role uuid.", required = true)
				@PathParam(value = "uuid") String roleUuid) throws BusinessException {
		return sharedSpaceRoleFacade.findAll(null, roleUuid)
				.stream()
				.map(ssr -> supportDrive(ssr))
				.collect(Collectors.toUnmodifiableList());
	}

}
