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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.AccountQuotaDto;
import org.linagora.linshare.webservice.admin.AccountQuotaRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Path("/quotas")
public class AccountQuotaRestServiceImpl implements AccountQuotaRestService {

	private AccountQuotaFacade facade;

	public AccountQuotaRestServiceImpl(AccountQuotaFacade facade) {
		this.facade = facade;
	}

	@Path("/accounts")
	@GET
	@Override
	public List<AccountQuotaDto> findAll(@QueryParam("domainUuid") String domainUuid, @QueryParam("type") ContainerQuotaType type) throws BusinessException {
		return facade.findAll(domainUuid, type);
	}

	@Path("/accounts/{uuid}")
	@GET
	@Operation(summary = "find account quota", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public AccountQuotaDto find(
			@Parameter(description = "Account quota Uuid", required = true)
				@PathParam("uuid")
					String uuid,
			@Parameter(description = "Compute real time quota value. Carefull it could be time consuming.", required = false)
				@QueryParam("realtime") @DefaultValue("false")
					boolean realTime
			) throws BusinessException {
		return facade.find(uuid, realTime);
	}

	@Path("/accounts/{uuid : .*}")
	@PUT
	@Operation(summary = "Update a account quota", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public AccountQuotaDto update(
			@Parameter(description = "Account quota to update. Only quota, maxFileSize, override and maintenance fields can be updated. If null they will be ignored.", required = true) AccountQuotaDto dto,
			@Parameter(description = "Account quota Uuid, if null dto.uuid is used.", required = false)
			@PathParam("uuid") String uuid) throws BusinessException {
		return facade.update(dto, uuid);
	}
}
