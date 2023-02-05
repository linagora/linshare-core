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

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

public class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {

	@Autowired
	protected ExceptionStatisticAdminFacade exceptionStatisticFacade;
	
	private static final Logger logger = LoggerFactory.getLogger(UnrecognizedPropertyExceptionMapper.class);

	@Override
	public Response toResponse(UnrecognizedPropertyException exception) {
		logger.error("An unrecognized field excpetion was caught : " + exception.getLocalizedMessage() + ". ",
				exception);
		ErrorDto errorDto = new ErrorDto(BusinessErrorCode.WEBSERVICE_BAD_REQUEST.getCode(),
				"You have an unrecognized field in your json check the : " + exception.getPropertyName().toString()
				+ ", " + exception.getClass().toString());
		ResponseBuilder response = Response.status(Status.BAD_REQUEST);
		exceptionStatisticFacade.createExceptionStatistic(null, null, ExceptionType.UNRECOGNIZED_PROPERTY_EXCEPTION);
		response.entity(errorDto);
		return response.build();
	}
}
