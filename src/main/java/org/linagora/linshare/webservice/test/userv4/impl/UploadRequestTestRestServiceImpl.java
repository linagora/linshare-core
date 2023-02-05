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
package org.linagora.linshare.webservice.test.userv4.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.UploadRequestTestFacade;
import org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.webservice.test.userv4.UploadRequestTestRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/upload_request_groups")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestTestRestServiceImpl implements UploadRequestTestRestService {

	protected final Logger logger = LoggerFactory.getLogger(UploadRequestTestRestServiceImpl.class);

	private final UploadRequestTestFacade uploadRequestFacade;

	public UploadRequestTestRestServiceImpl(UploadRequestTestFacade uploadRequestFacade) {
		super();
		this.uploadRequestFacade = uploadRequestFacade;
	}

	@GET
	@Path("/{uuid}/upload_requests")
	@Operation(summary = "Find a list of upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<UploadRequestDto> findAllUploadRequestsURl(
			@Parameter(description = "Upload request group uuid.", required = true)
				@PathParam(value = "uuid") String uuid) {
		return uploadRequestFacade.findAllUploadRequestsURl(null, uuid);
	}
}
