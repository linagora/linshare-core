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

package org.linagora.linshare.webservice.admin.impl;

import java.util.Set;

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
import org.linagora.linshare.core.facade.webservice.admin.WelcomeMessagesFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.WelcomeMessagesDto;
import org.linagora.linshare.webservice.admin.WelcomeMessagesRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/welcome_messages")
@Api(value = "/rest/admin/welcome_messages", description = "Welcome messages administration")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WelcomeMessagesRestServiceImpl implements
		WelcomeMessagesRestService {

	private final WelcomeMessagesFacade welcomeMessagesFacade;

	public WelcomeMessagesRestServiceImpl(final WelcomeMessagesFacade wlcmFacade) {
		this.welcomeMessagesFacade = wlcmFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all welcome message entries.", response = WelcomeMessagesDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin."),
			@ApiResponse(code = 400, message = "Bad request.") })
	@Override
	public Set<WelcomeMessagesDto> findAll(
			@ApiParam(value = "If not set, actor domain will be use.", required = true) @QueryParam("domainId") String domainId,
			@QueryParam("parent") @DefaultValue("false") Boolean parent)
			throws BusinessException {
		return welcomeMessagesFacade.findAll(domainId, parent);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a welcome message entry.", response = WelcomeMessagesDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't admin."),
			@ApiResponse(code = 404, message = "Welcome message not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields.") })
	@Override
	public WelcomeMessagesDto find(
			@ApiParam(value = "Welcome message uuid", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return welcomeMessagesFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a welcome message entry.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't admin."),
			@ApiResponse(code = 404, message = "Welcome message not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields.") })
	@Override
	public void head(
			@ApiParam(value = "Welcome message uuid", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		welcomeMessagesFacade.find(uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a welcome message entry.", response = WelcomeMessagesDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't admin."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields.") })
	@Override
	public WelcomeMessagesDto create(
			@ApiParam(value = "Welcome message to create (uuid is required because we will duplicate an existing WelcomeMessage", required = true) WelcomeMessagesDto customDto)
			throws BusinessException {
		return welcomeMessagesFacade.create(customDto);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a welcome message entry.", response = WelcomeMessagesDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't admin."),
			@ApiResponse(code = 404, message = "Welcome message not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields.") })
	@Override
	public WelcomeMessagesDto update(
			@ApiParam(value = "Welcome message updated", required = true) WelcomeMessagesDto customDto)
			throws BusinessException {
		return welcomeMessagesFacade.update(customDto);
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a welcome message entry.", response = WelcomeMessagesDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't admin."),
			@ApiResponse(code = 404, message = "Welcome message not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields.") })
	@Override
	public WelcomeMessagesDto delete(
			@ApiParam(value = "Welcome message uuid", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return welcomeMessagesFacade.delete(uuid);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a welcome message entry.", response = WelcomeMessagesDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't admin."),
			@ApiResponse(code = 404, message = "Welcome message not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields.") })
	@Override
	public WelcomeMessagesDto delete(
			@ApiParam(value = "Welcome message to delete", required = true) WelcomeMessagesDto customDto)
			throws BusinessException {
		return welcomeMessagesFacade.delete(customDto);
	}
}