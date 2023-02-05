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
package org.linagora.linshare.webservice.userv2.impl.only;

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
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupMemberFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.WorkGroupMemberRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/work_groups/{workGroupUuid}/members")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WorkGroupMemberRestServiceImpl extends WebserviceBase implements
		WorkGroupMemberRestService {

	private final WorkGroupMemberFacade workGroupMemberFacade;

	public WorkGroupMemberRestServiceImpl(final WorkGroupMemberFacade workGroupMemberFacade) {
		this.workGroupMemberFacade = workGroupMemberFacade;
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a workgroup member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupMemberDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupMemberDto create(
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The user domain identifier.", required = true) WorkGroupMemberDto workGroupMember)
					throws BusinessException {
		return workGroupMemberFacade.create(workGroupUuid, workGroupMember);
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all workgroup members.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupMemberDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<WorkGroupMemberDto> findAll(
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid)
				throws BusinessException {
		return workGroupMemberFacade.findAll(workGroupUuid);
	}

	@Path("/{userUuid}")
	@GET
	@Operation(summary = "Get a workgroup member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupMemberDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupMemberDto find(
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The user uuid.", required = true) @PathParam("userUuid") String userUuid)
			throws BusinessException {
		return workGroupMemberFacade.find(workGroupUuid, userUuid);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a workgroup member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupMemberDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupMemberDto update(
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The workgroup member to update.", required = true) WorkGroupMemberDto workGroupMember)
					throws BusinessException {
		return workGroupMemberFacade.update(workGroupUuid, workGroupMember);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a workgroup member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupMemberDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupMemberDto delete(
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The workgroup member to delete.", required = true) WorkGroupMemberDto workGroupMember)
					throws BusinessException {
		return workGroupMemberFacade.delete(workGroupUuid, workGroupMember.getUserUuid());
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a workgroup member.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupMemberDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupMemberDto delete(
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The user uuid.", required = true) @PathParam("uuid") String userUuid)
					throws BusinessException {
		return workGroupMemberFacade.delete(workGroupUuid, userUuid);
	}
}
