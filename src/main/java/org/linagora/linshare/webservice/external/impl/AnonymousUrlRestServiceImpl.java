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
package org.linagora.linshare.webservice.external.impl;

import com.google.common.io.ByteSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.facade.webservice.external.AnonymousUrlFacade;
import org.linagora.linshare.core.facade.webservice.external.dto.AnonymousUrlDto;
import org.linagora.linshare.core.facade.webservice.external.dto.ShareEntryDto;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.webservice.external.AnonymousUrlRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;


@Path("/anonymousurl")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class AnonymousUrlRestServiceImpl implements AnonymousUrlRestService{

	private final AnonymousUrlFacade anonymousUrlFacade;

	private final String noPasswordCookieValue;

	public AnonymousUrlRestServiceImpl(AnonymousUrlFacade anonymousUrlFacade, String noPasswordCookieValue) {
		super();
		this.anonymousUrlFacade = anonymousUrlFacade;
		this.noPasswordCookieValue = noPasswordCookieValue;
	}

	@GET
	@Path("/{urlUuid}")
	@Operation(summary = "Find an anonymous Url", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AnonymousUrlDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response getAnonymousUrl(
			@PathParam(value = "urlUuid") String urlUuid,
			@Context HttpHeaders headers) {
		String password = loadPasswordFromCookie(urlUuid, headers);
		AnonymousUrlDto anonymousUrl = anonymousUrlFacade.find(urlUuid, password);
		NewCookie cookie = new NewCookie(anonymousUrl.getUuid(), password);
		Response.ResponseBuilder rb = Response.ok(anonymousUrl);
		return rb.cookie(cookie).build();
	}

	@GET
	@Path("/{urlUuid}/{shareEntryUuid}")
	@Operation(summary = "Get an AnonymousShareEntry")
	@Override
	public ShareEntryDto getAnonymousShareEntry(
			@PathParam(value = "urlUuid") String anonymousUrlUuid,
			@PathParam(value = "shareEntryUuid") String shareEntryUuid,
			@Context HttpHeaders headers) {
		String password = loadPasswordFromCookie(anonymousUrlUuid, headers);
		return anonymousUrlFacade
				.getShareEntry(anonymousUrlUuid, shareEntryUuid, password);
	}

	@GET
	@Path("/{urlUuid}/{shareEntryUuid}/download")
	@Operation(summary = "Download a document")
	@Override
	public Response download(@PathParam(value = "urlUuid") String urlUuid,
			@PathParam(value = "shareEntryUuid") String shareEntryUuid,
			@Context HttpHeaders headers) {
		String password = loadPasswordFromCookie(urlUuid, headers);
		ShareEntryDto shareEntry = anonymousUrlFacade.getShareEntry(urlUuid,
				shareEntryUuid, password);
		ByteSource documentStream = anonymousUrlFacade.download(urlUuid,
				shareEntryUuid, password);
		FileAndMetaData data = new FileAndMetaData(documentStream, shareEntry.getSize(),
				shareEntry.getName(), shareEntry.getType());
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(data);
		return response.build();
	}

	@GET
	@Path("/{urlUuid}/{shareEntryUuid}/thumbnail{kind:(small)?|(medium)?|(large)?|(pdf)?}")
	@Operation(summary = "Get document thumbnail")
	@Override
	public Response getAnonymousShareEntryThumbnail(
			@PathParam(value = "urlUuid") String anonymousUrlUuid,
			@PathParam(value = "shareEntryUuid") String shareEntryUuid,
			@PathParam(value = "kind") ThumbnailType thumbnailType,
			@QueryParam("base64") @DefaultValue("false") boolean base64,
			@Context HttpHeaders headers) {
		String password = loadPasswordFromCookie(anonymousUrlUuid, headers);
		ShareEntryDto shareEntry = anonymousUrlFacade.getShareEntry(anonymousUrlUuid, shareEntryUuid, password);
		ByteSource byteSource = anonymousUrlFacade.getThumbnail(anonymousUrlUuid, shareEntryUuid, password,
				thumbnailType);
		ResponseBuilder response = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(byteSource,
				shareEntry.getName(), base64, thumbnailType);
		return response.build();
	}

	protected String loadPasswordFromCookie(String urlUuid, HttpHeaders headers) {
		String headerPassword = headers.getHeaderString("linshare-anonymousurl-password");
		if (headerPassword != null) {
			return headerPassword;
		}

		Cookie cookie = headers.getCookies().get(urlUuid);
		if (cookie == null) {
			return noPasswordCookieValue;
		}

		String cookiePassword = cookie.getValue();
		if (cookiePassword == null) {
			return noPasswordCookieValue;
		}

		return cookiePassword;
	}
}
