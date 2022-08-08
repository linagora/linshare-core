/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.fields.GenericStatisticField;
import org.linagora.linshare.core.domain.entities.fields.GenericStatisticGroupByField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.facade.webservice.adminv5.GenericStatisticFacade;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.GenericStatisticRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/domains/{domainUuid}/statistics/generics")
@Produces({ MediaType.APPLICATION_JSON })
public class GenericStatisticRestServiceImpl extends WebserviceBase
		implements GenericStatisticRestService {

	private final GenericStatisticFacade statisticFacade;

	private static PagingResponseBuilder<BasicStatistic> pageResponseBuilder = new PagingResponseBuilder<>();

	public GenericStatisticRestServiceImpl(GenericStatisticFacade statisticFacade) {
		this.statisticFacade = statisticFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Get storage consumption statistics. By default will be a 12 rolling months.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = BasicStatistic.class))),
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
				description = "The admin can choose the field to sort with the stat's list to retrieve, if not set the value field will be choosen by default.",
				required = false,
				schema = @Schema(implementation = GenericStatisticField.class, defaultValue = "value")
			)
			@QueryParam("sortField") @DefaultValue("value") String sortField,
		@Parameter(
				description = "The admin can choose the type of statistics to retrieve, if not set the DAILY type will be choosen by default.",
				required = false,
				schema = @Schema(implementation = BasicStatisticType.class, defaultValue = "DAILY")
			)
			@QueryParam("type") @DefaultValue("DAILY") String statisticType,
		@Parameter(
				description = "The admin can choose the type of LogAction to retrieve.",
				required = false,
				schema = @Schema(implementation = LogAction.class)
			)
			@QueryParam("action") List<String> logActions,
		@Parameter(
				description = "The admin can choose the type of resources to retrieve.",
				required = false,
				schema = @Schema(implementation = AuditLogEntryType.class)
			)
			@QueryParam("resourceType") List<String> resourceTypes,
		@Parameter(
				description = "Sum 'value' field by mimeType.",
				schema = @Schema(implementation = Boolean.class)
			)
			@QueryParam("sum") @DefaultValue("false") boolean sum,
		@Parameter(
				description = "TODO.",
				required = false,
				schema = @Schema(implementation = GenericStatisticGroupByField.class)
			)
			@QueryParam("sumBy") List<String> sumBy,
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
		Set<GenericStatisticGroupByField> sumByEnum = sumBy.stream().map(name -> GenericStatisticGroupByField.valueOf(name)).collect(Collectors.toSet());
		PageContainer<BasicStatistic> container = statisticFacade.findAll(
			domainUuid,
			includeNestedDomains,
			SortOrder.valueOf(sortOrder),
			GenericStatisticField.valueOf(sortField),
			BasicStatisticType.valueOf(statisticType),
			logActions.stream().map(name -> LogAction.valueOf(name)).collect(Collectors.toSet()),
			resourceTypes.stream().map(name -> AuditLogEntryType.valueOf(name)).collect(Collectors.toSet()),
			sum, sumByEnum,
			Optional.ofNullable(beginDate),
			Optional.ofNullable(endDate), pageNumber,
			pageSize);
		return pageResponseBuilder.build(container);
	}
}
