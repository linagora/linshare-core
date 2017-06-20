/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
package org.linagora.linshare.webservice.external.impl;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.core.facade.webservice.external.AnonymousUrlFacade;
import org.linagora.linshare.core.facade.webservice.external.dto.AnonymousUrlDto;
import org.linagora.linshare.core.facade.webservice.external.dto.ShareEntryDto;
import org.linagora.linshare.webservice.external.AnonymousUrlRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/anonymousurl")
@Api(value = "/rest/external/anonymousurl/", description = "anonymous url API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class AnonymousUrlRestServiceImpl implements AnonymousUrlRestService{

	private final AnonymousUrlFacade anonymousUrlFacade;

	public AnonymousUrlRestServiceImpl(AnonymousUrlFacade anonymousUrlFacade) {
		super();
		this.anonymousUrlFacade = anonymousUrlFacade;
	}

	@GET
	@Path("/{urlUuid}")
	@ApiOperation(value = "Find an anonymous Url", response = AnonymousUrlDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public Response getAnonymousUrl(
			@PathParam(value = "urlUuid") String urlUuid,
			@HeaderParam("linshare-anonymousurl-password") String password,
			@Context HttpHeaders headers) {
		password = loadPasswordFromCookie(urlUuid, password, headers);
		AnonymousUrlDto anonymousUrl = anonymousUrlFacade.find(urlUuid, password);
		NewCookie cookie = new NewCookie(anonymousUrl.getUuid(), password);
		Response.ResponseBuilder rb = Response.ok(anonymousUrl);
		return rb.cookie(cookie).build();
	}

	@GET
	@Path("/{urlUuid}/{shareEntryUuid}")
	@ApiOperation(value = "Get an AnonymousShareEntry")
	@ApiResponses({
		@ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public ShareEntryDto getAnonymousShareEntry(
			@PathParam(value = "urlUuid") String anonymousUrlUuid,
			@PathParam(value = "shareEntryUuid") String shareEntryUuid,
			@HeaderParam("linshare-anonymousurl-password") String password,
			@Context HttpHeaders headers) {
		password = loadPasswordFromCookie(anonymousUrlUuid, password, headers);
		ShareEntryDto shareEntry = anonymousUrlFacade
				.getShareEntry(anonymousUrlUuid, shareEntryUuid, password);
		return shareEntry;
	}

	@GET
	@Path("/{urlUuid}/{shareEntryUuid}/download")
	@ApiOperation(value = "Download a document")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public Response download(@PathParam(value = "urlUuid") String urlUuid,
			@PathParam(value = "shareEntryUuid") String shareEntryUuid,
			@HeaderParam("linshare-anonymousurl-password") String password,
			@Context HttpHeaders headers) {
		password = loadPasswordFromCookie(urlUuid, password, headers);
		ShareEntryDto shareEntry = anonymousUrlFacade.getShareEntry(urlUuid,
				shareEntryUuid, password);
		InputStream documentStream = anonymousUrlFacade.download(urlUuid,
				shareEntryUuid, password);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(documentStream,
						shareEntry.getName(), shareEntry.getType(),
						shareEntry.getSize());
		return response.build();
	}

	@GET
	@Path("/{urlUuid}/{shareEntryUuid}/thumbnail")
	@ApiOperation(value = "Get document thumbnail")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public Response getAnonymousShareEntryThumbnail(
			@PathParam(value = "urlUuid") String anonymousUrlUuid,
			@PathParam(value = "shareEntryUuid") String shareEntryUuid,
			@HeaderParam("linshare-anonymousurl-password") String password,
			@QueryParam("base64") @DefaultValue("false") boolean base64,
			@Context HttpHeaders headers) {
		password = loadPasswordFromCookie(anonymousUrlUuid, password, headers);
		ShareEntryDto shareEntry = anonymousUrlFacade.getShareEntry(anonymousUrlUuid,
				shareEntryUuid, password);
		InputStream documentStream = anonymousUrlFacade
				.getThumbnail(anonymousUrlUuid, shareEntryUuid, password);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getThumbnailResponseBuilder(documentStream,
						shareEntry.getName(), base64);
		return response.build();
	}

	protected String loadPasswordFromCookie(String urlUuid, String password, HttpHeaders headers) {
		if (password == null) {
			Cookie cookiePwd = headers.getCookies().get(urlUuid);
			if (cookiePwd != null) {
				// first time cookie does not exist.
				password = cookiePwd.getValue();
			}
		}
		return password;
	}
}
