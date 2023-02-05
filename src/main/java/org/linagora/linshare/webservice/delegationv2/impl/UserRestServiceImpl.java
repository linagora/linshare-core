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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDetailsDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.delegation.UserFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegationv2.UserRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/users")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UserRestServiceImpl extends WebserviceBase implements
		UserRestService {

	private final UserFacade userFacade;

	public UserRestServiceImpl(
			final UserFacade userFacade) {
		super();
		this.userFacade = userFacade;
	}

	@Path("/{mail}")
	@GET
	@Operation(summary = "Find a user.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = GenericUserDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public GenericUserDto getUser(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("mail") String mail,
			@Parameter(description = "The actor (user) uuid.", required = false)
				@QueryParam("domainId") String domainId)
			throws BusinessException {
		return userFacade.getUser(mail, domainId);
	}

	@Path("/details/{uuid}")
	@GET
	@Operation(summary = "Find all the user's informations from the given uuid.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UserDto findUser(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return userFacade.findUser(uuid);
	}

	@Path("/details")
	@POST
	@Operation(summary = "Looking for user LinShare account, if it does not exists it will be created from ldap directories.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UserDto findUser(
			@Parameter(description = "User details (mail and domain) to send to recover the whole informations about user", required = true) UserDetailsDto userDetailsDto)
			throws BusinessException {
		return userFacade.findUser(userDetailsDto);
	}
}

