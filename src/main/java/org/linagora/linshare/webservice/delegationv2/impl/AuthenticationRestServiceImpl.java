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
package org.linagora.linshare.webservice.delegationv2.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.DelegationGenericFacade;
import org.linagora.linshare.core.facade.webservice.delegation.UserFacade;
import org.linagora.linshare.core.facade.webservice.delegation.dto.AccountDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegationv2.AuthenticationRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/authentication")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class AuthenticationRestServiceImpl extends WebserviceBase implements AuthenticationRestService {

	private final DelegationGenericFacade delegationGenericFacade;

	public AuthenticationRestServiceImpl(
			final DelegationGenericFacade delegationFacade) {
		this.delegationGenericFacade = delegationFacade;
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
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountDto.class))),
			responseCode = "200"
		)
	})

	@Override
	public AccountDto isAuthorized() throws BusinessException {
		return delegationGenericFacade.isAuthorized();
	}

	@Path("/logout")
	@GET
	@Operation(summary = "Logout the current user.")
	@Override
	public void logout() {
		// This code is never reach because the URL will be catch by spring security before.
		// This function was created just to show the logout URL into WADL.
	}

	@Path("/version")
	@GET
	@Override
	public String getVersion() {
		return getCoreVersion();
	}
}

