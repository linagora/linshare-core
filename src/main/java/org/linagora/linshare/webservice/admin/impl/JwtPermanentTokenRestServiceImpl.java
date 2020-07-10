/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/jwt")
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
	@Operation(summary = "Find JWT permanent tokens owned by uuid.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermanentToken.class))),
			responseCode = "200"
		)
	})
	public PermanentToken find(
			@Parameter(description = "token uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return jwtLongTimeTokenFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Override
	@Operation(summary = "Create a JWT permanent token for others users.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermanentToken.class))),
			responseCode = "200"
		)
	})
	public PermanentToken create(
			@Parameter(description = "Permanent token to create from two fields : label (mandatory), description (optional), actorUuid (mandatory)", required = true) PermanentToken permanentToken)
			throws BusinessException {
		return jwtLongTimeTokenFacade.create(permanentToken);
	}

	@Path("/{uuid}")
	@HEAD
	@Override
	@Operation(summary = "Check if JWT permanent tokens exists.")
	public void head(
			@Parameter(description = "token uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		jwtLongTimeTokenFacade.find(uuid);
	}

	@Path("/")
	@GET
	@Override
	@Operation(summary = "Find all JWT permanent tokens of admin domain and recursivly if uuid is not specified.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermanentToken.class))),
			responseCode = "200"
		)
	})
	public List<PermanentToken> findAll(
			@Parameter(description = "domain uuid.", required = false)
				@QueryParam("domainUuid") String domainUuid) throws BusinessException {
		return jwtLongTimeTokenFacade.findAll(domainUuid);
	}

	@Path("/findbyactor/{actoruuid}")
	@GET
	@Override
	@Operation(summary = "Find all JWT permanent tokens of admin domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermanentToken.class))),
			responseCode = "200"
		)
	})
	public List<PermanentToken> findAllByActor(
			@Parameter(description = "actor uuid.", required = true)
				@PathParam("actoruuid") String actorUuid) throws BusinessException {
		return jwtLongTimeTokenFacade.findAllByActor(actorUuid);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Override
	@Operation(summary = "Delete a JWT permanent token by its uuid.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermanentToken.class))),
			responseCode = "200"
		)
	})
	public PermanentToken delete(
			@Parameter(description = "Permanent token to delete.", required = true)
					PermanentToken jwtLongTime,
			@Parameter(description = "token uuid, if null object is used", required = false)
					@PathParam("uuid") String uuid) throws BusinessException {
		return jwtLongTimeTokenFacade.delete(jwtLongTime, uuid);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Override
	@Operation(summary = "Update JWT permanent token by its uuid.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermanentToken.class))),
			responseCode = "200"
		)
	})
	public PermanentToken update(
			@Parameter(description = "Permanent token to update.", required = true)
					PermanentToken permanentToken,
			@Parameter(description = "token uuid, if null object is used", required = false)
					@PathParam("uuid") String uuid) throws BusinessException {
		return jwtLongTimeTokenFacade.update(permanentToken, uuid);
	}

}
