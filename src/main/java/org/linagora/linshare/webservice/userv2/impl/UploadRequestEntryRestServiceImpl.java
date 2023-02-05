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
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestEntryFacade;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.userv2.UploadRequestEntryRestService;
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


@Path("/upload_request_entries")
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
			@Parameter(description = "Upload request entry uuid.", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		UploadRequestEntryDto uploadRequestEntryDto = uploadRequestEntryFacade.find(2, null, uuid);
		ByteSource documentStream = uploadRequestEntryFacade.download(null, uuid);
		FileAndMetaData data = new FileAndMetaData(documentStream, uploadRequestEntryDto.getSize(), uploadRequestEntryDto.getName(), uploadRequestEntryDto.getType());
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
	@Override	public UploadRequestEntryDto delete(@Parameter(description = "Upload request entry uuid.", required = true)  @PathParam("uuid") String uuid) throws BusinessException {
		return uploadRequestEntryFacade.delete(null, uuid);
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
			@Parameter(description = "The upload request entry uuid.", required = true)
				@PathParam("uploadRequestEntryUuid") String uploadRequestEntryUuid,
			@Parameter(description = "Optional. If you want to filter the result with action", required = false)
				@QueryParam("actions") List<LogAction> actions)
			throws BusinessException {
		return uploadRequestEntryFacade.findAllAudits(null, uploadRequestEntryUuid, actions);
	}

	@Path("/{uuid}/thumbnail{kind:(small)?|(medium)?|(large)?|(pdf)?}")
	@GET
	@Operation(summary = "Download the thumbnail of a choosen upload request entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response thumbnail(
			@Parameter(description = "The upload requestEntry's uuid to which we will get the thumbnail.", required = true)
				@PathParam("uuid") String uploadRequestEntryUuid,
			@Parameter(description = "This parameter allows you to choose which thumbnail you want : Small, Medium or Large. Default value is Medium", required = false)
				@PathParam("kind") ThumbnailType thumbnailType,
			@Parameter(description = "True to get an encoded base 64 response", required = false)
				@QueryParam("base64") @DefaultValue("false") boolean base64)
					throws BusinessException {
		return uploadRequestEntryFacade.thumbnail(null, uploadRequestEntryUuid, base64, thumbnailType);
	}
}
