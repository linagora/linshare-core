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
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.userv2.ReceivedShareRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.io.ByteSource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;



@Path("/received_shares")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ReceivedShareRestServiceImpl implements ReceivedShareRestService {

	protected final ShareFacade shareFacade;

	public ReceivedShareRestServiceImpl(final ShareFacade shareFacade) {
		this.shareFacade = shareFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all connected user received shares.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public List<ShareDto> getReceivedShares() throws BusinessException {
		return shareFacade.getReceivedShares(Version.V2);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a received share entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public ShareDto getReceivedShare(
			@Parameter(description = "The received share uuid.", required = true) @PathParam("uuid") String receivedShareUuid)
					throws BusinessException {
		return shareFacade.getReceivedShare(Version.V2, receivedShareUuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a received share entry.")
	@Override
	public void head(@Parameter(description = "The received share uuid.", required = true) @PathParam("uuid") String receivedShareUuid) throws BusinessException {
		shareFacade.getReceivedShare(Version.V2, receivedShareUuid);
	}

	@Path("/{uuid}/thumbnail{kind:(small)?|(medium)?|(large)?|(pdf)?}")
	@GET
	@Operation(summary = "Download the thumbnail of a file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response thumbnail(@PathParam("uuid") String receivedShareUuid,
			@Parameter(description = "True to get an encoded base 64 response", required = false) @QueryParam("base64") @DefaultValue("false") boolean base64,
			@Parameter(description = "This parameter allows you to choose which thumbnail you want : Small, Medium or Large. Default value is Medium", required = false) @PathParam("kind") ThumbnailType thumbnailType
			) throws BusinessException {
		ShareDto receivedShareDto = shareFacade.getReceivedShare(Version.V2, receivedShareUuid);
		ByteSource receivedShareStream = shareFacade.getThumbnailByteSource(receivedShareUuid, thumbnailType);
		ResponseBuilder response = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(receivedShareStream,
				receivedShareDto.getName() + ThumbnailType.getFileType(thumbnailType), base64, thumbnailType);
		return response.build();
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a received share.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareDto delete(
			@Parameter(description = "The received share uuid.", required = true) @PathParam("uuid") String receivedShareUuid)
					throws BusinessException {
		return shareFacade.delete(receivedShareUuid, true);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a received share.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareDto delete(@Parameter(description = "The received share to delete.", required = true) ShareDto shareDto)
			throws BusinessException {
		Validate.notNull(shareDto, "Share dto must be set.");
		return shareFacade.delete(shareDto.getUuid(), true);
	}

	@Path("/{uuid}/download")
	@GET
	@Operation(summary = "Download a received share.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response download(
			@Parameter(description = "The received share uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		ShareDto receivedShareDto = shareFacade.getReceivedShare(Version.V2, uuid);
		ByteSource receivedShareStream = shareFacade.getDocumentByteSource(uuid);
		FileAndMetaData data = new FileAndMetaData(receivedShareStream, receivedShareDto.getSize(),
				receivedShareDto.getName(), receivedShareDto.getType());
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return response.build();
	}

	@Path("/{uuid}/audit")
	@GET
	@Operation(summary = "Get all traces for a received share.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAll(
			@Parameter(description = "The received share uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions,
			@Parameter(description = "Filter by type of resource's types.", required = false)
				@QueryParam("types") List<AuditLogEntryType> types,
				@QueryParam("beginDate") String beginDate,
				@QueryParam("endDate") String endDate) {
		return shareFacade.findAll(null, uuid, actions, types, beginDate, endDate);
	}
}

