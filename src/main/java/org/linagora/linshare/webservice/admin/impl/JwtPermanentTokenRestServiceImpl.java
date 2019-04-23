/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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
package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.linagora.linshare.core.facade.webservice.admin.JwtLongTimeTokenFacade;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.linagora.linshare.webservice.admin.JwtPermanentTokenRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/jwt")
@Api(value = "/rest/admin/jwt", description = "JWT permanent token service")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class JwtPermanentTokenRestServiceImpl implements JwtPermanentTokenRestService {

	private final JwtLongTimeTokenFacade jwtLongTimeTokenFacade ;

	public JwtPermanentTokenRestServiceImpl(JwtLongTimeTokenFacade jwtLongTimeTokenFacade) {
		super();
		this.jwtLongTimeTokenFacade = jwtLongTimeTokenFacade;
	}

	@Path("/{uuid}")
	@GET
	@Override
	@ApiOperation(value = "Find JWT permanent tokens owned by uuid.", response = PermanentToken.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	public PermanentToken find(
			@ApiParam(value = "token uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return jwtLongTimeTokenFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Override
	@ApiOperation(value = "Create a JWT permanent token for others users.", response = PermanentToken.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error.") })
	public PermanentToken create(
			@ApiParam(value = "Permanent token to create from two fields : label (mandatory), description (optional), actorUuid (mandatory)", required = true) PermanentToken permanentToken)
			throws BusinessException {
		return jwtLongTimeTokenFacade.create(permanentToken);
	}

	@Path("/{uuid}")
	@HEAD
	@Override
	@ApiOperation(value = "Check if JWT permanent tokens exists.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	public void head(
			@ApiParam(value = "token uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		jwtLongTimeTokenFacade.find(uuid);
	}

	@Path("/")
	@GET
	@Override
	@ApiOperation(value = "Find all JWT permanent tokens of admin domain and recursivly if uuid is not specified.", response = PermanentToken.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	public List<PermanentToken> findAll(
			@ApiParam(value = "domain uuid.", required = false)
			@QueryParam("domainUuid") String domainUuid) throws BusinessException {
		return jwtLongTimeTokenFacade.findAll(domainUuid);
	}

	@Path("/findbyactor/{actoruuid}")
	@GET
	@Override
	@ApiOperation(value = "Find all JWT permanent tokens of admin domain.", response = PermanentToken.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	public List<PermanentToken> findAllByActor(
			@ApiParam(value = "actor uuid.", required = true)
				@PathParam("actoruuid") String actorUuid) throws BusinessException {
		return jwtLongTimeTokenFacade.findAllByActor(actorUuid);
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
					PermanentToken jwtLongTime,
			@ApiParam(value = "token uuid, if null object is used", required = false)
					@PathParam("uuid") String uuid) throws BusinessException {
		return jwtLongTimeTokenFacade.delete(jwtLongTime, uuid);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Override
	@ApiOperation(value = "Update JWT permanent token by its uuid.", response = PermanentToken.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User is not allowed to use this endpoint"),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 404, message = "The requested token has not been found."),
					@ApiResponse(code = 500, message = "Internal server error.") })
	public PermanentToken update(
			@ApiParam(value = "Permanent token to update.", required = true)
					PermanentToken permanentToken,
			@ApiParam(value = "token uuid, if null object is used", required = false)
					@PathParam("uuid") String uuid) throws BusinessException {
		return jwtLongTimeTokenFacade.update(permanentToken, uuid);
	}

}
