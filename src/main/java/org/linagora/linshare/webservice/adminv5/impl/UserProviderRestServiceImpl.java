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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.facade.webservice.adminv5.UserProviderFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractUserProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPUserProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.OIDCUserProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.TwakeGuestUserProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.TwakeUserProviderDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.UserProviderRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/domains/{domainUuid}/user_providers")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class UserProviderRestServiceImpl extends WebserviceBase implements
	UserProviderRestService {

	private final UserProviderFacade userProviderFacade;

	public UserProviderRestServiceImpl(final UserProviderFacade userProviderFacade) {
		this.userProviderFacade = userProviderFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "It will return all user provider of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(array = @ArraySchema(
				schema = @Schema(
					oneOf = {LDAPUserProviderDto.class, OIDCUserProviderDto.class, TwakeUserProviderDto.class, TwakeGuestUserProviderDto.class},
					type = "object"
				))
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
	public Set<AbstractUserProviderDto> findAll(
			@Parameter(description = "The domain uuid.")
			@PathParam("domainUuid") String domain) {
		return userProviderFacade.findAll(domain);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "It will return the detail of an user provider of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPUserProviderDto.class, OIDCUserProviderDto.class, TwakeUserProviderDto.class, TwakeGuestUserProviderDto.class},
					type = "object"
				)
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
	public AbstractUserProviderDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "userProvider's uuid.", required = true)
				@PathParam("uuid") String uuid
				) {
		return userProviderFacade.find(domainUuid, uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "It allows root adminstrator to create a new domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPUserProviderDto.class, OIDCUserProviderDto.class, TwakeUserProviderDto.class, TwakeGuestUserProviderDto.class},
					type = "object"
				)
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
	public AbstractUserProviderDto create(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@RequestBody(description = "UserProvider dto to create", required = true,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPUserProviderDto.class, OIDCUserProviderDto.class, TwakeUserProviderDto.class, TwakeGuestUserProviderDto.class},
						type = "object"
					)
				)
			)
			AbstractUserProviderDto dto) {
		return userProviderFacade.create(domainUuid, dto);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "It allows adminstrator to update an UserProvider.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPUserProviderDto.class, OIDCUserProviderDto.class, TwakeUserProviderDto.class, TwakeGuestUserProviderDto.class},
					type = "object"
				)
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
	public AbstractUserProviderDto update(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "UserProvider's uuid to update, if null, object.uuid is used", required = false)
				@PathParam("uuid") String uuid,
			@RequestBody(description = "UserProvider dto with properties to update", required = true,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPUserProviderDto.class, OIDCUserProviderDto.class, TwakeUserProviderDto.class, TwakeGuestUserProviderDto.class},
						type = "object"
					)
				)
			)
			AbstractUserProviderDto dto) {
		return userProviderFacade.update(domainUuid, uuid, dto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "It allows adminstrator to delete an UserProvider.", responses = {
			@ApiResponse(
				responseCode = "200",
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPUserProviderDto.class, OIDCUserProviderDto.class, TwakeUserProviderDto.class, TwakeGuestUserProviderDto.class},
						type = "object"
					)
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
	public AbstractUserProviderDto delete(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "UserProvider uuid to delete, if null, object.uuid is used", required = false)
				@PathParam("uuid") String uuid,
			@RequestBody(description = "UserProvider uuid to delete, if null, object.uuid is used", required = true,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPUserProviderDto.class, OIDCUserProviderDto.class, TwakeUserProviderDto.class, TwakeGuestUserProviderDto.class},
						type = "object"
					)
				)
			)
			AbstractUserProviderDto dto) {
		return userProviderFacade.delete(domainUuid, uuid, dto);
	}
}
