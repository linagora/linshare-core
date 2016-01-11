/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.webservice.delegation.impl;

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

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareEntryGroupDto;
import org.linagora.linshare.core.facade.webservice.delegation.ShareEntryGroupFacade;
import org.linagora.linshare.webservice.delegation.ShareEntryGroupRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("{ownerUuid}/share_entry_group")
@Api(value = "/rest/delegation/{ownerUuid}/share_entry_group", basePath = "/rest/share_entry_group", description = "share entries group delegation service.", produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ShareEntryGroupRestServiceImpl implements ShareEntryGroupRestService {

	private final ShareEntryGroupFacade shareEntryGroupFacade;

	public ShareEntryGroupRestServiceImpl(ShareEntryGroupFacade shareEntryGroupFacade) {
		this.shareEntryGroupFacade = shareEntryGroupFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all share entries group for an user.", response = ShareEntryGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<ShareEntryGroupDto> findAll(
			@ApiParam(value = "Share entry group's owner uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@QueryParam("full") @DefaultValue("false") boolean full) throws BusinessException {
		return shareEntryGroupFacade.findAll(ownerUuid, full);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a share entry group.", response = ShareEntryGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Share entry group not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ShareEntryGroupDto find(
			@ApiParam(value = "Share entry group's owner uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Share entry group's uuid to find.", required = true) @PathParam("uuid") String uuid,
			@QueryParam("full") @DefaultValue("false") boolean full) throws BusinessException {
		return shareEntryGroupFacade.find(ownerUuid, uuid, full);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a share entry group.", response = ShareEntryGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Share entry group not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public void head(
			@ApiParam(value = "Share entry group's owner uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Share entry group's uuid to find.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		shareEntryGroupFacade.find(ownerUuid, uuid, false);
	}

	@Path("/{uuid}")
	@PUT
	@ApiOperation(value = "Update a share entry group.", response = ShareEntryGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Share entry group not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ShareEntryGroupDto update(
			@ApiParam(value = "Share entry group's owner uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Share entry group to update.", required = true) @PathParam("shareEntryGroupDto") ShareEntryGroupDto shareEntryGroupDto)
					throws BusinessException {
		return shareEntryGroupFacade.update(ownerUuid, shareEntryGroupDto);
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a share entry group.", response = ShareEntryGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Share entry group not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ShareEntryGroupDto delete(
			@ApiParam(value = "Share entry group's owner uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Share entry group's uuid to delete.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return shareEntryGroupFacade.delete(ownerUuid, uuid);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a share entry group.", response = ShareEntryGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Share entry group not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public ShareEntryGroupDto delete(
			@ApiParam(value = "Share entry group's owner uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Share entry group to delete.", required = true) @PathParam("uuid") ShareEntryGroupDto shareEntryGroupDto)
					throws BusinessException {
		Validate.notNull(shareEntryGroupDto);
		return shareEntryGroupFacade.delete(ownerUuid, shareEntryGroupDto.getUuid());
	}
}
