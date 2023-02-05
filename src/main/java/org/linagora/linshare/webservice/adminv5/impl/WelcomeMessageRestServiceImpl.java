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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.WelcomeMessageFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.WelcomeMessageRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/welcome_messages")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class WelcomeMessageRestServiceImpl extends WebserviceBase implements WelcomeMessageRestService {

	private final WelcomeMessageFacade welcomeMessageFacade;

	public WelcomeMessageRestServiceImpl(WelcomeMessageFacade welcomeMessageFacade) {
		this.welcomeMessageFacade = welcomeMessageFacade;
	}

	@Path("/{uuid}/domains")
	@GET
	@Operation(summary = "It will return the domains associated to this welcome message.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(implementation = DomainDto.class)
			)
		)
	})
	@Override
	public List<DomainDto> associatedDomains(
		@Parameter(description = "welcomeMessage's uuid.", required = true)
			@PathParam("uuid") String welcomeMessageUuid) throws BusinessException {
		return welcomeMessageFacade.associatedDomains(welcomeMessageUuid);
	}
}
