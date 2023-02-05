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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.TechnicalAccountFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.TechnicalAccountDto;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.TechnicalAccountRestService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/technical_accounts")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class TechnicalAccountRestServiceImpl extends WebserviceBase implements
		TechnicalAccountRestService {

	protected final TechnicalAccountFacade technicalAccountFacade;

	public TechnicalAccountRestServiceImpl(
			TechnicalAccountFacade technicalAccountFacade) {
		super();
		this.technicalAccountFacade = technicalAccountFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all technical accounts.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<TechnicalAccountDto> findAll() throws BusinessException {
		return technicalAccountFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto find(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return technicalAccountFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a technical account.")
	@Override
	public void head(@PathParam(value = "uuid") String uuid) throws BusinessException {
		technicalAccountFacade.find(uuid);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto update(TechnicalAccountDto account)
			throws BusinessException {
		return technicalAccountFacade.update(account);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto create(TechnicalAccountDto account)
			throws BusinessException {
		return technicalAccountFacade.create(account, Version.V1);
	}

	@Path("/{uuid}/change_password")
	@POST
	@Operation(summary = "Change the password of a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void changePassword(@PathParam(value = "uuid") String uuid, PasswordDto password) throws BusinessException {
		technicalAccountFacade.changePassword(uuid, password);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto delete(TechnicalAccountDto account) throws BusinessException {
		return technicalAccountFacade.delete(account);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto delete(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return technicalAccountFacade.delete(uuid);
	}
}
