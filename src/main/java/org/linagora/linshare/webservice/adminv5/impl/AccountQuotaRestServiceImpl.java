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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.Date;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.fields.AccountQuotaDtoField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AccountQuotaDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.AccountQuotaRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Produces({MediaType.APPLICATION_JSON })
@Consumes({MediaType.APPLICATION_JSON })
@Path("/domains/{domainUuid}/statistics/account_quotas")
public class AccountQuotaRestServiceImpl extends WebserviceBase implements AccountQuotaRestService {

	private final AccountQuotaFacade accountQuotaFacade;

	private static PagingResponseBuilder<AccountQuotaDto> pageResponseBuilder = new PagingResponseBuilder<>();

	public AccountQuotaRestServiceImpl(AccountQuotaFacade accountQuotaFacade) {
		super();
		this.accountQuotaFacade = accountQuotaFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Get storage consumption statistics. By default will be a 12 rolling months.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response findAll(
			@Parameter(description = "domain's uuid")
				@PathParam("domainUuid") String domainUuid,
			@Parameter(
					description = "Include nested domains.",
					schema = @Schema(implementation = Boolean.class)
				)
				@QueryParam("includeNestedDomains") @DefaultValue("false") boolean includeNestedDomains,
			@Parameter(
					description = "The admin can choose the order of sorting the account quota list to retrieve, if not set the ascending order will be applied by default.",
					required = false,
					schema = @Schema(implementation = SortOrder.class, defaultValue = "ASC")
				)
				@QueryParam("sortOrder") @DefaultValue("ASC") String sortOrder,
			@Parameter(
					description = "The admin can choose the field to sort with the account quota list to retrieve, if not set the batch modification date date order will be choosen by default.",
					required = false,
					schema = @Schema(implementation = AccountQuotaDtoField.class, defaultValue = "batchModificationDate")
				)
				@QueryParam("sortField") @DefaultValue("batchModificationDate") String sortField,
			@Parameter(description = "It allows administrator to retrieve account quota with used space greater than the entered value.", required = false)
				@QueryParam("greaterThanOrEqualTo") Long greaterThanOrEqualTo,
			@Parameter(description = "It allows administrator to retrieve account quota with used space less than the entered value.", required = false)
				@QueryParam("lessThanOrEqualTo") Long lessThanOrEqualTo,
			@Parameter(
					description = "Filter by container type.",
					schema = @Schema(implementation = ContainerQuotaType.class)
				)
				@QueryParam("type") String containerQuotaType,
			@Parameter(
					description = "Filter by account quota modification date. format: yyyy-MM-dd",
					schema = @Schema(implementation = Date.class)
				)
				@QueryParam("beginDate") String beginDate,
			@Parameter(
					description = "Filter by account quota modification date. format: yyyy-MM-dd",
					schema = @Schema(implementation = Date.class)
				)
				@QueryParam("endDate") String endDate,
			@Parameter(
					description = "The admin can choose the page number to get.",
					required = false
				)
				@QueryParam("page") Integer pageNumber,
			@Parameter(
					description = "The admin can choose the number of elements to get.",
					required = false
				)
				@QueryParam("size") @DefaultValue("100") Integer pageSize) {
		PageContainer<AccountQuotaDto> container = accountQuotaFacade.findAll(
				domainUuid, includeNestedDomains,
				SortOrder.valueOf(sortOrder),
				AccountQuotaDtoField.valueOf(sortField),
				Optional.ofNullable(greaterThanOrEqualTo),
				Optional.ofNullable(lessThanOrEqualTo),
				Optional.ofNullable(containerQuotaType),
				Optional.ofNullable(beginDate),
				Optional.ofNullable(endDate), pageNumber,
				pageSize);
		return pageResponseBuilder.build(container);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find an account quota.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = AccountQuotaDto.class)), responseCode = "200")
	})
	@Override
	public AccountQuotaDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "account quota's uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(
					description = "realTime for usedSpace.",
					schema = @Schema(implementation = Boolean.class)
				)
				@QueryParam("realTime") @DefaultValue("false") boolean realTime)
			throws BusinessException {
		return accountQuotaFacade.find(domainUuid, uuid, realTime);
	}
}
