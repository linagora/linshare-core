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
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.WelcomeMessagesFacade;
import org.linagora.linshare.webservice.userv2.WelcomeMessagesRestService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/welcome_messages")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WelcomeMessagesRestServiceImpl implements WelcomeMessagesRestService {

	protected WelcomeMessagesFacade welcomeMessagesFacade;

	public WelcomeMessagesRestServiceImpl(WelcomeMessagesFacade welcomeMessagesFacade) {
		super();
		this.welcomeMessagesFacade = welcomeMessagesFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all welcome message entries.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = List.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<Map<SupportedLanguage, String>>  findAll() throws BusinessException {
		return welcomeMessagesFacade.findAll(null);
	}

}
