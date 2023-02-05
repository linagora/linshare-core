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

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestEntryFacade;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.delegationv2.UploadRequestEntryRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/{actorUuid}/upload_request_entries")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestEntryRestServiceImpl implements UploadRequestEntryRestService {

	protected final Logger logger = LoggerFactory.getLogger(UploadRequestEntryRestServiceImpl.class);

	private final UploadRequestEntryFacade uploadRequestEntryFacade;
	
	public UploadRequestEntryRestServiceImpl(UploadRequestEntryFacade uploadRequestEntryFacade) {
		super();
		this.uploadRequestEntryFacade = uploadRequestEntryFacade;
	}

	@Path("/{uuid}/download")
	@GET
	@Operation(summary = "Download a file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response download(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Upload request entry uuid.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		UploadRequestEntryDto uploadRequestEntryDto = uploadRequestEntryFacade.find(2, actorUuid, uuid);
		ByteSource documentStream = uploadRequestEntryFacade.download(actorUuid, uuid);
		FileAndMetaData data = new FileAndMetaData(documentStream, uploadRequestEntryDto.getSize(),
				uploadRequestEntryDto.getName(), uploadRequestEntryDto.getType());
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return response.build();
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestEntryDto delete(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Upload request entry uuid.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return uploadRequestEntryFacade.delete(actorUuid, uuid);
	}

	@GET
	@Path("/{uploadRequestEntryUuid}/audit")
	@Operation(summary = "Get all traces for a given Upload Request entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAllAudits(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The upload request entry uuid.", required = true)
				@PathParam("uploadRequestEntryUuid") String uploadRequestEntryUuid,
			@Parameter(description = "Optional. If you want to filter the result with action", required = false)
				@QueryParam("actions") List<LogAction> actions)
			throws BusinessException {
		return uploadRequestEntryFacade.findAllAudits(actorUuid, uploadRequestEntryUuid, actions);
	}
}
