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
package org.linagora.linshare.webservice.admin.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailContentLangFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContentLangDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailContentLangRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/mail_content_langs")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailContentLangRestServiceImpl extends WebserviceBase implements
		MailContentLangRestService {

	private final MailContentLangFacade mailContentLangFacade;

	public MailContentLangRestServiceImpl(
			final MailContentLangFacade mailContentLangFacade) {
		super();
		this.mailContentLangFacade = mailContentLangFacade;
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a mail content lang.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContentLangDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailContentLangDto find(
			@Parameter(description = "Mail content lang's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mailContentLangFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a mail content lang.")
	@Override
	public void head(
			@Parameter(description = "Mail content lang's uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		mailContentLangFacade.find(uuid);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a mail content lang.")
	@Override
	public MailContentLangDto update(
			@Parameter(description = "Mail content lang to update.", required = true) MailContentLangDto dto)
			throws BusinessException {
		return mailContentLangFacade.update(dto);
	}
}
