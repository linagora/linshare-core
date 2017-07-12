/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.userv2.ReceivedShareRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;


@Path("/received_shares")
@Api(value = "/rest/user/received_shares", description = "received shares rest service.", produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ReceivedShareRestServiceImpl implements ReceivedShareRestService {

	private final ShareFacade shareFacade;

	public ReceivedShareRestServiceImpl(final ShareFacade shareFacade) {
		this.shareFacade = shareFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all connected user received shares.", response = ShareDto.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Received shares not found."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public List<ShareDto> getReceivedShares() throws BusinessException {
		return shareFacade.getReceivedShares();
	}

	@Path("/{uuid}")
	@GET
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Received share not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@ApiOperation(value = "Find a received share entry.", response = ShareDto.class)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public ShareDto getReceivedShare(
			@ApiParam(value = "The received share uuid.", required = true) @PathParam("uuid") String receivedShareUuid)
					throws BusinessException {
		return shareFacade.getReceivedShare(receivedShareUuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Received share not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@ApiOperation(value = "Find a received share entry.")
	@Override
	public void head(@ApiParam(value = "The received share uuid.", required = true) @PathParam("uuid") String receivedShareUuid) throws BusinessException {
		shareFacade.getReceivedShare(receivedShareUuid);
	}

	@Path("/{uuid}/thumbnail")
	@GET
	@ApiOperation(value = "Download the thumbnail of a file.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Received share not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Response thumbnail(@PathParam("uuid") String receivedShareUuid,
			@ApiParam(value = "True to get an encoded base 64 response", required = false) @QueryParam("base64") @DefaultValue("false") boolean base64) throws BusinessException {
		ShareDto receivedShareDto = shareFacade.getReceivedShare(receivedShareUuid);
		InputStream receivedShareStream = shareFacade.getThumbnailStream(receivedShareUuid);
		ResponseBuilder response = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(receivedShareStream,
				receivedShareDto.getName() + "_thumb.png", base64);
		return response.build();
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a received share.", response = ShareDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Received share not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ShareDto delete(
			@ApiParam(value = "The received share uuid.", required = true) @PathParam("uuid") String receivedShareUuid)
					throws BusinessException {
		return shareFacade.delete(receivedShareUuid, true);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a received share.", response = ShareDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Received share not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ShareDto delete(@ApiParam(value = "The received share to delete.", required = true) ShareDto shareDto)
			throws BusinessException {
		Validate.notNull(shareDto, "Share dto must be set.");
		return shareFacade.delete(shareDto.getUuid(), true);
	}

	@Path("/{uuid}/copy")
	@POST
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Received share not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@ApiOperation(value = "Copy a received share in your personal files.", response = DocumentDto.class)
	@Override
	public DocumentDto copy(
			@ApiParam(value = "The received share uuid.", required = true) @PathParam("uuid") String shareEntryUuid)
					throws BusinessException {
		return shareFacade.copy(shareEntryUuid);
	}

	@Path("/{uuid}/download")
	@GET
	@ApiOperation(value = "Download a received share.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the rights."),
			@ApiResponse(code = 404, message = "Received share not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Response download(
			@ApiParam(value = "The received share uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		ShareDto receivedShareDto = shareFacade.getReceivedShare(uuid);
		InputStream receivedShareStream = shareFacade.getDocumentStream(uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(receivedShareStream,
				receivedShareDto.getName(), receivedShareDto.getType(), receivedShareDto.getSize());
		return response.build();
	}

	@Path("/{uuid}/audit")
	@GET
	@ApiOperation(value = "Get all traces for a received share.", response = AuditLogEntryUser.class, responseContainer="Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Share not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Set<AuditLogEntryUser> findAll(
			@ApiParam(value = "The received share uuid.", required = true)
				@PathParam("uuid") String uuid,
			@ApiParam(value = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<String> actions,
			@ApiParam(value = "Filter by type of resource's types.", required = false)
				@QueryParam("types") List<String> types,
				@QueryParam("beginDate") String beginDate,
				@QueryParam("endDate") String endDate) {
		return shareFacade.findAll(null, uuid, actions, types, beginDate, endDate);
	}
}

