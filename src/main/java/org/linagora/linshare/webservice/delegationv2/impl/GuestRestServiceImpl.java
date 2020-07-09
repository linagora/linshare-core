/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

package org.linagora.linshare.webservice.delegationv2.impl;

import java.util.List;

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
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegationv2.GuestRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("{actorUuid}/guests")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class GuestRestServiceImpl extends WebserviceBase implements GuestRestService {

	private GuestFacade guestFacade;

	public GuestRestServiceImpl(GuestFacade guestFacade) {
		super();
		this.guestFacade = guestFacade;
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a guest.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public GuestDto create(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Guest to create.", required = true) GuestDto guest)
					throws BusinessException {
		return guestFacade.create(actorUuid, guest);
	}

	@Path("/{identifier}")
	@GET
	@Operation(summary = "Get a guest.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public GuestDto get(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The guest identifier, could be uuid or mail.", required = true) @PathParam("identifier") String identifier,
			@Parameter(description = "Boolean value to search by domain.") @DefaultValue("false") @QueryParam("mail") Boolean isMail,
			@Parameter(description = "Domain identifier.") @QueryParam("domain") String domain)
			throws BusinessException {
		if (isMail) {
			return guestFacade.find(actorUuid, domain, identifier);
		}
		return guestFacade.find(actorUuid, identifier);
	}

	@Path("/{identifier}")
	@HEAD
	@Operation(summary = "Get a guest.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void head(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The guest identifier, could be uuid or mail.", required = true) @PathParam("identifier") String identifier,
			@Parameter(description = "Boolean value to search by domain.") @DefaultValue("false") @QueryParam("mail") Boolean isMail,
			@Parameter(description = "Domain identifier.") @QueryParam("domain") String domain)
			throws BusinessException {
		if (isMail) {
			guestFacade.find(actorUuid, domain, identifier);
		}
		guestFacade.find(actorUuid, identifier);
	}

	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/")
	@GET
	@Operation(summary = "Get all guests.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<GuestDto> getAll(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid) throws BusinessException {
		return guestFacade.findAll(actorUuid);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "Update a guest.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public GuestDto update(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Guest to update.", required = true) GuestDto guest,
			@Parameter(description = "The guest uuid.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return guestFacade.update(actorUuid, guest, uuid);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "Delete a guest.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public GuestDto delete(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Guest to delete.", required = true) GuestDto guest,
			@Parameter(description = "The guest uuid.", required = true)
				@PathParam("uuid") String uuid)
					throws BusinessException {
		return guestFacade.delete(actorUuid, guest, uuid);
	}
}
