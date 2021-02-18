/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.webservice.userv2.impl;

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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;
import org.linagora.linshare.core.facade.webservice.user.MailingListFacade;
import org.linagora.linshare.webservice.userv2.MailingListRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/lists")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailingListRestServiceImpl implements MailingListRestService {

	private final MailingListFacade mailingListFacade;

	public MailingListRestServiceImpl(final MailingListFacade mailingListFacade) {
		this.mailingListFacade = mailingListFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all an user mailing lists.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MailingListDto> findAll() throws BusinessException {
		return mailingListFacade.findAll(null, null);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find an user mailing list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailingListDto find(
			@Parameter(description = "The mailing list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return mailingListFacade.find(null, uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find an user mailing list.")
	@Override
	public void head(String uuid) throws BusinessException {
		mailingListFacade.find(null, uuid);
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
			@Parameter(description = "The mailing list to create. Only identifier is required.", required = true) MailingListDto dto)
					throws BusinessException {
		return mailingListFacade.create(null, dto);

	}

	@Path("/{uuid}")
	@PUT
	@Operation(summary = "Update a mailing list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailingListDto update(
			@Parameter(description = "The mailing list to update.", required = true) MailingListDto dto)
					throws BusinessException {
		return mailingListFacade.update(null, dto, null);
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
			@Parameter(description = "The mailing list to delete.", required = true) MailingListDto dto)
					throws BusinessException {
		Validate.notNull(dto,  "Mailing list dto must be set.");
		return mailingListFacade.delete(null, dto.getUuid());
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Find an user mailing list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailingListDto delete(
			@Parameter(description = "The mailing list to delete uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return mailingListFacade.delete(null, uuid);

	}

	@Path("/{uuid}/contacts")
	@GET
	@Operation(summary = "Find an user mailing list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailingListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MailingListContactDto> findAllContacts(
			@Parameter(description = "The mailing list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return mailingListFacade.findAllContacts(null, uuid);
	}

	@Path("/{uuid}/contacts")
	@POST
	@Operation(summary = "Create a contact in a mailing list.")
	@Override
	public MailingListContactDto createContact(@Parameter(description = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Contact to create.", required = true) MailingListContactDto dto)
					throws BusinessException {
		return mailingListFacade.addContact(null, uuid, dto);
	}

	@Path("/{uuid}/contacts")
	@PUT
	@Operation(summary = "Delete a contact from a mailing list.")
	@Override
	public void updateContact(
			@Parameter(description = "Mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Contact to create.", required = true) MailingListContactDto dto)
					throws BusinessException {
		mailingListFacade.updateContact(null, dto);
	}

	@Path("/{uuid}/contacts")
	@DELETE
	@Operation(summary = "Delete a contact from a mailing list.")
	@Override
	public void deleteContact(
			@Parameter(description = "Mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Contact to create.", required = true) MailingListContactDto dto)
					throws BusinessException {
		mailingListFacade.deleteContact(null, dto.getUuid());
	}

	@Path("/{uuid}/contacts/{contactUuid}")
	@DELETE
	@Operation(summary = "Delete a contact from a mailing list.")
	@Override
	public void deleteContact(
			@Parameter(description = "Mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Mailing list contact uuid.", required = true) @PathParam("contactUuid") String contactUuid)
					throws BusinessException {
		mailingListFacade.deleteContact(null, contactUuid);
	}
}
