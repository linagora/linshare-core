/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.StatisticFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.StatisticDto;
import org.linagora.linshare.webservice.WebserviceBase;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/statistic")
@Api(value = "/rest/admin/statistic", description = "Statistic service.", produces = "application/json, application/xml", consumes = "application/json,aaplication/xml")
public class StatisticRestServiceImpl extends WebserviceBase
		implements org.linagora.linshare.webservice.admin.StatisticRestService {

	private static final String DOMAIN = "domain";
	private static final String ACCOUNT = "account";
	private static final String STATISTIC_TYPE = "statisticType";
	private static final String BEGIN_DATE = "beginDate";
	private static final String END_DATE = "endDate";
	private final StatisticFacade statisticFacade;

	public StatisticRestServiceImpl(StatisticFacade statisticFacade) {
		this.statisticFacade = statisticFacade;
	}

	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get Statistic Between two dates.", response = StatisticDto.class)
	@ApiResponses({ @ApiResponse(code = 409, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Owner not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public List<StatisticDto> findBetweenTwoDates(@QueryParam(ACCOUNT) String account,
			@QueryParam(DOMAIN) String domain, @QueryParam(BEGIN_DATE) String beginDate,
			@QueryParam(END_DATE) String endDate, @QueryParam(STATISTIC_TYPE) StatisticType statisticType)
					throws BusinessException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date bDate = null;
		Date eDate = null;
		try {
			if(beginDate != null){
				bDate = formatter.parse(beginDate);
			}
			if(endDate != null){
				eDate = formatter.parse(endDate);
			}
			return statisticFacade.findBetweenTwoDates(account, domain, bDate, eDate, statisticType);
		} catch (ParseException e) {
			throw new BusinessException(BusinessErrorCode.BAD_REQUEST, "Bad request.");
		}
	}
}
