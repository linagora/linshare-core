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
import org.linagora.linshare.core.facade.webservice.admin.MailFooterLangFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailFooterLangDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailFooterLangRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/mail_footer_langs")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailFooterLangRestServiceImpl extends WebserviceBase implements
		MailFooterLangRestService {

	private final MailFooterLangFacade mailFooterLangFacade;

	public MailFooterLangRestServiceImpl(
			final MailFooterLangFacade mailFooterLangFacade) {
		super();
		this.mailFooterLangFacade = mailFooterLangFacade;
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a mail footer lang.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailFooterLangDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailFooterLangDto find(
			@Parameter(description = "Mail footer lang's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mailFooterLangFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a mail footer lang.")
	@Override
	public void head(
			@Parameter(description = "Mail footer lang's uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		mailFooterLangFacade.find(uuid);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a mail footer lang.")
	@Override
	public MailFooterLangDto update(
			@Parameter(description = "Mail footer lang to update.", required = true) MailFooterLangDto dto)
			throws BusinessException {
		return mailFooterLangFacade.update(dto);
	}
}
