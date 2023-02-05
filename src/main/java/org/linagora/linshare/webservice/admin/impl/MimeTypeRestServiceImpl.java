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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MimeTypeFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MimeTypeDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MimeTypeRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/mime_types")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MimeTypeRestServiceImpl extends WebserviceBase implements
	MimeTypeRestService {

	private final MimeTypeFacade mimeTypeFacade;

	public MimeTypeRestServiceImpl (
			final MimeTypeFacade mimeTypeFacade) {
		this.mimeTypeFacade = mimeTypeFacade;
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a mime type.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimeTypeDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimeTypeDto find(
			@Parameter(description = "Uuid of the mime type to search for.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimeTypeFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a mime type.")
	@Override
	public void head(
			@Parameter(description = "Uuid of the mime type to search for.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		mimeTypeFacade.find(uuid);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a mime type.")
	@Override
	public MimeTypeDto update(
			@Parameter(description = "Policy to update.", required = true) MimeTypeDto policy)
			throws BusinessException {
		return mimeTypeFacade.update(policy);
	}
}
