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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.linagora.linshare.core.facade.webservice.admin.AutocompleteFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.UserFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.RestrictedContactDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDtoQuotaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ModeratorRoleEnum;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;
import org.linagora.linshare.webservice.adminv5.UserRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

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

	protected final AutocompleteFacade autocompleteFacade;

	private PagingResponseBuilder<UserDto> pageResponseBuilder= new PagingResponseBuilder<>();

	public UserRestServiceImpl(
			UserFacade userFacade,
			AutocompleteFacade autocompleteFacade) {
		this.userFacade = userFacade;
		this.autocompleteFacade = autocompleteFacade;
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
			@Parameter(description = "If the admin specify the domains he will retrieve the list of users from the choosen domains, else all users of all authorized domains will be returned.", required = false)
				@QueryParam("domains") List<String> domainsUuids,
			@Parameter(description = "The `domain` param is deprecated, `domains`can be used instead, if provided the `domains` list will be overrided by `domain` value.", deprecated = true, required = false)
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
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to retrieve the users with a chosen role.", required = false)
				@QueryParam("role") String role,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to retrieve the users with a chosen type.", required = false)
				@QueryParam("type") String type,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to retrieve the users with a chosen moderator role.", required = false)
				@QueryParam("moderatorRole") String moderatorRole,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to retrieve the guest users with a number of moderators greater than.", required = false)
				@QueryParam("greaterThan") Integer greaterThan,
			@Parameter(description = "It is an optional parameter if it is indicated the admin will be able to retrieve the guest users with a number of moderators lower than.", required = false)
				@QueryParam("lowerThan") Integer lowerThan,
			@Parameter(description = "The admin can choose the page number to get.", required = false)
				@QueryParam("page") Integer pageNumber, @Parameter(description = "The admin can choose the number of elements to get.", required = false)
				@QueryParam("size") Integer pageSize) throws BusinessException {
		if (!Strings.isNullOrEmpty(domainUuid)) {
			domainsUuids = Lists.newArrayList(domainUuid);
		}
		PageContainer<UserDto> container = userFacade.findAll(null, domainsUuids, SortOrder.valueOf(sortOrder),
				UserFields.valueOf(sortField), mail, firstName, lastName, restricted, canCreateGuest, canUpload, role,
				type, moderatorRole, Optional.ofNullable(greaterThan), Optional.ofNullable(lowerThan), pageNumber, pageSize);
		return pageResponseBuilder.build(container);
	}

	@Path("/autocomplete/{pattern}")
	@GET
	@Operation(summary = "Provide user autocompletion.", responses = {
			@ApiResponse(
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
					responseCode = "200"
			)
	})
	@Override
	public Set<UserDto> autocomplete(
			@Parameter(description = "Pattern to complete.", required = true) @PathParam("pattern") String pattern,
			@Parameter(description = "Account type to look for.", required = false) @QueryParam("accountType") String accountType,
			@Parameter(description = "Domain to look into.", required = false) @QueryParam("domain") String domain)
			throws BusinessException {
		return autocompleteFacade.findUserV5(pattern, accountType, domain);
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

	@Path("/{uuid}/quota/{quotaUuid}")
	@GET
	@Operation(summary = "find user's quota", responses = {
			@ApiResponse(
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDtoQuotaDto.class))),
					responseCode = "200"
					)
	})
	@Override
	public UserDtoQuotaDto findUserQuota(
			@Parameter(description = "User's Uuid", required = true)
				@PathParam("uuid") String accountUuid,
			@Parameter(description = "User's quota Uuid", required = true)
				@PathParam("quotaUuid") String quotaUuid
			) throws BusinessException {
		return userFacade.findUserQuota(null, accountUuid, quotaUuid);
	}

	@Path("/{uuid}/2fa/{secondfaUuid}")
	@GET
	@Operation(summary = "Get the 2FA state ",
		responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = SecondFactorDto.class)), responseCode = "200")
		})
	@Override
	public SecondFactorDto find2FA(
			@Parameter(description = "User uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "The second factor key uuid, Required.", required = true)
				@PathParam("secondfaUuid") String secondfaUuid
			)
			throws BusinessException {
		return userFacade.find2FA(uuid, secondfaUuid);
	}

	@Path("/{uuid}/2fa/{secondfaUuid: .*}")
	@DELETE
	@Operation(summary = "Delete a shared key of a given user, 2fa will be disabled ",
		responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = SecondFactorDto.class)), responseCode = "200")
		})
	@Override
	public SecondFactorDto delete2FA(
			@Parameter(description = "User uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "The second factor key uuid, Optional if defined in payload.", required = false)
				@PathParam("secondfaUuid") String secondfaUuid,
			@Parameter(
					description = "Second factor dto. Optional. Uuid can be provided if not defined in the URL.",
					required = false
					)
				SecondFactorDto dto)
			throws BusinessException {
		return userFacade.delete2FA(uuid, secondfaUuid, dto);
	}

	@Path("/{uuid}/quota/{quotaUuid: .*}")
	@PUT
	@Operation(summary = "Update user's quota", responses = {
			@ApiResponse(
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDtoQuotaDto.class))),
					responseCode = "200"
					)
	})
	@Override
	public UserDtoQuotaDto updateUserQuota(
			@Parameter(description = "User's Uuid", required = true)
				@PathParam("uuid") String userUuid,
			@Parameter(description = "User's quota Uuid", required = true)
				@PathParam("quotaUuid") String quotaUuid,
			@Parameter(description = "User's quota Dto. Should at least contains quota field. Only quota, maxFileSize, quotaOverride and maxFileSizeOverride fields can be updated.", required = true)
			UserDtoQuotaDto dto
			) throws BusinessException {
		return userFacade.updateUserQuota(null, userUuid, quotaUuid, dto);
	}

	@Path("/{uuid}/guests")
	@GET
	@Operation(summary = "Find a user.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = UserDto.class)), responseCode = "200")
	})
	@Override
	public List<GuestDto> findAllUserGuests(
			@Parameter(description = "The admin can find all user's guests.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "The admin can filter the users' guests by moderator role.", required = false)
				@QueryParam("role") ModeratorRoleEnum role,
			@Parameter(description = "The list of returned guests will be filtered by the entered patten if not null.", required = false)
				@QueryParam("pattern") String pattern)
			throws BusinessException {
		return userFacade.findAllUserGuests(null, uuid, role, pattern);
	}
}
