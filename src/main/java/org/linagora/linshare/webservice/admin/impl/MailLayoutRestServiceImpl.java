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
import org.linagora.linshare.core.facade.webservice.admin.MailLayoutFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailLayoutDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailLayoutRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/mail_layouts")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailLayoutRestServiceImpl extends WebserviceBase implements
		MailLayoutRestService {

	private final MailLayoutFacade mailLayoutFacade;

	public MailLayoutRestServiceImpl(final MailLayoutFacade mailLayoutFacade) {
		super();
		this.mailLayoutFacade = mailLayoutFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all mail layouts.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailLayoutDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MailLayoutDto> findAll(
			@QueryParam(value = "domainId") String domainId,
			@QueryParam("onlyCurrentDomain") @DefaultValue("false") boolean onlyCurrentDomain)
			throws BusinessException {
		return mailLayoutFacade.findAll(domainId, onlyCurrentDomain);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a mail layout.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailLayoutDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailLayoutDto find(
			@Parameter(description = "Mail layout uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mailLayoutFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a mail layout.")
	@Override
	public void head(
			@Parameter(description = "Mail layout uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		mailLayoutFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a mail layout.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailLayoutDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailLayoutDto create(
			@Parameter(description = "Mail layout to create.", required = true) MailLayoutDto dto)
			throws BusinessException {
		return mailLayoutFacade.create(dto);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a mail layout.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailLayoutDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailLayoutDto update(
			@Parameter(description = "Mail layout to update.", required = true) MailLayoutDto dto)
			throws BusinessException {
		return mailLayoutFacade.update(dto);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete an unused mail layout.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailLayoutDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailLayoutDto delete(
			@Parameter(description = "Mail layout to delete.", required = true) MailLayoutDto dto)
			throws BusinessException {
		return mailLayoutFacade.delete(dto.getUuid());
	}
}
