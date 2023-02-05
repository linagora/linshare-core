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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListDto;
import org.linagora.linshare.core.facade.webservice.user.ContactListFacade;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.userv2.ContactListRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/contact_lists")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ContactListRestServiceImpl implements ContactListRestService {

	private final ContactListFacade contactListFacade;

	public ContactListRestServiceImpl(final ContactListFacade contactListFacade) {
		this.contactListFacade = contactListFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all an user contact lists.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContactListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<ContactListDto> findAll(
			@Parameter(description = "filter contact list by my contact list(true), others (false) or all (null).", required = false) @QueryParam("mine") Boolean mine,
			@Parameter(description = "filter contact list by a contact email.", required = false) @QueryParam("contactMail") String contactMail)
			throws BusinessException {
		if (contactMail == null) {
			return contactListFacade.findAll(null, mine);
		}
		return contactListFacade.findAllByMemberEmail(null, mine, contactMail);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find an user contact list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContactListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContactListDto find(
			@Parameter(description = "The contact list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return contactListFacade.find(null, uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find an user contact list.")
	@Override
	public void head(String uuid) throws BusinessException {
		contactListFacade.find(null, uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a contact list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContactListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContactListDto create(
			@Parameter(description = "The contact list to create. Only identifier is required.", required = true) ContactListDto dto)
					throws BusinessException {
		return contactListFacade.create(null, dto);
	}

	@Path("/{uuid}/duplicate/{identifier}")
	@POST
	@Operation(summary = "Ducplicate a contact list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContactListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContactListDto duplicate(
			@Parameter(description = "The contact list uuid, to duplicate", required = true)
				@PathParam("uuid") String contactsListUuidSource,
			@Parameter(description = "New name for the duplicate ContactList.", required = true)
				@PathParam("identifier")  String contactListName)
					throws BusinessException {
		return contactListFacade.duplicate(null, contactsListUuidSource, contactListName);
	}

	@Path("/{uuid : .*}")
	@PUT
	@Operation(summary = "Update a contact list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContactListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContactListDto update(
			@Parameter(description = "The contact list to update.", required = true) ContactListDto dto,
			@Parameter(description = "Mailing list uuid, if null dto.uuid is used.", required = false)
				@PathParam("uuid") String uuid)
					throws BusinessException {
		return contactListFacade.update(null, dto, uuid);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a contact list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContactListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContactListDto delete(
			@Parameter(description = "The contact list to delete.", required = true) ContactListDto dto)
					throws BusinessException {
		Validate.notNull(dto,  "Mailing list dto must be set.");
		return contactListFacade.delete(null, dto.getUuid());
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Find an user contact list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContactListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContactListDto delete(
			@Parameter(description = "The contact list to delete uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return contactListFacade.delete(null, uuid);
	}

	@Path("/{uuid}/contacts")
	@GET
	@Operation(summary = "Find an user contact list.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContactListDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<ContactListContactDto> findAllContacts(
			@Parameter(description = "The contact list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return contactListFacade.findAllContacts(null, uuid);
	}

	@Path("/{uuid}/contacts")
	@POST
	@Operation(summary = "Create a contact in a contact list.")
	@Override
	public ContactListContactDto createContact(@Parameter(description = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Contact to create.", required = true) ContactListContactDto dto)
					throws BusinessException {
		return contactListFacade.addContact(null, uuid, dto);
	}

	@Path("/{uuid}/contacts/{contact_uuid : .*}")
	@PUT
	@Operation(summary = "Delete a contact from a contact list.")
	@Override
	public ContactListContactDto updateContact(
			@Parameter(description = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Mailing list contact uuid.", required = true) @PathParam("contact_uuid") String contactUuid,
			@Parameter(description = "Contact to create.", required = true) ContactListContactDto dto)
					throws BusinessException {
		return contactListFacade.updateContact(null, dto, contactUuid);
	}

	@Path("/{uuid}/contacts")
	@DELETE
	@Operation(summary = "Delete a contact from a contact list.")
	@Override
	public void deleteContact(
			@Parameter(description = "Mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Contact to create.", required = true) ContactListContactDto dto)
					throws BusinessException {
		contactListFacade.deleteContact(null, dto.getUuid());
	}

	@Path("/{uuid}/contacts/{contactUuid}")
	@DELETE
	@Operation(summary = "Delete a contact from a contact list.")
	@Override
	public void deleteContact(
			@Parameter(description = "Mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Mailing list contact uuid.", required = true) @PathParam("contactUuid") String contactUuid)
					throws BusinessException {
		contactListFacade.deleteContact(null, contactUuid);
	}

	@Path("/{uuid}/audit")
	@GET
	@Operation(summary = "Get all traces for a document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> audit(
			@Parameter(description = "The contact list uuid.", required = true)
				@PathParam("uuid") String uuid) {
		return contactListFacade.audit(null, uuid);
	}

}
