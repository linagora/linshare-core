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
