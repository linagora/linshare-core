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
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AbstractUserProfileDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.core.facade.webservice.common.dto.FavouriteRecipientDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestProfileDto;
import org.linagora.linshare.core.facade.webservice.common.dto.RestrictedContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserProfileDto;
import org.linagora.linshare.core.facade.webservice.user.UserProfileFacade;
import org.linagora.linshare.webservice.userv5.UserProfileRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/me")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UserProfileRestServiceImpl implements UserProfileRestService {

	private final UserProfileFacade userProfileFacade;

	public UserProfileRestServiceImpl(UserProfileFacade userProfileFacade) {
		super();
		this.userProfileFacade = userProfileFacade;
	}

	@Path("/profile")
	@GET
	@Operation(summary = "Find the user profile.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {UserProfileDto.class, GuestProfileDto.class},
					type = "object"
				)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public AbstractUserProfileDto find() throws BusinessException {
		return userProfileFacade.find();
	}

	@Path("/profile/{uuid}")
	@PUT
	@Operation(summary = "Update the user profile.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {UserProfileDto.class, GuestProfileDto.class},
					type = "object"
				)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public AbstractUserProfileDto update(
		@Parameter(description = "User's uuid.", required = true)
			@PathParam("uuid") String uuid,
		@Parameter(description = "The profile to update", required = true)
			AbstractUserProfileDto dto) throws BusinessException {
		return userProfileFacade.update(dto);
	}

	@Path("/restricted_contacts")
	@GET
	@Operation(summary = "Find my restricted contacts (Guest only).", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				array = @ArraySchema(
					schema = @Schema(implementation = RestrictedContactDto.class)
				)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public List<RestrictedContactDto> restrictedContacts() throws BusinessException {
		return userProfileFacade.restrictedContacts();
	}

	@Path("/favourite_recipients")
	@GET
	@Operation(summary = "Find my favourite recipients.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				array = @ArraySchema(
					schema = @Schema(implementation = FavouriteRecipientDto.class)
				)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public List<FavouriteRecipientDto> favouriteRecipients(
		@Parameter(description = "Filter favourite recipients by its mail.", required = false)
			@QueryParam("mail") String mailFilter) throws BusinessException {
		return userProfileFacade.favouriteRecipients(Optional.ofNullable(mailFilter));
	}

	@Path("/favourite_recipients/{recipient}")
	@DELETE
	@Operation(summary = "Find my favourite recipients.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				array = @ArraySchema(
					schema = @Schema(implementation = FavouriteRecipientDto.class)
				)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public FavouriteRecipientDto removeFavouriteRecipient(
		@Parameter(description = "Recipient to be deleted.", required = true)
			@PathParam("recipient") String recipient) throws BusinessException {
		return userProfileFacade.removeFavouriteRecipient(recipient);
	}
}
