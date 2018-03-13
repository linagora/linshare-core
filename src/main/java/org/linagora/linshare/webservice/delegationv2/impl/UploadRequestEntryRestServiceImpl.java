/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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

package org.linagora.linshare.webservice.delegationv2.impl;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestEntryFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.webservice.delegationv2.UploadRequestEntryRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{actorUuid}/upload_request_entries")
@Api(value = "/rest/delegation/v2/{actorUuid}/upload_request_entry", description = "requests API")
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
	@ApiOperation(value = "Download a file.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Response download(
			@ApiParam(value = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Upload request entry uuid.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		UploadRequestEntryDto uploadRequestEntryDto = uploadRequestEntryFacade.find(actorUuid, uuid);
		InputStream documentStream = uploadRequestEntryFacade.download(uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(documentStream,
				uploadRequestEntryDto.getName(), uploadRequestEntryDto.getType(), uploadRequestEntryDto.getSize());
		return response.build();
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a file.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public UploadRequestEntryDto delete(
			@ApiParam(value = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Upload request entry uuid.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return uploadRequestEntryFacade.delete(actorUuid, uuid);
	}

	@Path("/{uuid}/copy")
	@POST
	@ApiOperation(value = "Copy a document from an existing upload resquest.", response = DocumentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<DocumentDto> copy(
			@ApiParam(value = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Copy document.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return uploadRequestEntryFacade.copy(null, uuid);
	}
}
