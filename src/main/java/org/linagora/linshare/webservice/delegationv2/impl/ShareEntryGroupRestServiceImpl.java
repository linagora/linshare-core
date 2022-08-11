/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
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

package org.linagora.linshare.webservice.delegationv2.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareEntryGroupDto;
import org.linagora.linshare.core.facade.webservice.delegation.ShareEntryGroupFacade;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.delegationv2.ShareEntryGroupRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("{actorUuid}/share_entry_group")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ShareEntryGroupRestServiceImpl implements ShareEntryGroupRestService {

	private final ShareEntryGroupFacade shareEntryGroupFacade;

	public ShareEntryGroupRestServiceImpl(ShareEntryGroupFacade shareEntryGroupFacade) {
		this.shareEntryGroupFacade = shareEntryGroupFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all share entries group for an user.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<ShareEntryGroupDto> findAll(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@QueryParam("full") @DefaultValue("false") boolean full) throws BusinessException {
		return shareEntryGroupFacade.findAll(Version.V2, actorUuid, full);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareEntryGroupDto find(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group's uuid to find.", required = true) @PathParam("uuid") String uuid,
			@QueryParam("full") @DefaultValue("false") boolean full) throws BusinessException {
		return shareEntryGroupFacade.find(Version.V2, actorUuid, uuid, full);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void head(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group's uuid to find.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		shareEntryGroupFacade.find(Version.V2, actorUuid, uuid, false);
	}

	@Path("/{uuid}")
	@PUT
	@Operation(summary = "Update a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareEntryGroupDto update(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group to update.", required = true) @PathParam("shareEntryGroupDto") ShareEntryGroupDto shareEntryGroupDto)
					throws BusinessException {
		return shareEntryGroupFacade.update(actorUuid, shareEntryGroupDto);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareEntryGroupDto delete(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group's uuid to delete.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return shareEntryGroupFacade.delete(actorUuid, uuid);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a share entry group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareEntryGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ShareEntryGroupDto delete(
			@Parameter(description = "Share entry group's actor uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Share entry group to delete.", required = true) @PathParam("uuid") ShareEntryGroupDto shareEntryGroupDto)
					throws BusinessException {
		Validate.notNull(shareEntryGroupDto);
		return shareEntryGroupFacade.delete(actorUuid, shareEntryGroupDto.getUuid());
	}
}
