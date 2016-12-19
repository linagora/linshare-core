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

package org.linagora.linshare.webservice.userv1.impl;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.webservice.userv1.GuestRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/guests")
@Api(value = "/rest/user/guests", description = "Guests service")
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
	@ApiOperation(value = "Find all guests of a user.", response = GuestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "No permission to list all guests."),
			@ApiResponse(code = 404, message = "Guests not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<GuestDto> findAll(
			@QueryParam("all") @DefaultValue("false") boolean all,
			@QueryParam("pattern") String pattern) throws BusinessException {
		if (all) {
			return guestFacade.findAll(null, pattern);
		}
		return guestFacade.findAll(true, pattern);
	}

	@Path("/search")
	@POST
	@ApiOperation(value = "Search all guests who match with some pattern.", response = GuestDto.class, responseContainer = "List")
	@Override
	public List<GuestDto> search(
			@ApiParam(value = "Patterns to search.", required = true) UserSearchDto userSearchDto)
			throws BusinessException {
		Validate.isTrue(!(
				lessThan3Char(userSearchDto.getFirstName())
				&& lessThan3Char(userSearchDto.getLastName())
				&& lessThan3Char(userSearchDto.getMail())),
				"One pattern is required, pattern must be greater than 3 characters");
		return guestFacade.search(userSearchDto);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a guest.", response = GuestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Guest not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public GuestDto find(@ApiParam(value = "Guest's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return guestFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a guest.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Guest not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public void head(@ApiParam(value = "Guest's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		guestFacade.find(uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a guest.", response = GuestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "No permission to create."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public GuestDto create(@ApiParam(value = "Guest to create.", required = true) GuestDto guest)
			throws BusinessException {
		return guestFacade.create(guest);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a guest.", response = GuestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "No permission to update."),
			@ApiResponse(code = 404, message = "Guest not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public GuestDto update(@ApiParam(value = "Guest to update.", required = true) GuestDto guest)
			throws BusinessException {
		return guestFacade.update(guest, null);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a guest.", response = GuestDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "No permission to delete."),
			@ApiResponse(code = 404, message = "Guest not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public GuestDto delete(
			@ApiParam(value = "Guest to delete.", required = true) GuestDto guest)
			throws BusinessException {
		return guestFacade.delete(guest);
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a guest.", response = GuestDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "No permission to delete."),
			@ApiResponse(code = 404, message = "Guest not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public GuestDto delete(
			@ApiParam(value = "Guest's uuid to delete.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return guestFacade.delete(uuid);
	}

	private boolean lessThan3Char(String s) {
		return StringUtils.trimToEmpty(s).length() < 3;
	}
}
