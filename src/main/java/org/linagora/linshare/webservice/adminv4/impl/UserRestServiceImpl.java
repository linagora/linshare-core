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
package org.linagora.linshare.webservice.adminv4.impl;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AutocompleteFacade;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.adminv4.UserRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/users")
public class UserRestServiceImpl extends org.linagora.linshare.webservice.admin.impl.UserRestServiceImpl implements UserRestService {

	public UserRestServiceImpl(UserFacade userFacade, AutocompleteFacade autocompleteFacade) {
		super(userFacade, autocompleteFacade);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a user.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UserDto find(
			@Parameter(description = "User uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return userFacade.findUser(uuid, Version.V4);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update an user.")
	@Override
	public UserDto update(
			@Parameter(description = "User to update", required = true) UserDto userDto)
			throws BusinessException {
		return userFacade.update(userDto, Version.V4);
	}
}
