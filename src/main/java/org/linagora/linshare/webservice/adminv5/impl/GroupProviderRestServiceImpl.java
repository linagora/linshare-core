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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPGroupProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.GroupProviderFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractGroupProviderDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.GroupProviderRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/domains/{domainUuid}/group_providers")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class GroupProviderRestServiceImpl extends WebserviceBase implements
	GroupProviderRestService {

	private final GroupProviderFacade groupProviderFacade;

	public GroupProviderRestServiceImpl(final GroupProviderFacade groupProviderFacade) {
		this.groupProviderFacade = groupProviderFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "It will return all group providers of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(array = @ArraySchema(
				schema = @Schema(
					oneOf = {LDAPGroupProviderDto.class},
					type = "object"
				))
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
	public Set<AbstractGroupProviderDto> findAll(
			@Parameter(description = "The domain uuid.")
				@PathParam("domainUuid") String domain) {
		return groupProviderFacade.findAll(domain);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "It will return the detail of a group provider of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPGroupProviderDto.class},
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
	public AbstractGroupProviderDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "groupProvider's uuid.", required = true)
				@PathParam("uuid") String uuid
				) {
		return groupProviderFacade.find(domainUuid, uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "It allows root administrator to create a new group provider.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPGroupProviderDto.class},
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
	public AbstractGroupProviderDto create(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@RequestBody(description = "groupProvider dto to create", required = true,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPGroupProviderDto.class},
						type = "object"
					)
				)
			)
			AbstractGroupProviderDto dto) {
		return groupProviderFacade.create(domainUuid, dto);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "It allows administrator to update a groupProvider.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPGroupProviderDto.class},
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
	public AbstractGroupProviderDto update(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "groupProvider's uuid to update, if null, object.uuid is used", required = false)
				@PathParam("uuid") String uuid,
			@RequestBody(description = "groupProvider dto with properties to update", required = false,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPGroupProviderDto.class},
						type = "object"
					)
				)
			)
			AbstractGroupProviderDto dto) {
		return groupProviderFacade.update(domainUuid, uuid, dto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "It allows administrator to delete a groupProvider.", responses = {
			@ApiResponse(
				responseCode = "200",
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPGroupProviderDto.class},
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
	public AbstractGroupProviderDto delete(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "groupProvider uuid to delete, if null, object.uuid is used", required = false)
				@PathParam("uuid") String uuid,
			@RequestBody(description = "groupProvider uuid to delete, if null, object.uuid is used", required = false,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPGroupProviderDto.class},
						type = "object"
					)
				)
			)
			AbstractGroupProviderDto dto) {
		return groupProviderFacade.delete(domainUuid, uuid, dto);
	}
}
