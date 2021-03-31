/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.entities.fields.UserFields;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.UserFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.RestrictedContactDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDto;
import org.linagora.linshare.webservice.adminv5.UserRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/users")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UserRestServiceImpl implements UserRestService {

	private final UserFacade userFacade;

	private PagingResponseBuilder<UserDto> pageResponseBuilder= new PagingResponseBuilder<>();

	public UserRestServiceImpl(
			UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all users user.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response findAll(
			@Parameter(description = "If the admin specify the domain he will retrieve the list of the choosen domain, else all users of all domains will be returned.", required = false)
				@QueryParam("domain") String domainUuid,
			@Parameter(description = "The admin can choose the order of sorting the user's list to retrieve, if not set the ascending order will be applied by default.", required = false)
				@QueryParam("sortOrder") @DefaultValue("ASC") String sortOrder,
			@Parameter(description = "The admin can choose the field to sort with the user's list to retrieve, if not set the modification date order will be choosen by default.", required = false)
				@QueryParam("sortField") @DefaultValue("modificationDate") String sortField,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to filter the users' list by mail adress, en email pattern is expected like `@linshare.org`.", required = false)
				@QueryParam("mail") String mail,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to filter the users' list by first name.", required = false)
				@QueryParam("firstName") String firstName,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to filter the users' list by last name.", required = false)
				@QueryParam("lastName") String lastName,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to filter the restricted users' list.", required = false)
				@QueryParam("restricted") Boolean restricted,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to retrieve the users whose can create guest.", required = false)
				@QueryParam("canCreateGuest") Boolean canCreateGuest,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to retrieve the users whose can upload.", required = false)
				@QueryParam("canUpload") Boolean canUpload,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to retrieve the users with a choosen role.", required = false)
				@QueryParam("role") String role,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to retrieve the users with a choosen type.", required = false)
				@QueryParam("type") String type,
			@Parameter(description = "The admin can choose the page number to get.", required = false)
				@QueryParam("page") Integer pageNumber,
			@Parameter(description = "The admin can choose the number of elements to get.", required = false)
				@QueryParam("size") Integer pageSize) throws BusinessException {
		PageContainer<UserDto> container = userFacade.findAll(null, domainUuid, SortOrder.valueOf(sortOrder),
				UserFields.valueOf(sortField), mail, firstName, lastName, restricted, canCreateGuest, canUpload, role,
				type, pageNumber, pageSize);
		return pageResponseBuilder.build(container);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a user.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = UserDto.class)), responseCode = "200")
	})
	@Override
	public UserDto find(
			@Parameter(description = "The admin can find a user with the entered uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return userFacade.find(null, uuid);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "Update a user.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = UserDto.class)), responseCode = "200")
	})
	@Override
	public UserDto update(
			@Parameter(description = "User to update", required = false) UserDto userDto,
			@Parameter(description = "User's uuid to update, if null object is used", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return userFacade.update(null, userDto, uuid);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "Delete an user.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = UserDto.class)), responseCode = "200")
	})
	@Override
	public UserDto delete(
			@Parameter(description = "User to delete.", required = false) UserDto userDto,
			@Parameter(description = "User's uuid to delete, if null object is used", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return userFacade.delete(null, userDto, uuid);
	}

	@Path("/{uuid}/restricted_contacts")
	@GET
	@Operation(summary = "Find all restricted contacts of a choosen user.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestrictedContactDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<RestrictedContactDto> findAllRestrictedContacts(
		@Parameter(description = "The user's uuid to retrieve his restricted contacts.", required = true)
			@PathParam("uuid") String userUuid,
		@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to filter the restricted contact's list by mail adress, en email pattern is expected like `@linshare.org`.", required = false)
			@QueryParam("mail") String mail,
		@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to filter the restricted contact's list by first name.", required = false)
			@QueryParam("firstName") String firstName,
		@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to filter the restricted contact's list by last name.", required = false)
			@QueryParam("lastName") String lastName) throws BusinessException {
		return userFacade.findAllRestrictedContacts(null, userUuid, mail, firstName, lastName);
	}

	@Path("/{uuid}/restricted_contacts/{restrictedContactUuid}")
	@GET
	@Operation(summary = "Find a restricted contact.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = RestrictedContactDto.class)), responseCode = "200")
	})
	@Override
	public RestrictedContactDto findRestrictedContact(
		@Parameter(description = "The owner's uuid of the restricted contact to retrieve.", required = true)
			@PathParam("uuid") String ownerUuid,
		@Parameter(description = "The restricted contact's uuid to retrieve.", required = true)
			@PathParam("restrictedContactUuid") String restrictedContactUuid) throws BusinessException {
		return userFacade.findRestrictedContact(null, ownerUuid, restrictedContactUuid);
	}

	@Path("/{uuid}/restricted_contacts")
	@POST
	@Operation(summary = "Create a restricted contact.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = RestrictedContactDto.class)), responseCode = "200")
	})
	@Override
	public RestrictedContactDto createRestrictedContact(
			@Parameter(description = "The owner's uuid of the restricted contact to create.", required = true)
				@PathParam("uuid") String ownerUuid,
			@Parameter(description = "The restricted contact to create, if its uuid is null, mail and domain are required for its creation.", required = false) RestrictedContactDto restrictedContactDto) throws BusinessException {
		return userFacade.createRestrictedContact(null, ownerUuid, restrictedContactDto);
	}

	@Path("/{uuid}/restricted_contacts/{restrictedContactUuid : .*}")
	@DELETE
	@Operation(summary = "Delete a restricted contact.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = RestrictedContactDto.class)), responseCode = "200")
	})
	@Override
	public RestrictedContactDto deleteRestrictedContact(
			@Parameter(description = "The owner's uuid of the restricted contact to delete.", required = true)
				@PathParam("uuid") String ownerUuid,
			@Parameter(description = "The restricted contact to delete", required = false) RestrictedContactDto restrictedContactDto,
			@Parameter(description = "The restricted contact's uuid to delete, if null object is used", required = false)
				@PathParam("restrictedContactUuid") String restrictedContactUuid) throws BusinessException {
		return userFacade.deleteRestrictedContact(null, ownerUuid, restrictedContactDto, restrictedContactUuid);
	}
}
