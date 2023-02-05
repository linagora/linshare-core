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
package org.linagora.linshare.webservice.userv4.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.core.facade.webservice.user.SecondFactorAuthenticationFacade;
import org.linagora.linshare.core.facade.webservice.user.UserFacade;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.userv4.AuthenticationRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/authentication")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class AuthenticationRestServiceImpl extends org.linagora.linshare.webservice.userv2.impl.AuthenticationRestServiceImpl implements AuthenticationRestService {

	public AuthenticationRestServiceImpl(UserFacade userFacade,
			SecondFactorAuthenticationFacade secondFactorAuthenticationFacade, GuestFacade guestFacade) {
		super(userFacade, secondFactorAuthenticationFacade, guestFacade);
	}

	@Path("/authorized")
	@GET
	@Operation(summary = "Check if user is authorized.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UserDto isAuthorized() throws BusinessException {
		return userFacade.isAuthorized(Version.V4);
	}

}
