/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.webservice.admin.impl;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.BasicStatisticAdminFacade;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.webservice.admin.BasicStatisticRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/basic_statistic")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class BasicStatisticRestServiceImpl implements BasicStatisticRestService {

	private BasicStatisticAdminFacade basicStatisticFacade;

	public BasicStatisticRestServiceImpl(BasicStatisticAdminFacade facade) {
		super();
		this.basicStatisticFacade = facade;
	}

	@Path("/{domainUuid}")
	@HEAD
	@Operation(summary = "Get a statistic Between two dates.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = BasicStatistic.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response countValueStatisticBetweenTwoDates(
			@Parameter(description = "domain's uuid")
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "list of actions")
				@QueryParam("logActions") List<LogAction> actions,
			@Parameter(description = "statistic's  begin date")
				@QueryParam("beginDate") String beginDate,
			@Parameter(description = "statistic's end date")
				@QueryParam("endDate") String endDate,
			@Parameter(description = "resource's types")
				@QueryParam("resourceTypes") List<AuditLogEntryType> resourceTypes,
			@Parameter(description = "Statistics type")
				@QueryParam("statisticType") BasicStatisticType type)
			throws BusinessException {
		long count = basicStatisticFacade.countValueStatisticBetweenTwoDates(domainUuid, actions, beginDate, endDate, resourceTypes, type);
		return Response.noContent().header("count", count).build();
	}

	@Path("/{domainUuid}")
	@GET
	@Operation(summary = "Get a Basic statistic Between two dates.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = BasicStatistic.class))),
			responseCode = "200"
		)
	})
	public Set<BasicStatistic> findBetweenTwoDates(
			@Parameter(description = "domain's uuid")
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "list of actions")
				@QueryParam("logActions") List<LogAction> logActions,
			@Parameter(description = "statistic's  begin date")
				@QueryParam("beginDate") String beginDate,
			@Parameter(description = "statistic's end date")
				@QueryParam("endDate") String endDate,
			@Parameter(description = "resource's types")
				@QueryParam("resourceTypes") List<AuditLogEntryType> resourceTypes,
			@Parameter(description = "basic statistic's type")
				@QueryParam("type") BasicStatisticType type)
			throws BusinessException {
		return basicStatisticFacade.findBetweenTwoDates(domainUuid, logActions, beginDate, endDate, resourceTypes, type);
	}
}