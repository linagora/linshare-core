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
package org.linagora.linshare.webservice.test.userv5.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.test.user.guest.GuestTestFacade;
import org.linagora.linshare.core.facade.webservice.test.user.guest.dto.GuestPasswordDto;
import org.linagora.linshare.webservice.test.userv5.GuestTestRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/guest")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class GuestTestRestServiceImpl implements GuestTestRestService {

	private final GuestTestFacade guestTestFacade;

	public GuestTestRestServiceImpl(GuestTestFacade guestTestFacade) {
		super();
		this.guestTestFacade = guestTestFacade;
	}

	@PUT
	@Path("/{uuid}/password")
	@Operation(summary = "Update a guest password.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(implementation = GuestDto.class)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public GuestDto setPassword(
		@Parameter(description = "Guest's uuid.", required = true)
			@PathParam("uuid") String uuid,
		@Parameter(description = "Guest's password.", required = true) GuestPasswordDto dto) throws BusinessException {
		return guestTestFacade.setPassword(uuid, dto);
	}
}
