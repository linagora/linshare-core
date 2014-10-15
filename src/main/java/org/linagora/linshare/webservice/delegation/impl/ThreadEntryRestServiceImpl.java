/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

package org.linagora.linshare.webservice.delegation.impl;

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

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ThreadEntryDto;
import org.linagora.linshare.core.facade.webservice.delegation.ThreadEntryFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegation.ThreadEntryRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{ownerUuid}/threads/{threadUuid}/entries")
@Api(value = "/rest/delegation/{ownerUuid}/threads/{threadUuid}/entries", basePath = "/rest/threads/{threadUuid}/entries",
	description = "thread entries service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ThreadEntryRestServiceImpl extends WebserviceBase implements
		ThreadEntryRestService {

	private final ThreadEntryFacade threadEntryFacade;

	public ThreadEntryRestServiceImpl(ThreadEntryFacade threadEntryFacade) {
		super();
		this.threadEntryFacade = threadEntryFacade;
	}

	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Create a thread entry which will contain the uploaded file.", response = ThreadEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadEntryDto create(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "File stream.", required = true) InputStream theFile,
			@ApiParam(value = "An optional description of a thread entry.") String description,
			@ApiParam(value = "The given file name of the uploaded file.", required = true) String givenFileName,
			MultipartBody body)
					throws BusinessException {
		String fileName;

		if (theFile == null) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing file (check parameter file)");
		}
		if (givenFileName == null || givenFileName.isEmpty()) {
			// parameter givenFileName is optional
			// so need to search this information in the header of the
			// attachement (with id file)
			fileName = body.getAttachment("file").getContentDisposition().getParameter("filename");
		} else {
			fileName = givenFileName;
		}
		return threadEntryFacade.create(ownerUuid, threadUuid, theFile,
				StringUtils.defaultString(description), fileName);
	}

	@Path("/copy")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Create a thread entry which will contain the uploaded file.", response = ThreadEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadEntryDto copy(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The document entry uuid.", required = true) @PathParam("entryUuid")  String entryUuid)
					throws BusinessException {
		return threadEntryFacade.copy(ownerUuid, threadUuid, entryUuid);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Get a thread entry.", response = ThreadEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadEntryDto find(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return threadEntryFacade.find(ownerUuid, threadUuid, uuid);
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Get all thread entries.", response = ThreadEntryDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public List<ThreadEntryDto> findAll(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid)
					throws BusinessException {
		return threadEntryFacade.findAll(ownerUuid, threadUuid);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a thread entry.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry to delete.", required = true) ThreadEntryDto threadEntry)
					throws BusinessException {
		threadEntryFacade.delete(ownerUuid, threadUuid, threadEntry);
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a thread entry.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry uuid to delete.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		threadEntryFacade.delete(ownerUuid, threadUuid, uuid);
	}

	@Path("/{ownerUuid}/documents/{uuid}/download")
	@GET
	@ApiOperation(value = "Download a file.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Response download(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return threadEntryFacade.download(ownerUuid, threadUuid, uuid);
	}

	@Path("/{ownerUuid}/documents/{uuid}/thumbnail")
	@GET
	@ApiOperation(value = "Download the thumbnail of a file.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Response thumbnail(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return threadEntryFacade.thumbnail(ownerUuid, threadUuid, uuid);
	}

}
