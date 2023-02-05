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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.facade.webservice.common.dto.MimeTypeDto;
import org.linagora.linshare.core.facade.webservice.user.MimeTypeFacade;
import org.linagora.linshare.webservice.userv2.MimeTypeRestService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


//Class created to generate the swagger documentation of v1 RestServices
@Path("/mime_types")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MimeTypeRestServiceImpl implements MimeTypeRestService {

	private final MimeTypeFacade facade;

	public MimeTypeRestServiceImpl(final MimeTypeFacade facade) {
		this.facade = facade;
	}

	@GET
	@Path("/")
	@Operation(summary = "Find all Mime types.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimeTypeDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<MimeTypeDto> find(@QueryParam("disabled") @DefaultValue("false") boolean disabled) {
		return facade.find(null, disabled);
	}
}
