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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.facade.webservice.user.AutoCompleteFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.AutoCompleteResultDto;
import org.linagora.linshare.webservice.userv2.AutoCompleteRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


//Class created to generate the swagger documentation of v1 RestServices
@Path("/autocomplete")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class AutoCompleteRestServiceImpl implements AutoCompleteRestService {

	private final AutoCompleteFacade autoCompleteFacade;

	public AutoCompleteRestServiceImpl(final AutoCompleteFacade autoCompleteFacade) {
		super();
		this.autoCompleteFacade = autoCompleteFacade;
	}

	@Path("/{pattern}")
	@GET
	@Operation(summary = "Perform search.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AutoCompleteResultDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<AutoCompleteResultDto> autoComplete(
			@Parameter(description = "The pattern.", required = true)
				@PathParam("pattern") String pattern,
			@Parameter(description = "The search type.", required = true)
				@QueryParam("type") String type,
			@Parameter(description = "If your are looking for thread members, you must fill this parameter.", required = false)
				@QueryParam("threadUuid") String threadUuid) {
		Validate.notEmpty(type, "Query param named type is required.");
		Validate.notEmpty(pattern, "Pattern must be set.");
		return autoCompleteFacade.search(pattern, type, threadUuid);
	}
}
