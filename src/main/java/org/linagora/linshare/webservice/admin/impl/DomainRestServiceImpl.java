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
package org.linagora.linshare.webservice.admin.impl;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.DomainRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/domains")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class DomainRestServiceImpl extends WebserviceBase implements
		DomainRestService {

	private final DomainFacade domainFacade;

	public DomainRestServiceImpl(final DomainFacade domainFacade) {
		this.domainFacade = domainFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all domains.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<DomainDto> findAll() throws BusinessException {
		return domainFacade.findAll();
	}

	@Path("/{domainId}")
	@GET
	@Operation(summary = "Find a domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainDto find(@PathParam(value = "domainId") String domainId,
			@QueryParam("tree") @DefaultValue("false") boolean tree,
			@QueryParam("parent") @DefaultValue("false") boolean parent)
			throws BusinessException {
		return domainFacade.find(domainId, tree, parent);
	}

	@Path("/{domainId}")
	@HEAD
	@Operation(summary = "Find a domain.")
	@Override
	public void head(@PathParam(value = "domainId") String domainId)
					throws BusinessException {
		domainFacade.find(domainId, false, false);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainDto create(DomainDto domain) throws BusinessException {
		return domainFacade.create(domain);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainDto update(DomainDto domain) throws BusinessException {
		return domainFacade.update(domain);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainDto delete(DomainDto domain) throws BusinessException {
		return domainFacade.delete(domain.getIdentifier());
	}
}
