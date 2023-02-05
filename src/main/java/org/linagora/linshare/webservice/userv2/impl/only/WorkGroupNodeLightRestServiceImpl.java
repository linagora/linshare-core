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

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupNodeFacade;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.WorkGroupNodeLightRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/work_groups_nodes")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WorkGroupNodeLightRestServiceImpl extends WebserviceBase implements WorkGroupNodeLightRestService{

	protected final WorkGroupNodeFacade workGroupNodeFacade;

	public WorkGroupNodeLightRestServiceImpl(WorkGroupNodeFacade workGroupNodeFacade) {
		super();
		this.workGroupNodeFacade = workGroupNodeFacade;
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Get a workgroup node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupNode find(
			@Parameter(description = "The workgroup node uuid.", required = true)
				@PathParam("uuid") String uuid,
			@QueryParam("tree") @DefaultValue("false") Boolean withTree)
			throws BusinessException {
		String workGroupUuid = workGroupNodeFacade.findByWorkGroupNodeUuid(uuid);
		return workGroupNodeFacade.find(null, workGroupUuid, uuid, withTree);
	}

}
