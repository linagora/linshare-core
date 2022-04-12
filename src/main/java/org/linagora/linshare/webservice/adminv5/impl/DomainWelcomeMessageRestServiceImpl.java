/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.WelcomeMessageFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.WelcomeMessageAssignDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.WelcomeMessageDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.DomainWelcomeMessageRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/domains/{domainUuid}/welcome_messages")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class DomainWelcomeMessageRestServiceImpl extends WebserviceBase implements DomainWelcomeMessageRestService {

	private final WelcomeMessageFacade welcomeMessageFacade;

	public DomainWelcomeMessageRestServiceImpl(WelcomeMessageFacade welcomeMessageFacade) {
		this.welcomeMessageFacade = welcomeMessageFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "It will return all welcome messages of the current domain and its parent.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WelcomeMessageDto.class)))
		)
	})
	@Override
	public List<WelcomeMessageDto> findAll(
			@Parameter(description = "The domain uuid.")
				@PathParam("domainUuid") String domainUuid) {
		return welcomeMessageFacade.findAll(domainUuid);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "It will return the detail of the welcome message of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(implementation = WelcomeMessageDto.class)
			)
		)
	})
	@Override
	public WelcomeMessageDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "welcomeMessage's uuid.", required = true)
				@PathParam("uuid") String welcomeMessageUuid
				) {
		return welcomeMessageFacade.find(domainUuid, welcomeMessageUuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "It allows to create a new welcome message.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(implementation = WelcomeMessageDto.class)
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
	public WelcomeMessageDto create(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "Welcome message to create", required = true) WelcomeMessageDto welcomeMessageDto)
			throws BusinessException {
		return welcomeMessageFacade.create(domainUuid, welcomeMessageDto);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "It allows to update a welcome message.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(implementation = WelcomeMessageDto.class)
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
	public WelcomeMessageDto update(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "welcomeMessage's uuid, if null, object.uuid is used.", required = false)
				@PathParam("uuid") String welcomeMessageUuid,
			@Parameter(description = "Welcome message to update.", required = true) WelcomeMessageDto welcomeMessageDto)
			throws BusinessException {
		return welcomeMessageFacade.update(domainUuid, welcomeMessageUuid, welcomeMessageDto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "It allows to delete a welcome message.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(implementation = WelcomeMessageDto.class)
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
	public WelcomeMessageDto delete(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "welcomeMessage's uuid to delete.", required = false)
				@PathParam("uuid") String welcomeMessageUuid,
			@Parameter(description = "Welcome message to delete.", required = true) WelcomeMessageDto welcomeMessageDto)
			throws BusinessException {
		return welcomeMessageFacade.delete(domainUuid, welcomeMessageUuid, welcomeMessageDto);
	}

	@Path("/{uuid}/assign")
	@PUT
	@Operation(summary = "It allows to assign a welcome message.", responses = {
			@ApiResponse(
					responseCode = "200",
					content = @Content(
							schema = @Schema(implementation = WelcomeMessageDto.class)
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
	public WelcomeMessageDto assign(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "welcomeMessage's uuid to assign.", required = true)
				@PathParam("uuid") String welcomeMessageUuid,
			@Parameter(description = "Welcome message assign object.", required = true) WelcomeMessageAssignDto assignDto)
			throws BusinessException {
		return welcomeMessageFacade.assign(domainUuid, welcomeMessageUuid, assignDto);
	}
}
