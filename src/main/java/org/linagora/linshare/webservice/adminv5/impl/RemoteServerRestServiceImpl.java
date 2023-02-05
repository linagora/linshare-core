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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.RemoteServerFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractServerDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPServerDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.TwakeServerDto;
import org.linagora.linshare.webservice.adminv5.RemoteServerRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/remote_servers")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class RemoteServerRestServiceImpl implements RemoteServerRestService {

	private final RemoteServerFacade ldapServerFacade;

	
	protected RemoteServerRestServiceImpl(
			RemoteServerFacade lDAPServerFacade) {
		super();
		this.ldapServerFacade = lDAPServerFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all remote servers.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(
				schema = @Schema(
					oneOf = {LDAPServerDto.class, TwakeServerDto.class},
					type = "object"
				))
			),
			responseCode = "200"
		)
	})
	@Override
	public List<AbstractServerDto> findAll() throws BusinessException {
		return ldapServerFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a remote server.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(
				schema = @Schema(
					oneOf = {LDAPServerDto.class, TwakeServerDto.class},
					type = "object"
				))
			),
			responseCode = "200"
		)
	})
	@Override
	public AbstractServerDto find(
			@Parameter(description = "Remote server's uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return ldapServerFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a remote server.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(
				schema = @Schema(
					oneOf = {LDAPServerDto.class, TwakeServerDto.class},
					type = "object"
				))
			),
			responseCode = "200"
		)
	})
	@Override
	public AbstractServerDto create(
			@Parameter(description = "The remote server to create.", required = true) AbstractServerDto serverDto)
			throws BusinessException {
		return ldapServerFacade.create(serverDto);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "Update a remote server.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(
				schema = @Schema(
					oneOf = {LDAPServerDto.class, TwakeServerDto.class},
					type = "object"
				))
			),
			responseCode = "200"
		)
	})
	@Override
	public AbstractServerDto update(
			@Parameter(description = "Remote server to update", required = true) AbstractServerDto serverDto,
			@Parameter(description = "Remote server's uuid to update, if null object is used", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return ldapServerFacade.update(uuid, serverDto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "Delete a remote server.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(
				schema = @Schema(
					oneOf = {LDAPServerDto.class, TwakeServerDto.class},
					type = "object"
				))
			),
			responseCode = "200"
		)
	})
	@Override
	public AbstractServerDto delete(
			@Parameter(description = "Remote server to delete", required = false) AbstractServerDto serverDto,
			@Parameter(description = "Remote server's uuid to delete, if null object is used", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return ldapServerFacade.delete(uuid, serverDto);
	}

	@Override
	@Path("/{uuid}/domains")
	@GET
	@Operation(summary = "Find all domains using a chosen Remote server.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	public List<DomainDto> findAllDomainsByRemoteServer(
			@Parameter(description = "LDAP server's uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return ldapServerFacade.findAllDomainsByRemoteServer(uuid);
	}
}
