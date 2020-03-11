/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
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

	private final ShareFacade shareFacade;

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
		return shareFacade.getReceivedShares();
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
		return shareFacade.getReceivedShare(receivedShareUuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a received share entry.")
	@Override
	public void head(@Parameter(description = "The received share uuid.", required = true) @PathParam("uuid") String receivedShareUuid) throws BusinessException {
		shareFacade.getReceivedShare(receivedShareUuid);
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
		ShareDto receivedShareDto = shareFacade.getReceivedShare(receivedShareUuid);
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
		ShareDto receivedShareDto = shareFacade.getReceivedShare(uuid);
		ByteSource receivedShareStream = shareFacade.getDocumentByteSource(uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(receivedShareStream,
				receivedShareDto.getName(), receivedShareDto.getType(), receivedShareDto.getSize());
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

