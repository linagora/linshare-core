/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListDto;
import org.linagora.linshare.core.facade.webservice.user.ContactListFacade;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.userv2.ContactListRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/contact_lists")
@Api(value = "/rest/user/v2/contact_lists", description = "Contact lists user api.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ContactListRestServiceImpl implements ContactListRestService {

	private final ContactListFacade contactListFacade;

	public ContactListRestServiceImpl(final ContactListFacade contactListFacade) {
		this.contactListFacade = contactListFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all an user contact lists.", response = ContactListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Set<ContactListDto> findAll(
			@ApiParam(value = "filter contact list by my contact list(true), others (false) or all (null).", required = false)
				@QueryParam("mine") Boolean mine) throws BusinessException {
		return contactListFacade.findAll(null, mine);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find an user contact list.", response = ContactListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Mailing list not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ContactListDto find(
			@ApiParam(value = "The contact list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return contactListFacade.find(null, uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find an user contact list.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Mailing list not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public void head(String uuid) throws BusinessException {
		contactListFacade.find(null, uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a contact list.", response = ContactListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ContactListDto create(
			@ApiParam(value = "The contact list to create. Only identifier is required.", required = true) ContactListDto dto)
					throws BusinessException {
		return contactListFacade.create(null, dto);
	}

	@Path("/{uuid : .*}")
	@PUT
	@ApiOperation(value = "Update a contact list.", response = ContactListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ContactListDto update(
			@ApiParam(value = "The contact list to update.", required = true) ContactListDto dto,
			@ApiParam(value = "Mailing list uuid, if null dto.uuid is used.", required = false)
				@PathParam("uuid") String uuid)
					throws BusinessException {
		return contactListFacade.update(null, dto, uuid);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a contact list.", response = ContactListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 404, message = "Mailing list not found."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ContactListDto delete(
			@ApiParam(value = "The contact list to delete.", required = true) ContactListDto dto)
					throws BusinessException {
		Validate.notNull(dto,  "Mailing list dto must be set.");
		return contactListFacade.delete(null, dto.getUuid());
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Find an user contact list.", response = ContactListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Mailing list not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ContactListDto delete(
			@ApiParam(value = "The contact list to delete uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return contactListFacade.delete(null, uuid);
	}

	@Path("/{uuid}/contacts")
	@GET
	@ApiOperation(value = "Find an user contact list.", response = ContactListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Mailing list not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Set<ContactListContactDto> findAllContacts(
			@ApiParam(value = "The contact list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return contactListFacade.findAllContacts(null, uuid);
	}

	@Path("/{uuid}/contacts")
	@POST
	@ApiOperation(value = "Create a contact in a contact list.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") })
	@Override
	public ContactListContactDto createContact(@ApiParam(value = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "Contact to create.", required = true) ContactListContactDto dto)
					throws BusinessException {
		return contactListFacade.addContact(null, uuid, dto);
	}

	@Path("/{uuid}/contacts")
	@PUT
	@ApiOperation(value = "Delete a contact from a contact list.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") })
	@Override
	public void updateContact(
			@ApiParam(value = "Mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "Contact to create.", required = true) ContactListContactDto dto)
					throws BusinessException {
		contactListFacade.updateContact(null, dto);
	}

	@Path("/{uuid}/contacts")
	@DELETE
	@ApiOperation(value = "Delete a contact from a contact list.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") })
	@Override
	public void deleteContact(
			@ApiParam(value = "Mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "Contact to create.", required = true) ContactListContactDto dto)
					throws BusinessException {
		contactListFacade.deleteContact(null, dto.getUuid());
	}

	@Path("/{uuid}/contacts/{contactUuid}")
	@DELETE
	@ApiOperation(value = "Delete a contact from a contact list.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") })
	@Override
	public void deleteContact(
			@ApiParam(value = "Mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "Mailing list contact uuid.", required = true) @PathParam("contactUuid") String contactUuid)
					throws BusinessException {
		contactListFacade.deleteContact(null, contactUuid);
	}

	@Path("/{uuid}/audit")
	@GET
	@ApiOperation(value = "Get all traces for a document.", response = AuditLogEntryUser.class, responseContainer="Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Contact List not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Set<AuditLogEntryUser> audit(
			@ApiParam(value = "The contact list uuid.", required = true)
				@PathParam("uuid") String uuid) {
		return contactListFacade.audit(null, uuid);
	}
}
