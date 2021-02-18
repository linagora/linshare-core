/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
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

package org.linagora.linshare.webservice.userv2.impl;

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
import org.linagora.linshare.core.facade.webservice.user.ShareEntryGroupFacade;
import org.linagora.linshare.webservice.userv2.ShareEntryGroupRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


//Class created to generate the swagger documentation of v1 RestServices
@Path("/share_entry_group")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ShareEntryGroupRestServiceImpl implements ShareEntryGroupRestService {

	private final ShareEntryGroupFacade facade;

	public ShareEntryGroupRestServiceImpl(ShareEntryGroupFacade facade) {
		this.facade = facade;
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
	public List<ShareEntryGroupDto> findAll(@QueryParam("full") @DefaultValue("false") boolean full)
			throws BusinessException {
		List<ShareEntryGroupDto> data = facade.findAll(full);
		return data;
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
			@Parameter(description = "Share entry grooup uuid.", required = true) @PathParam("uuid") String uuid,
			@QueryParam("full") @DefaultValue("false") boolean full) throws BusinessException {
		ShareEntryGroupDto dto = facade.find(uuid, full);
		return dto;
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a share entry group.")
	@Override
	public void head(@Parameter(description = "Share entry grooup uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		facade.find(uuid, false);
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
	public ShareEntryGroupDto update(ShareEntryGroupDto shareEntryGroupDto) throws BusinessException {
		ShareEntryGroupDto dto = facade.update(shareEntryGroupDto);
		return dto;
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
			@Parameter(description = "Share entry group's uuid to delete.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		ShareEntryGroupDto dto = facade.delete(uuid);
		return dto;
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
			@Parameter(description = "Share entry group to delete.", required = true) ShareEntryGroupDto shareEntryGroupDto)
					throws BusinessException {
		Validate.notNull(shareEntryGroupDto);
		ShareEntryGroupDto dto = facade.delete(shareEntryGroupDto.getUuid());
		return dto;
	}
}

