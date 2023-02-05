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
package org.linagora.linshare.webservice.delegationv2.impl;
/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2022 LINAGORA
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
 * applicable to LinShare software.
 */

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.delegation.WorkgroupFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegationv2.WorkGroupRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Deprecated(since = "2.0", forRemoval = true)
@Path("/{actorUuid}/workgroups")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WorkGroupRestServiceImpl extends WebserviceBase implements WorkGroupRestService {

	private final WorkgroupFacade workgroupFacade;

	public WorkGroupRestServiceImpl(final WorkgroupFacade workgroupFacade) {
		this.workgroupFacade = workgroupFacade;
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
	public WorkGroupDto create(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Workgroup to create.", required = true) WorkGroupDto workgroup) throws BusinessException {
		return workgroupFacade.create(actorUuid, workgroup);
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
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return workgroupFacade.find(actorUuid, uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Get a workgroup.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void head(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		workgroupFacade.find(actorUuid, uuid);
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
	public List<WorkGroupDto> findAll(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid)
			throws BusinessException {
		return workgroupFacade.findAll(actorUuid);
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
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Workgroup to delete.", required = true) WorkGroupDto workgroup) throws BusinessException {
		return workgroupFacade.delete(actorUuid, workgroup);
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
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		WorkGroupDto tmp = new WorkGroupDto();
		tmp.setUuid(uuid);
		return workgroupFacade.delete(actorUuid, tmp);
	}

	@Path("/{uuid}")
	@PUT
	@Operation(summary = "Update a workgroup.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupDto update(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("uuid") String workgroupUuid,
			@Parameter(description = "Workgroup to create.", required = true) WorkGroupDto workgroupDto) throws BusinessException {
		return workgroupFacade.update(actorUuid, workgroupUuid, workgroupDto);
	}
}
