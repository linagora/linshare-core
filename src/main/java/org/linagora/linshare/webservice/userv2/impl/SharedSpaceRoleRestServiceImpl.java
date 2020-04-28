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
package org.linagora.linshare.webservice.userv2.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.NodeType;
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
		return sharedSpaceRoleFacade.find(null, uuid);
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
		return sharedSpaceRoleFacade.findByName(null, name);
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
		return sharedSpaceRoleFacade.findAll(null, nodeType);
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
		return sharedSpaceRoleFacade.findAll(null, roleUuid);
	}

}
