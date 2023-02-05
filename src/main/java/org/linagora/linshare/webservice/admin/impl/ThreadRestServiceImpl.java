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
package org.linagora.linshare.webservice.admin.impl;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.webservice.admin.ThreadRestService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Deprecated(since = "2.0", forRemoval = true)
@Path("/threads")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ThreadRestServiceImpl implements ThreadRestService {

	private final ThreadFacade threadFacade;

	public ThreadRestServiceImpl(final ThreadFacade threadFacade) {
		super();
		this.threadFacade = threadFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all threads.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<WorkGroupDto> findAll(
			@QueryParam("pattern") String pattern,
			@QueryParam("threadName") String threadName,
			@QueryParam("memberName") String memberName)
					throws BusinessException {
		return threadFacade.findAll(pattern, threadName, memberName);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a thread", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupDto find(@PathParam("uuid") String uuid)
			throws BusinessException {
		return threadFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a thread")
	@Override
	public void head(@PathParam("uuid") String uuid)
			throws BusinessException {
		threadFacade.find(uuid);
	}

	@Path("/{uuid}/members")
	@GET
	@Operation(summary = "Find all thread members.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<WorkGroupMemberDto> members(@PathParam("uuid") String uuid)
			throws BusinessException {
		return threadFacade.members(uuid);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a thread.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupDto update(WorkGroupDto thread) throws BusinessException {
		return threadFacade.update(thread);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a thread.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupDto delete(WorkGroupDto thread) throws BusinessException {
		return threadFacade.delete(thread.getUuid());
	}
}
