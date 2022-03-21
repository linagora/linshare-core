/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.ModeratorFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ModeratorDto;
import org.linagora.linshare.webservice.adminv5.ModeratorRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/guests/{guestUuid}/moderators")
@Consumes({MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ModeratorRestServiceImpl implements ModeratorRestService{

	private final ModeratorFacade moderatorFacade;

	public ModeratorRestServiceImpl(ModeratorFacade moderatorFacade) {
		this.moderatorFacade = moderatorFacade;
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a guest's moderator.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = ModeratorDto.class)), responseCode = "200")
	})
	@Override
	public ModeratorDto create(
		@Parameter(description = "The guest's uuid.", required = true)
			@PathParam("guestUuid") String guestUuid,
		@Parameter(description = "Moderator to create", required = false) ModeratorDto moderatorDto) throws BusinessException {
		return moderatorFacade.create(moderatorDto);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a chosen moderator.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = ModeratorDto.class)), responseCode = "200")
	})
	@Override
	public ModeratorDto find(
		@Parameter(description = "The guest's uuid.", required = true)
			@PathParam("guestUuid") String guestUuid,
		@Parameter(description = "The admin can find a guest's moderator with the entered uuid.", required = true) 
			@PathParam("uuid") String uuid) throws BusinessException {
		return moderatorFacade.find(uuid);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "Update a a guest moderator.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = ModeratorDto.class)), responseCode = "200")
	})
	@Override
	public ModeratorDto update(
		@Parameter(description = "The guest's uuid.", required = true)
			@PathParam("guestUuid") String guestUuid,
		@Parameter(description = "Moderator's uuid to update, if null object is used", required = false)
			@PathParam("uuid") String uuid,
		@Parameter(description = "Moderator to update", required = false) ModeratorDto moderatorDto) 
				throws BusinessException {
		return moderatorFacade.update(uuid, moderatorDto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "Delete a guest moderator.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = ModeratorDto.class)), responseCode = "200")
	})
	@Override
	public ModeratorDto delete(
		@Parameter(description = "The guest's uuid.", required = true)
			@PathParam("guestUuid") String guestUuid,
		@Parameter(description = "Moderator's uuid to delete, if null object is used", required = false)
			@PathParam("uuid") String uuid,
		@Parameter(description = "Moderator to delete", required = false) ModeratorDto moderatorDto) 
				throws BusinessException {
		return moderatorFacade.delete(uuid, moderatorDto);
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all guest's moderators.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = ModeratorDto.class)), responseCode = "200")
	})
	@Override
	public List<ModeratorDto> findAllByGuest(
		@Parameter(description = "The guest's uuid.", required = true)
			@PathParam("guestUuid") String guestUuid) throws BusinessException {
		return moderatorFacade.findAllByGuest(guestUuid);
	}

}
