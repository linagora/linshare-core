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

@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

	private static final Logger logger = LoggerFactory.getLogger(IllegalArgumentExceptionMapper.class);

	@Autowired
	protected ExceptionStatisticAdminFacade exceptionStatisticFacade;

	@Override
	public Response toResponse(IllegalArgumentException exception) {
		logger.error("A IllegalArgumentException was caught : "
				+ exception.getLocalizedMessage());
		logger.debug("Stacktrace: ", exception);

		ErrorDto errorDto = new ErrorDto(BusinessErrorCode.WEBSERVICE_FAULT.getCode()," : " +exception.getMessage());
		ResponseBuilder response = Response.status(Status.BAD_REQUEST);
		exceptionStatisticFacade.createExceptionStatistic(null, exception.getStackTrace(), ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION);
		response.entity(errorDto);
		return response.build();
	}
}
