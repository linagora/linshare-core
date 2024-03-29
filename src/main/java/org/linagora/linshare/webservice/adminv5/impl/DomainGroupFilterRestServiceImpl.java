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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainGroupFilterFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractGroupFilterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPWorkGroupFilterDto;
import org.linagora.linshare.webservice.adminv5.DomainGroupFilterRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/group_filters")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class DomainGroupFilterRestServiceImpl implements DomainGroupFilterRestService {

	private final DomainGroupFilterFacade domainGroupFilterFacade;

	public DomainGroupFilterRestServiceImpl(
			final DomainGroupFilterFacade domainGroupFilterFacade) {
		this.domainGroupFilterFacade = domainGroupFilterFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all domain group filters.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = LDAPWorkGroupFilterDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<AbstractGroupFilterDto> findAll(
			@Parameter(description = "It is an optional parameter, if true default domain group filters' models will be returned, else the admins' created ones will be returned.", required = false)
				@QueryParam("model") boolean model) throws BusinessException {
		return domainGroupFilterFacade.findAll(model);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a chosen domain group filter.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = LDAPWorkGroupFilterDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public AbstractGroupFilterDto find(
			@Parameter(description = "The admin can find a domain group filter by the entered uuid.", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return domainGroupFilterFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a domain group filter.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = LDAPWorkGroupFilterDto.class)), responseCode = "200")
	})
	@Override
	public AbstractGroupFilterDto create(
			@Parameter(description = "The domain group filter to create.", required = false) LDAPWorkGroupFilterDto ldapWorkGroupFilterDto)
			throws BusinessException {
		return domainGroupFilterFacade.create(ldapWorkGroupFilterDto);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "Update a domain group filter.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = LDAPWorkGroupFilterDto.class)), responseCode = "200")
	})
	@Override
	public AbstractGroupFilterDto update(
			@Parameter(description = "Domain group filter's uuid to update, if null object is used", required = false)
				@PathParam("uuid") String uuid,
			@Parameter(description = "Domain group filter to update", required = false) LDAPWorkGroupFilterDto ldapWorkGroupFilterDto) throws BusinessException {
		return domainGroupFilterFacade.update(uuid, ldapWorkGroupFilterDto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "Delete a domain group filter.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = LDAPWorkGroupFilterDto.class)), responseCode = "200")
	})
	@Override
	public AbstractGroupFilterDto delete(
			@Parameter(description = "Domain group filter's uuid to delete, if null object is used", required = false)
				@PathParam("uuid") String uuid,
			@Parameter(description = "Domain group filter to delete.", required = false) LDAPWorkGroupFilterDto ldapWorkGroupFilterDto) throws BusinessException {
		return domainGroupFilterFacade.delete(uuid, ldapWorkGroupFilterDto);
	}

	@Override
	@Path("/{uuid}/domains")
	@GET
	@Operation(summary = "Find all domains using a chosen group filter.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	public List<DomainDto> findAllDomainsByGroupFilter(
			@Parameter(description = "Domain group filter's uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return domainGroupFilterFacade.findAllDomainsByGroupFilter(uuid);
	}
}
