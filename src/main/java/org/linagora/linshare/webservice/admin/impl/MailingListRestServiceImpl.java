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
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailingListFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailingListRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/lists")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailingListRestServiceImpl extends WebserviceBase implements
		MailingListRestService {

	private final MailingListFacade mailingListFacade;

	public MailingListRestServiceImpl(final MailingListFacade mailingListFacade) {
		super();
		this.mailingListFacade = mailingListFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all mailing lists.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MailingListDto> findAll() throws BusinessException {
		return mailingListFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a mailing list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailingListDto find(
			@Parameter(description = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mailingListFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a mailing list.")
	@Override
	public void head(
			@Parameter(description = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		mailingListFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a mailing list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailingListDto create(
			@Parameter(description = "Mailing list to create.", required = true) MailingListDto dto)
			throws BusinessException {
		return mailingListFacade.create(dto);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a mailing list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailingListDto update(
			@Parameter(description = "Mailing list to update.", required = true) MailingListDto dto)
			throws BusinessException {
		return mailingListFacade.update(dto);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a mailing list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailingListDto delete(@PathParam(value = "Mailing list to delete.") String uuid)
			throws BusinessException {
		return mailingListFacade.delete(uuid);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a mailing list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailingListDto delete(
			@Parameter(description = "Mailing list to delete.", required = true) MailingListDto dto)
			throws BusinessException {
		return mailingListFacade.delete(dto.getUuid());
	}

	@Path("/{uuid}/contacts")
	@POST
	@Operation(summary = "Create a contact in a mailing list.")
	@Override
	public void createContact(
			@Parameter(description = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Contact to create.", required = true) MailingListContactDto dto)
			throws BusinessException {
		mailingListFacade.addContact(uuid, dto);
	}

	@Path("/{uuid}/contacts")
	@DELETE
	@Operation(summary = "Delete a contact from a mailing list.")
	@Override
	public void deleteContact(
			@Parameter(description = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Contact uuid.", required = true) MailingListContactDto dto)
			throws BusinessException {
		mailingListFacade.deleteContact(dto.getUuid());
	}
}
