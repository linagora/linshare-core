/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

package org.linagora.linshare.webservice.userv2.impl;

import java.util.List;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.JwtLongTimeFacade;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.userv2.JwtLongTimeRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/jwt")
@Api(value = "/rest/user/v2/jwt", basePath = "/rest/user/v2/", description = "JWT permanent token service", produces = "application/json,application/xml", consumes = "application/json,application/xml")
public class JwtLongTimeRestServiceImpl implements JwtLongTimeRestService {

	private final JwtLongTimeFacade jwtLongTimeFacade;

	public JwtLongTimeRestServiceImpl(JwtLongTimeFacade jwtLongTimeFacade) {
		super();
		this.jwtLongTimeFacade = jwtLongTimeFacade;
	}

	@Path("/")
	@POST
	@Override
	@ApiOperation(value = "Create a JWT permanent token.", response = PermanentToken.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error.") })
	public PermanentToken create(
			@ApiParam(value = "token label")
					@QueryParam("label") String label,
			@ApiParam(value = "token description")
					@QueryParam("description") String description)
			throws BusinessException {
		return jwtLongTimeFacade.create(label, description);
	}

	@Path("/")
	@GET
	@Override
	@ApiOperation(value = "Find all JWT permanent tokens owned by user.", response = PermanentToken.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	public List<PermanentToken> findAll() throws BusinessException {
		return jwtLongTimeFacade.findAll();
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Override
	@ApiOperation(value = "Delete a JWT permanent token by its uuid.", response = PermanentToken.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 404, message = "The requested token has not been found."),
					@ApiResponse(code = 500, message = "Internal server error.") })
	public PermanentToken delete(
			@ApiParam(value = "Permanent token to delete.", required = true)
					PermanentToken permanentToken,
			@ApiParam(value = "token uuid, if null object is used", required = false)
					@PathParam("uuid") String uuid) throws BusinessException {
		return jwtLongTimeFacade.delete(permanentToken, uuid);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Override
	@ApiOperation(value = "Update a JWT permanent token by its uuid.", response = PermanentToken.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 404, message = "The requested token has not been found."),
					@ApiResponse(code = 500, message = "Internal server error.") })
	public PermanentToken update(
			@ApiParam(value = "Permanent token to update.", required = true)
					PermanentToken permanentToken,
			@ApiParam(value = "token uuid, if null object is used", required = false)
					@PathParam("uuid") String uuid) throws BusinessException {
		return jwtLongTimeFacade.update(permanentToken, uuid);
	}

	@GET
	@Path("/audit")
	@ApiOperation(value = "Get all traces for a JWT permanent token.", response = AuditLogEntryUser.class, responseContainer="Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission."),
		@ApiResponse(code = 404, message = "The requested token has not been found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
		})
	@Override
	public Set<AuditLogEntryUser> findAllAudit(
			@ApiParam(value = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions) {
		return jwtLongTimeFacade.findAllAudit(actions);
	}
}
