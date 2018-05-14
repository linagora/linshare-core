/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.BasicStatisticAdminFacade;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.webservice.admin.BasicStatisticRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/basicStatistic")
@Api(value = "/rest/admin/basicStatistic", description = "basicStatistic service.", produces = "application/json,application/xml", consumes = "application/json,application/xml")
public class BasicStatisticRestServiceImpl implements BasicStatisticRestService {

	private BasicStatisticAdminFacade basicStatisticFacade;

	public BasicStatisticRestServiceImpl(BasicStatisticAdminFacade facade) {
		super();
		this.basicStatisticFacade = facade;
	}

	@Path("/{domainUuid}")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get a Basic statistic Between two dates.", response = BasicStatistic.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User has not Admin role"),
			@ApiResponse(code = 404, message = "Statistic not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	public Set<BasicStatistic> findBetweenTwoDates(
		    @ApiParam(value = "domain's uuid")
		       @PathParam("domainUuid") String domainUuid,
		    @ApiParam(value = "list of actions")
		   @QueryParam("logActions") List<LogAction> logActions,
		    @ApiParam(value = "statistic's  begin date")
			   @QueryParam("beginDate") String beginDate,
		    @ApiParam(value = "statistic's end date")
			   @QueryParam("endDate") String endDate, 
		    @ApiParam(value = "resource's types")
			   @QueryParam("resourceTypes") List<AuditLogEntryType> resourceTypes,
		    @ApiParam(value = "basic statistic's type")
			@QueryParam("type") BasicStatisticType type)
			throws BusinessException {
		return basicStatisticFacade.findBetweenTwoDates(domainUuid, logActions, beginDate, endDate, resourceTypes, type);
	}
}