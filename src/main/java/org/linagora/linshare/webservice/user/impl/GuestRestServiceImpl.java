/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

package org.linagora.linshare.webservice.user.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.webservice.dto.UserDto;
import org.linagora.linshare.webservice.user.GuestRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/guests")
@Api(value = "/rest/guests", description = "Guests service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class GuestRestServiceImpl implements GuestRestService {

	private final GuestFacade guestFacade;

	public GuestRestServiceImpl(final GuestFacade guestFacade) {
		this.guestFacade = guestFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all guest of a user.", response = UserDto.class)
	@Override
	public List<UserDto> findAll(String ownerLsUuid) throws BusinessException {
		return null;
	}

	@Path("/{lsUuid}")
	@GET
	@ApiOperation(value = "Find a guest.")
	@Override
	public UserDto find(
			@ApiParam(value = "Guest's lsUuid.", required = true) @PathParam("lsUuid") String lsUuid)
			throws BusinessException {
		return guestFacade.find(lsUuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a guest.")
	@Override
	public UserDto create(
			@ApiParam(value = "Guest to create.", required = true) UserDto guest,
			@ApiParam(value = "Guest owner lsuuid.") @QueryParam("ownerLsUuid") @DefaultValue("null") String ownerLsUuid)
			throws BusinessException {
		return guestFacade.create(guest, ownerLsUuid);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a guest.")
	@Override
	public UserDto update(
			@ApiParam(value = "Guest to update.", required = true) UserDto guest)
			throws BusinessException {
		return guestFacade.update(guest);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a guest.")
	@Override
	public void delete(
			@ApiParam(value = "Guest to delete.", required = true) UserDto guest)
			throws BusinessException {
		guestFacade.delete(guest);
	}

	@Path("/{lsUuid}")
	@DELETE
	@ApiOperation(value = "Delete a guest.")
	@Override
	public void delete(
			@ApiParam(value = "Guest's lsUuid to create.", required = true) @PathParam("lsUuid") String lsUuid)
			throws BusinessException {
		guestFacade.delete(lsUuid);
	}
}