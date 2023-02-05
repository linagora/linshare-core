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
package org.linagora.linshare.webservice.userv5.impl;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ModeratorRoleEnum;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.userv5.GuestRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/guests")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class GuestRestServiceImpl implements GuestRestService {

	private final GuestFacade guestFacade;

	public GuestRestServiceImpl(GuestFacade guestFacade) {
		super();
		this.guestFacade = guestFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all guests of a user.", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))), responseCode = "200") })
	@Override
	public List<GuestDto> findAll(
			@Parameter(
					description = "If value is SIMPLE/ADMIN the guests where a user is moderator of by role will be returned, if value is `ALL` all user's guests, if null all guests of user's authorized domains will be returned.",
					required = false,
					schema = @Schema(implementation = ModeratorRoleEnum.class)
				)
				@QueryParam("role") String role,
			@Parameter(description = "The list of returned guests will be filtered by the entered patten if not null.", required = false)
				@QueryParam("pattern") String pattern)
			throws BusinessException {
		return guestFacade.findAll(Version.V5, pattern, role);
	}

	@Path("/search")
	@POST
	@Operation(summary = "Search all guests who match with some pattern.", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))), responseCode = "200") })
	@Override
	public List<GuestDto> search(
			@Parameter(description = "Patterns to search.", required = true) UserSearchDto userSearchDto)
			throws BusinessException {
		Validate.isTrue(
				!(lessThan3Char(userSearchDto.getFirstName()) && lessThan3Char(userSearchDto.getLastName())
						&& lessThan3Char(userSearchDto.getMail())),
				"One pattern is required, pattern must be greater than 3 characters");
		return guestFacade.search(Version.V5, userSearchDto);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a guest.", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))), responseCode = "200") })
	@Override
	public GuestDto find(@Parameter(description = "Guest's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return guestFacade.find(Version.V5, null, uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a guest.")
	@Override
	public void head(@Parameter(description = "Guest's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		guestFacade.find(Version.V5, null, uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a guest.", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))), responseCode = "200") })
	@Override
	public GuestDto create(@Parameter(description = "Guest to create.", required = true) GuestDto guest)
			throws BusinessException {
		return guestFacade.create(Version.V5, null, guest);
	}

	@Path("/{uuid : .*}")
	@PUT
	@Operation(summary = "Update a guest.", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))), responseCode = "200") })
	@Override
	public GuestDto update(@Parameter(description = "Guest to update.", required = true) GuestDto guest,
			@Parameter(description = "Guest uuid, if null dto.uuid is used.", required = false) @PathParam("uuid") String uuid)
			throws BusinessException {
		return guestFacade.update(Version.V5, null, guest, uuid);

	}

	@Path("/{uuid : .*}")
	@DELETE
	@Operation(summary = "Delete a guest.", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))), responseCode = "200") })
	@Override
	public GuestDto delete(@Parameter(description = "Guest to delete.", required = true) GuestDto guest,
			@Parameter(description = "Guest's uuid to delete.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return guestFacade.delete(Version.V5, null, guest, uuid);
	}

	@Path("/{uuid}/reset")
	@POST
	@Operation(summary = "Reset guest password.", responses = {
			@ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))), responseCode = "200") })
	@Override
	public void resetPassword(@Parameter(description = "reset password for the guest.", required = true) GuestDto guest,
			@Parameter(description = "Guest's uuid to reset.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		guestFacade.resetPassword(guest, uuid);
	}

	private boolean lessThan3Char(String s) {
		return StringUtils.trimToEmpty(s).length() < 3;
	}
}
