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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareCreationDto;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.ShareRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.io.ByteSource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


//Class created to generate the swagger documentation of v1 RestServices
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ShareRestServiceImpl extends WebserviceBase implements ShareRestService {

	private final ShareFacade webServiceShareFacade;

	public ShareRestServiceImpl(final ShareFacade facade) {
		this.webServiceShareFacade = facade;
	}

	/**
	 * get the files of the user
	 */
	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Find all shares for an user.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<ShareDto> getShares() throws BusinessException {
		return webServiceShareFacade.getShares(Version.V2);
	}

	@Path("/{uuid}")
	@Operation(summary = "Find a share.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public ShareDto getShare(
			@Parameter(description = "The received share uuid.", required = true) @PathParam("uuid") String shareUuid) {
		return webServiceShareFacade.getShare(Version.V2, shareUuid);
	}

	@Path("/{uuid}")
	@Operation(summary = "Find a share.")
	@HEAD
	@Override
	public void head(@Parameter(description = "The received share uuid.", required = true) @PathParam("uuid") String shareUuid)
			throws BusinessException {
		webServiceShareFacade.getShare(Version.V2, shareUuid);
	}

	@Path("/{uuid}/download")
	@GET
	@Override
	public Response getDocumentStream(@PathParam("uuid") String shareUuid) throws BusinessException {
		ShareDto shareDto = webServiceShareFacade.getReceivedShare(Version.V2, shareUuid);
		ByteSource documentStream = webServiceShareFacade.getDocumentByteSource(shareUuid);
		FileAndMetaData data = new FileAndMetaData(documentStream, shareDto.getSize(),
				shareDto.getName(), shareDto.getType());
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return response.build();
	}

	@Path("/{uuid}/thumbnail")
	@GET
	@Override
	public Response getThumbnailStream(@PathParam("uuid") String shareUuid,
			@Parameter(description = "True to get an encoded base 64 response", required = false) @QueryParam("base64") @DefaultValue("false") boolean base64)
					throws BusinessException {
		ShareDto shareDto = webServiceShareFacade.getShare(Version.V2, shareUuid);
		ByteSource documentStream = webServiceShareFacade.getThumbnailByteSource(shareUuid, ThumbnailType.MEDIUM);
		ResponseBuilder response = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(documentStream,
				shareDto.getName() + "_thumb.png", base64, ThumbnailType.MEDIUM);
		return response.build();
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a share.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<ShareDto> create(ShareCreationDto createDto) throws BusinessException {
		return webServiceShareFacade.create(createDto);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a share document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareDto delete(
			@Parameter(description = "Share's to delete uuid.", required = true) @PathParam("uuid") String shareUuid)
					throws BusinessException {
		return webServiceShareFacade.delete(shareUuid, false);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a share document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareDto delete(
			@Parameter(description = "Share's to delete.", required = true) ShareDto shareDto)
					throws BusinessException {
		Validate.notNull(shareDto, "Share dto must be set.");
		return webServiceShareFacade.delete(shareDto.getUuid(), false);
	}
}
