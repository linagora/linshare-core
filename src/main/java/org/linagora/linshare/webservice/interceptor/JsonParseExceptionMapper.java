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
package org.linagora.linshare.webservice.interceptor;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import javax.ws.rs.core.Response.Status;
import org.linagora.linshare.core.domain.constants.ExceptionType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.facade.webservice.admin.ExceptionStatisticAdminFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;

@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
	
	@Autowired
	protected ExceptionStatisticAdminFacade exceptionStatisticFacade;

	private static final Logger logger = LoggerFactory.getLogger(JsonParseExceptionMapper.class);

	@Override
	public Response toResponse(JsonParseException exception) {
		logger.error("A non validate json format exception was caught : " + exception.getLocalizedMessage() + ". ",
				exception);
		ErrorDto errorDto = new ErrorDto(BusinessErrorCode.WEBSERVICE_BAD_REQUEST.getCode(),
				"You have an error in your json : " + exception.getClass().toString() + " : " + exception.getMessage());
		ResponseBuilder response = Response.status(Status.BAD_REQUEST);
		exceptionStatisticFacade.createExceptionStatistic(null, exception.getStackTrace(), ExceptionType.JSON_PARSE_EXCEPTION);
		response.entity(errorDto);
		return response.build();
	}
}
