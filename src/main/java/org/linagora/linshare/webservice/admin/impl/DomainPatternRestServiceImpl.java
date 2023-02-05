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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainPatternFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPatternDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.DomainPatternRestService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/domain_patterns")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class DomainPatternRestServiceImpl extends WebserviceBase implements
		DomainPatternRestService {

	private final DomainPatternFacade domainPatternFacade;

	public DomainPatternRestServiceImpl(
			final DomainPatternFacade domainPatternFacade) {
		this.domainPatternFacade = domainPatternFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all domain patterns.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPatternDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<DomainPatternDto> findAll() throws BusinessException {
		return domainPatternFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a domain pattern.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPatternDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainPatternDto find(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return domainPatternFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a domain pattern.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPatternDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void head(@PathParam(value = "uuid") String uuid) throws BusinessException {
		domainPatternFacade.find(uuid);
	}

	@Path("/models")
	@GET
	@Operation(summary = "Find all domain pattern's models.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPatternDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<DomainPatternDto> findAllModels() throws BusinessException {
		return domainPatternFacade.findAllModels();
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a domain pattern.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPatternDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainPatternDto create(DomainPatternDto domainPattern)
			throws BusinessException {
		return domainPatternFacade.create(domainPattern);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a domain pattern.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPatternDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainPatternDto update(DomainPatternDto domainPattern)
			throws BusinessException {
		return domainPatternFacade.update(domainPattern);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a domain pattern.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainPatternDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainPatternDto delete(DomainPatternDto domainPattern)
			throws BusinessException {
		return domainPatternFacade.delete(domainPattern);
	}
}
