/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
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

package org.linagora.linshare.webservice.userv2.impl;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
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
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupFacade;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.WorkGroupRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Deprecated(since = "2.0", forRemoval = true)
@Path("/work_groups")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WorkGroupRestServiceImpl extends WebserviceBase implements WorkGroupRestService {

	private final WorkGroupFacade workGroupFacade;

	public WorkGroupRestServiceImpl(final WorkGroupFacade workGroupFacade) {
		this.workGroupFacade = workGroupFacade;
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a workgroup.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupDto create(@Parameter(description = "Workgroup to create.", required = true) WorkGroupDto workgroup)
			throws BusinessException {
		return workGroupFacade.create(workgroup);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Get a workgroup.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupDto find(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "Return also all workgroup members if true.", required = false)
				@QueryParam("members") @DefaultValue("false") Boolean members)
			throws BusinessException {
		return workGroupFacade.find(uuid, members);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Get a workgroup.")
	@Override
	public void head(@Parameter(description = "The workgroup uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		workGroupFacade.find(uuid, false);
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all workgroups.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<WorkGroupDto> findAll() throws BusinessException {
		return workGroupFacade.findAll();
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a workgroup.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupDto delete(
			@Parameter(description = "Workgroup to delete.", required = true) WorkGroupDto workgroup)
					throws BusinessException {
		return workGroupFacade.delete(workgroup);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a workgroup.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupDto delete(
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return workGroupFacade.delete(uuid);
	}

	@Path("/{uuid}")
	@PUT
	@Operation(summary = "Update a workgroup.")
	@Override
	public WorkGroupDto update(@Parameter(description = "The workgroup uuid.", required = true) @PathParam("uuid") String workGroupUuid,
			@Parameter(description = "Workgroup to create.", required = true) WorkGroupDto workGroupDto) throws BusinessException {
		return workGroupFacade.update(workGroupUuid, workGroupDto);
	}

	@Path("/{uuid}/audit")
	@GET
	@Operation(summary = "Get all traces for a workgroup.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAll(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("uuid") String workGroupUuid,
			@Parameter(description = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions,
			@Parameter(description = "Filter by type of resource's types.", required = false)
				@QueryParam("types") List<AuditLogEntryType> types,
				@QueryParam("beginDate") String beginDate,
				@QueryParam("endDate") String endDate) {
		return workGroupFacade.findAll(workGroupUuid, actions, types, beginDate, endDate, null);
	}
}
