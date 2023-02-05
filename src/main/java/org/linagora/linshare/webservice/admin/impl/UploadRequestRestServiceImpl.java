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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UploadRequestFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestCriteriaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestHistoryDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.UploadRequestRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/upload_requests")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestRestServiceImpl extends WebserviceBase implements
		UploadRequestRestService {

	private final UploadRequestFacade uploadRequestFacade;

	public UploadRequestRestServiceImpl(
			final UploadRequestFacade uploadRequestFacade) {
		this.uploadRequestFacade = uploadRequestFacade;
	}

	@Path("/history/{requestUuid}")
	@GET
	@Operation(summary = "Search all history entries for an upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestHistoryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<UploadRequestHistoryDto> findAll(
			@PathParam(value = "requestUuid") String requestUuid)
			throws BusinessException {
		return uploadRequestFacade.findAllHistory(requestUuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Search all upload request by criteria.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestHistoryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<UploadRequestDto> findAllByCriteria(
			@Parameter(description = "Criteria to search for.", required = true) UploadRequestCriteriaDto dto)
			throws BusinessException {
		return uploadRequestFacade.findAll(dto.getStatus(), dto.getAfterDate(),
				dto.getBeforeDate());
	}
}
