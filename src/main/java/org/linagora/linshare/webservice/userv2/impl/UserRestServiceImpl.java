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
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.user.AutoCompleteFacade;
import org.linagora.linshare.core.facade.webservice.user.UserFacade;
import org.linagora.linshare.webservice.userv2.UserRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/users")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserRestServiceImpl implements UserRestService {

	private final UserFacade webServiceUserFacade;

	private final AutoCompleteFacade autocompleteFacade;

	public UserRestServiceImpl(final UserFacade webServiceUserFacade, final AutoCompleteFacade autocompleteFacade) {
		this.webServiceUserFacade = webServiceUserFacade;
		this.autocompleteFacade = autocompleteFacade;
	}

	@Path("/")
	@GET
	@Override
	public List<UserDto> findAll() throws BusinessException {
		return webServiceUserFacade.findAll();
	}

	@Path("/autocomplete/{pattern}")
	@GET
	@Operation(summary = "Provide user autocompletion.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<UserDto> autocomplete(
			@Parameter(description = "Pattern to complete.", required = true) @PathParam("pattern") String pattern)
					throws BusinessException {
		return autocompleteFacade.findUser(pattern);
	}
}
