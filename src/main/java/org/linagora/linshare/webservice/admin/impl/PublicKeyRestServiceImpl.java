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
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.PublicKeyFacade;
import org.linagora.linshare.mongo.entities.PublicKeyLs;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.PublicKeyRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/public_keys")
@Api(value = "/rest/admin/public_keys", description = "Public keys API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class PublicKeyRestServiceImpl extends WebserviceBase implements PublicKeyRestService {

	private final PublicKeyFacade publicKeyFacade;

	public PublicKeyRestServiceImpl(PublicKeyFacade publicKeyFacade) {
		super();
		this.publicKeyFacade = publicKeyFacade;
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find public key by uuid.", response = PublicKeyLs.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission."),
			@ApiResponse(code = 404, message = "PublicKeys not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public PublicKeyLs find(
			@ApiParam(value = "public key uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return publicKeyFacade.find(uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Store a new public key.", response = PublicKeyLs.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission."),
			@ApiResponse(code = 404, message = "PublicKeys not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public PublicKeyLs create(
			@ApiParam(value = "New public key", required = true)
				PublicKeyLs publicKeyLs) throws BusinessException {
		return publicKeyFacade.create(publicKeyLs);
	}

	@GET
	@Path("/domain/{domainUuid}")
	@ApiOperation(value = "Find a list of public keys by Domain uuid.", response = PublicKeyLs.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission.") ,
					@ApiResponse(code = 404, message = "PublicKeys not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
		})
	@Override
	public List<PublicKeyLs> findAll(
			@ApiParam(value = "Domain uuid", required = true)
				@PathParam("domainUuid") String domainUuid) throws BusinessException {
		return publicKeyFacade.findAll(domainUuid);
	}

	@Path("/{uuid : .*}")
	@DELETE
	@ApiOperation(value = "Delete a public keys.", response = PublicKeyLs.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission."),
			@ApiResponse(code = 404, message = "PublicKeys not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public PublicKeyLs delete(
			@ApiParam(value = "public key to delete", required = true)
				PublicKeyLs publicKeyLs,
			@ApiParam(value = "public key uuid to delete", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return publicKeyFacade.delete(uuid, publicKeyLs);
	}

	@GET
	@Path("/audit/{domainUuid}")
	@ApiOperation(value = "Get all traces for a public keys.", response = AuditLogEntryUser.class, responseContainer="Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission."),
					@ApiResponse(code = 404, message = "PublicKeyLs not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
		})
	@Override
	public Set<AuditLogEntryAdmin> findAll(
			@ApiParam(value = "The domain uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@ApiParam(value = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions) {
		return publicKeyFacade.findAll(domainUuid, actions);
	}
}
