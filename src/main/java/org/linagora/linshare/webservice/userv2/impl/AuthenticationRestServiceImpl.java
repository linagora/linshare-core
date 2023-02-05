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

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.JwtToken;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.core.facade.webservice.user.SecondFactorAuthenticationFacade;
import org.linagora.linshare.core.facade.webservice.user.UserFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;
import org.linagora.linshare.core.facade.webservice.user.dto.VersionDto;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.AuthenticationRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/authentication")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class AuthenticationRestServiceImpl extends WebserviceBase implements AuthenticationRestService {

	protected final UserFacade userFacade;

	protected final GuestFacade guestFacade;

	protected final SecondFactorAuthenticationFacade secondFactorAuthenticationFacade;

	public AuthenticationRestServiceImpl(
			final UserFacade userFacade,
			final SecondFactorAuthenticationFacade secondFactorAuthenticationFacade,
			final GuestFacade guestFacade) {
		this.userFacade = userFacade;
		this.secondFactorAuthenticationFacade = secondFactorAuthenticationFacade;
		this.guestFacade = guestFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "No operation.")
	@Override
	public void noop() {
		return; // do nothing
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
		return userFacade.isAuthorized(Version.V2);
	}

	@Path("/logout")
	@GET
	@Operation(summary = "Logout the current user.")
	@Override
	public void logout() {
// This code is never reach because the URL will be catch by spring
// security before.
// This function was created just to show the logout URL into WADL.
	}

	@Path("/version")
	@GET
	@Override
	public VersionDto getVersion() {
		return new VersionDto(getCoreVersion());
	}

	@Path("/jwt")
	@GET
	@Override
	public JwtToken generateToken() throws BusinessException {
		String token = userFacade.generateToken();
		return new JwtToken(token);
	}


	@Path("/password")
	@POST
	@Operation(summary = "Change the password of the current guest.")
	@Override
	public void changePassword(
			@Parameter(description = "The password to update.", required = true)
			PasswordDto password) throws BusinessException {
		guestFacade.changePassword(password);
	}

	@Path("/password")
	@GET
	@Operation(summary = "Get the password validation rules.")
	@Override
	public Map<String, Integer> getPasswordRules() throws BusinessException {
		return guestFacade.getPasswordRules();
	}

	@Path("/2fa/{uuid}")
	@GET
	@Operation(summary = "Get current 2FA state. use account uuid as 2FA uuid.", responses = {
		@ApiResponse(
			content = @Content(schema = @Schema(implementation = SecondFactorDto.class)),
			responseCode = "200"
		)
	})
	@Override
	public SecondFactorDto get2FA(
			@Parameter(description = "Required 2FA's uuid to get (aka account uuid).", required = true)
			@PathParam("uuid") String uuid
			) {
		return secondFactorAuthenticationFacade.find(uuid);
	}

	@Path("/2fa")
	@POST
	@Operation(summary = "Enable 2FA. A shared key will be computend and returned. This is the one and only time the shared key will be returned.", responses = {
			@ApiResponse(
				content = @Content(schema = @Schema(implementation = SecondFactorDto.class)),
				responseCode = "200"
			)
		})
	@Override
	public SecondFactorDto create2FA(SecondFactorDto sfd) {
		return secondFactorAuthenticationFacade.create(sfd);
	}

	@Path("/2fa/{uuid : .*}")
	@DELETE
	@Operation(summary = "Disable 2FA. Shared key will be remvoved.", responses = {
			@ApiResponse(
				content = @Content(schema = @Schema(implementation = SecondFactorDto.class)),
				responseCode = "200"
			)
		})
	@Override
	public SecondFactorDto delete2FA(
			@Parameter(description = "Optional 2FA 's uuid to delete.", required = true)
			@PathParam("uuid") String uuid,
			@Parameter(description = "Optional 2FA object to delete.", required = true)
			SecondFactorDto sfd) {
		return secondFactorAuthenticationFacade.delete(uuid, sfd);
	}
}
