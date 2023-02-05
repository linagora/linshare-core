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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.entities.fields.StorageConsumptionStatisticField;
import org.linagora.linshare.core.facade.webservice.adminv5.StorageConsumptionStatisticFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.StorageConsumptionStatisticDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.StorageConsumptionStatisticRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/domains/{domainUuid}/statistics/storage_consumptions")
@Produces({ MediaType.APPLICATION_JSON })
public class StorageConsumptionStatisticRestServiceImpl extends WebserviceBase
		implements StorageConsumptionStatisticRestService {

	private final StorageConsumptionStatisticFacade statisticFacade;

	private static PagingResponseBuilder<StorageConsumptionStatisticDto> pageResponseBuilder = new PagingResponseBuilder<>();

	public StorageConsumptionStatisticRestServiceImpl(StorageConsumptionStatisticFacade statisticFacade) {
		this.statisticFacade = statisticFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Get storage consumption statistics. By default will be a 12 rolling months.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = StorageConsumptionStatisticDto.class))),
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
					description = "The admin can choose the order of sorting the stat's list to retrieve, if not set the ascending order will be applied by default.",
					required = false,
					schema = @Schema(implementation = SortOrder.class, defaultValue = "ASC")
				)
				@QueryParam("sortOrder") @DefaultValue("ASC") String sortOrder,
			@Parameter(
					description = "The admin can choose the field to sort with the stat's list to retrieve, if not set the statisticDate date order will be choosen by default.",
					required = false,
					schema = @Schema(implementation = StorageConsumptionStatisticField.class, defaultValue = "statisticDate")
				)
				@QueryParam("sortField") @DefaultValue("statisticDate") String sortField,
			@Parameter(
					description = "The admin can choose the type of statistics to retrieve, if not set the DOMAIN_MONTHLY_STAT type will be choosen by default.",
					required = false,
					schema = @Schema(implementation = StatisticType.class, defaultValue = "DOMAIN_MONTHLY_STAT")
				)
				@QueryParam("type") @DefaultValue("DOMAIN_MONTHLY_STAT") String statisticType,
			@Parameter(
					description = "begin statistic creation date. format: yyyy-MM-dd",
					schema = @Schema(implementation = Date.class)
				)
				@QueryParam("beginDate") String beginDate,
			@Parameter(
					description = "end statistic creation date. format: yyyy-MM-dd",
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
		PageContainer<StorageConsumptionStatisticDto> container = statisticFacade.findAll(
				domainUuid, includeNestedDomains,
				SortOrder.valueOf(sortOrder),
				StorageConsumptionStatisticField.valueOf(sortField),
				StatisticType.valueOf(statisticType),
				Optional.ofNullable(beginDate),
				Optional.ofNullable(endDate), pageNumber,
				pageSize);
		return pageResponseBuilder.build(container);
	}
}
