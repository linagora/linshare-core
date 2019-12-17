/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
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

package org.linagora.linshare.webservice.uploadrequestv2.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.webservice.uploadrequestv2.UploadRequestRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/requests")
@Api(value = "/rest/uploadrequest/v2/requests", description = "requests API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestRestServiceImpl implements UploadRequestRestService {

	protected final Logger logger = LoggerFactory
			.getLogger(UploadRequestRestServiceImpl.class);

	private final UploadRequestUrlFacade uploadRequestUrlFacade;

	public UploadRequestRestServiceImpl(
			UploadRequestUrlFacade uploadRequestUrlFacade) {
		super();
		this.uploadRequestUrlFacade = uploadRequestUrlFacade;
	}

	@GET
	@Path("/{uuid}")
	@ApiOperation(value = "Find an upload request.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
					@ApiResponse(code = 404, message = "UploadRequest not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
		})
	@Override
	public Response find(@PathParam(value = "uuid") String uuid,
			@HeaderParam("linshare-uploadrequest-password") String password)
			throws BusinessException {
		logger.debug("uuid : " + uuid);
		logger.debug("password : " + password);

		UploadRequestDto data = uploadRequestUrlFacade.find(uuid, password);
		ResponseBuilder response = Response.ok(data);
		// Fixing IE cache issue.
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		response.cacheControl(cc);
		return response.build();
	}

	@PUT
	@Path("/{requestUrlUuid}")
	@ApiOperation(value = "Update an upload request.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
					@ApiResponse(code = 404, message = "UploadRequest not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
		})
	@Override
	public UploadRequestDto close(@PathParam(value = "requestUrlUuid") String requestUrlUuid,
			@HeaderParam("linshare-uploadrequest-password") String password)
			throws BusinessException {
		return uploadRequestUrlFacade.close(requestUrlUuid, password);
	}

	@DELETE
	@Path("/{requestUrlUuid}/entries/{entryUuid : .*}")
	@ApiOperation(value = "Delete an entry in an upload request.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
					@ApiResponse(code = 404, message = "UploadRequest not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
		})
	public void delete(
			@ApiParam(value = "UploadRequestUrl uuid that contains the uploadRequestEntry to delete.", required = true)
				@PathParam(value = "requestUrlUuid") String requestUrlUuid,
			@HeaderParam("linshare-uploadrequest-password") String password,
			@ApiParam(value = "UploadRequestEntry uuid to delete.", required = false)
				@PathParam(value = "entryUuid") String entryUuid,
			@ApiParam(value = "UploadRequest entry to delete. ", required = false) EntryDto entry)
			throws BusinessException {
		uploadRequestUrlFacade.deleteUploadRequestEntry(requestUrlUuid, password, entryUuid, entry);
	}
}
