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
package org.linagora.linshare.webservice.external.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ResetPasswordDto;
import org.linagora.linshare.core.facade.webservice.external.ResetGuestPasswordFacade;
import org.linagora.linshare.core.facade.webservice.external.dto.AnonymousUrlDto;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.webservice.external.ResetGuestPasswordRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/reset_password")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ResetGuestPasswordRestServiceImpl implements ResetGuestPasswordRestService {

	protected ResetGuestPasswordFacade facade;

	public ResetGuestPasswordRestServiceImpl(ResetGuestPasswordFacade facade) {
		super();
		this.facade = facade;
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Find an anonymous Url", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AnonymousUrlDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ResetGuestPassword find(
			@Parameter(description = "uuid", required = true) @PathParam(value = "uuid") String uuid) throws BusinessException {
		return facade.find(uuid);
	}

	@PUT
	@Path("/{uuid}")
	@Override
	public ResetGuestPassword update(
			@Parameter(description = "uuid", required = true) @PathParam(value = "uuid") String uuid,
			ResetGuestPassword reset) throws BusinessException {
		return facade.update(uuid, reset);
	}

	@POST
	@Path("/")
	@Override
	public void create(
			@HeaderParam("domain-uuid") String domainUuid, ResetPasswordDto resetDto) throws BusinessException {
		facade.create(domainUuid, resetDto);
	}

}
