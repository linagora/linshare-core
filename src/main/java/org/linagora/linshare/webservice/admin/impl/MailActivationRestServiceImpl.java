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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailActivationFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailActivationAdminDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailActivationRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/mail_activations")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailActivationRestServiceImpl extends WebserviceBase implements
		MailActivationRestService {

	protected final MailActivationFacade facade;

	public MailActivationRestServiceImpl(
			MailActivationFacade mailActivationFacade) {
		super();
		this.facade = mailActivationFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all domain's mail activations.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = FunctionalityAdminDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<MailActivationAdminDto> findAll(
			@Parameter(description = "Domain identifier.", required = false) @QueryParam(value = "domainId") String domainId)
			throws BusinessException {
		return facade.findAll(domainId);
	}

	@Path("/{mailActivationId}")
	@GET
	@Operation(summary = "Find a domain's mail activations.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = FunctionalityAdminDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailActivationAdminDto find(
			@Parameter(description = "Domain identifier.", required = false) @QueryParam(value = "domainId") String domainId,
			@Parameter(description = "Mail activation identifier.", required = true) @PathParam(value = "mailActivationId") String mailActivationId)
			throws BusinessException {
		return facade.find(domainId, mailActivationId);
	}

	@Path("/{mailActivationId}")
	@HEAD
	@Operation(summary = "Find a domain's mail activations.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = FunctionalityAdminDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void head(
			@Parameter(description = "Domain identifier.", required = false) @QueryParam(value = "domainId") String domainId,
			@Parameter(description = "Mail activation identifier.", required = true) @PathParam(value = "mailActivationId") String mailActivationId)
					throws BusinessException {
		facade.find(domainId, mailActivationId);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a domain's mail activations.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = FunctionalityAdminDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailActivationAdminDto update(MailActivationAdminDto mailActivation)
			throws BusinessException {
		return facade.update(mailActivation);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a domain's mail activations.")
	@Override
	public void delete(MailActivationAdminDto mailActivation)
			throws BusinessException {
		facade.delete(mailActivation);
	}

}
