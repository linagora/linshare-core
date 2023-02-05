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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AdvancedStatisticsFacade;
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;
import org.linagora.linshare.webservice.admin.AdvancedStatisticRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/advanced_statistic")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class AdvancedStatisticRestServiceImpl implements AdvancedStatisticRestService {

	private AdvancedStatisticsFacade advancedStatisticFacade;

	public AdvancedStatisticRestServiceImpl(AdvancedStatisticsFacade facade) {
		super();
		this.advancedStatisticFacade = facade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Get a Advanced statistic (MimeType) Between two dates.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimeTypeStatistic.class))),
			responseCode = "200"
		)
	})
	public Set<MimeTypeStatistic> findBetweenTwoDates(
			@Parameter(description = "domain's uuid")
				@QueryParam("domainUuid") String domainUuid,
			@Parameter(description = "statistic's  begin date")
				@QueryParam("beginDate") String beginDate,
			@Parameter(description = "statistic's end date")
				@QueryParam("endDate") String endDate, 
			@Parameter(description = "MimeType")
				@QueryParam("mimeType") String mimeType)
						throws BusinessException {
		return advancedStatisticFacade.findBetweenTwoDates(domainUuid, beginDate, endDate, mimeType);
	}

}
