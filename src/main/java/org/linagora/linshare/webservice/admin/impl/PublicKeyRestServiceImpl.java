/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.webservice.admin.impl;

import java.io.InputStream;
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

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.PublicKeyFormat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.PublicKeyFacade;
import org.linagora.linshare.mongo.entities.PublicKeyLs;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.PublicKeyRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/public_keys")
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
	@Operation(summary = "Find public key by uuid.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicKeyLs.class))),
			responseCode = "200"
		)
	})
	@Override
	public PublicKeyLs find(
			@Parameter(description = "public key uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return publicKeyFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Store a new public key.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicKeyLs.class))),
			responseCode = "200"
		)
	})
	@Override
	public PublicKeyLs create(
			@Parameter(description = "New public key", required = true)
				PublicKeyLs publicKeyLs) throws BusinessException {
		return publicKeyFacade.create(publicKeyLs);
	}

	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(summary = "Store a new public key from file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicKeyLs.class))),
			responseCode = "200"
		)
	})
	@Override
	public PublicKeyLs create(
			@Parameter(description = "New temp file containing public key", required = true)
			@Multipart(value = "file", required = true)
				InputStream publicKeyInputS,
			@Parameter(description = "The name for Domain Uuid ", required = true)
			@Multipart(value = "domainUuid", required = true)
				String domainUuid,
			@Parameter(description = "The name of issuer", required = true)
			@Multipart(value = "issuer", required = true)
				String issuer,
			@Parameter(description = "Type of Format", required = true)
			@Multipart(value = "formatType", required = true)
				PublicKeyFormat format) throws BusinessException {
		return publicKeyFacade.create(publicKeyInputS, domainUuid, issuer, format);
	}

	@Path("/")
	@GET
	@Operation(summary = "Find a list of public keys by Domain uuid.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicKeyLs.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<PublicKeyLs> findAll(
			@Parameter(description = "Domain uuid", required = true)
				@QueryParam("domainUuid") String domainUuid) throws BusinessException {
		return publicKeyFacade.findAll(domainUuid);
	}

	@Path("/{uuid : .*}")
	@DELETE
	@Operation(summary = "Delete a public keys.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicKeyLs.class))),
			responseCode = "200"
		)
	})
	@Override
	public PublicKeyLs delete(
			@Parameter(description = "public key to delete", required = true)
				PublicKeyLs publicKeyLs,
			@Parameter(description = "public key uuid to delete", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return publicKeyFacade.delete(uuid, publicKeyLs);
	}

	@Path("/audit/{domainUuid}")
	@GET
	@Operation(summary = "Get all traces for a public keys.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryAdmin> findAll(
			@Parameter(description = "The domain uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions) {
		return publicKeyFacade.findAll(domainUuid, actions);
	}
}
