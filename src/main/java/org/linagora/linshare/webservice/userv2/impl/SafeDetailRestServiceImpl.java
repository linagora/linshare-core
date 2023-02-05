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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SafeDetailFacade;
import org.linagora.linshare.mongo.entities.SafeDetail;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.SafeDetailRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/safe_details")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SafeDetailRestServiceImpl extends WebserviceBase implements SafeDetailRestService {

	private final SafeDetailFacade safeDetailFacade;

	public SafeDetailRestServiceImpl(SafeDetailFacade safeDetailFacade) {
		super();
		this.safeDetailFacade = safeDetailFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "EXPERIMENTAL - Get all safeDetails.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SafeDetail.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SafeDetail> findAll() throws BusinessException {
		return safeDetailFacade.findAll(null);
	}

	@Path("/{uuid : .*}")
	@DELETE
	@Operation(summary = "EXPERIMENTAL - Delete a safeDetail.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SafeDetail.class))),
			responseCode = "200"
		)
	})
	@Override
	public SafeDetail delete(
			@Parameter(description = "The safeDetail uuid.", required = false) 
				@PathParam("uuid") String uuid,
			@Parameter(description = "The safeDetail to delete.", required = false) SafeDetail safeDetail) throws BusinessException {
		return safeDetailFacade.delete(null, uuid, safeDetail);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Get a SafeDetail.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SafeDetail.class))),
			responseCode = "200"
		)
	})
	@Override
	public SafeDetail find(
			@Parameter(description = "The safeDetail uuid.", required = true) 
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return safeDetailFacade.find(null, uuid);
	}
	
}
