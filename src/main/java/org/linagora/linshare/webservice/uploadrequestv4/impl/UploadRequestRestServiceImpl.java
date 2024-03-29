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
package org.linagora.linshare.webservice.uploadrequestv4.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.webservice.uploadrequestv4.UploadRequestRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/requests")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestRestServiceImpl implements UploadRequestRestService {

	protected final Logger logger = LoggerFactory
			.getLogger(UploadRequestRestServiceImpl.class);

	protected final UploadRequestUrlFacade uploadRequestUrlFacade;

	public UploadRequestRestServiceImpl(
			UploadRequestUrlFacade uploadRequestUrlFacade) {
		super();
		this.uploadRequestUrlFacade = uploadRequestUrlFacade;
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Find an upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
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

	@GET
	@Path("/{uuid}/entries")
	@Operation(summary = "Find all entries of an upload request url.")
	public List<UploadRequestEntryDto> findAllEntries(
			@Parameter(description = "UploadRequestUrl uuid that you want to retrieve its entries.", required = true)
				@PathParam(value = "uuid") String uuid,
			@HeaderParam("linshare-uploadrequest-password") String password)
			throws BusinessException {
		// endpoint upload_request/v5 was never created.
		return uploadRequestUrlFacade.findAllExtEntries(4, uuid, password);
	}

	@PUT
	@Path("/{requestUrlUuid}")
	@Operation(summary = "Update an upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestDto close(@PathParam(value = "requestUrlUuid") String requestUrlUuid,
			@HeaderParam("linshare-uploadrequest-password") String password)
			throws BusinessException {
		return uploadRequestUrlFacade.close(requestUrlUuid, password);
	}

	@DELETE
	@Path("/{requestUrlUuid}/entries/{entryUuid : .*}")
	@Operation(summary = "Delete an entry in an upload request.")
	public UploadRequestEntryDto delete(
			@Parameter(description = "UploadRequestUrl uuid that contains the uploadRequestEntry to delete.", required = true)
				@PathParam(value = "requestUrlUuid") String requestUrlUuid,
			@HeaderParam("linshare-uploadrequest-password") String password,
			@Parameter(description = "UploadRequestEntry uuid to delete.", required = false)
				@PathParam(value = "entryUuid") String entryUuid,
			@Parameter(description = "UploadRequest entry to delete. ", required = false) EntryDto entry)
			throws BusinessException {
		return uploadRequestUrlFacade.deleteUploadRequestEntry(requestUrlUuid, password, entryUuid, entry);
	}

	@Path("/{uploadRequestUrlUuid}/entries/{entryUuid}/thumbnail{kind:(small)?|(medium)?|(large)?|(pdf)?}")
	@GET
	@Operation(summary = "Download the thumbnail of a choosen upload request entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response thumbnail(
			@Parameter(description = "UploadRequestUrl uuid that you want to get thumbnail of its entry.", required = true)
				@PathParam(value = "uploadRequestUrlUuid") String uploadRequestUrlUuid,
			@Parameter(description = "UploadRequestUrl's password, required if uploadRequest is secured.", required = true)
				@HeaderParam("linshare-uploadrequest-password") String password,
			@Parameter(description = "The upload requestEntry's uuid to which we will get the thumbnail.", required = true)
				@PathParam("entryUuid") String uploadRequestEntryUuid,
			@Parameter(description = "This parameter allows you to choose which thumbnail you want : Small, Medium or Large. Default value is Medium", required = false)
				@PathParam("kind") ThumbnailType thumbnailType,
			@Parameter(description = "True to get an encoded base 64 response", required = false)
				@QueryParam("base64") @DefaultValue("false") boolean base64)
					throws BusinessException {
		return uploadRequestUrlFacade.thumbnail(uploadRequestUrlUuid, password, uploadRequestEntryUuid, base64, thumbnailType);
	}

	@Path("/{uploadRequestUrlUuid}/entries/{entryUuid}/download")
	@GET
	@Operation(summary = "Download a choosen upload request entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response download(
			@Parameter(description = "UploadRequestUrl uuid that you want to download its entry.", required = true)
				@PathParam(value = "uploadRequestUrlUuid") String uploadRequestUrlUuid,
			@Parameter(description = "UploadRequestUrl's password, required if uploadRequest is secured.", required = true)
				@HeaderParam("linshare-uploadrequest-password") String password,
			@Parameter(description = "Upload request entry'uuid to download.", required = true)
				@PathParam("entryUuid") String uploadRequestEntryUuid) throws BusinessException {
		return uploadRequestUrlFacade.download(uploadRequestUrlUuid, password, uploadRequestEntryUuid);
	}
}
