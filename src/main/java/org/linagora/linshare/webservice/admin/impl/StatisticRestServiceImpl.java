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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.StatisticFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.StatisticDto;
import org.linagora.linshare.webservice.WebserviceBase;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/statistic")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class StatisticRestServiceImpl extends WebserviceBase
		implements org.linagora.linshare.webservice.admin.StatisticRestService {

	private final StatisticFacade statisticFacade;

	public StatisticRestServiceImpl(StatisticFacade statisticFacade) {
		this.statisticFacade = statisticFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Get Statistic Between two dates.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = StatisticDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<StatisticDto> findBetweenTwoDates(
			@Parameter(description = "account's uuid") 
				@QueryParam("accountUuid") String accountUuid,
			@Parameter(description = "domain's uuid")
				@QueryParam("domainUuid") String domainUuid,
			@Parameter(description = "begin statistic creation date")
				@QueryParam("beginDate") String beginDate,
			@Parameter(description = "end statistic creation date") 
				@QueryParam("endDate") String endDate,
			@Parameter(description = "statistic type")   
				@QueryParam("statisticType") StatisticType statisticType)
			throws BusinessException {
		return statisticFacade.findBetweenTwoDates(accountUuid, domainUuid,
				beginDate, endDate, statisticType);
	}
}

