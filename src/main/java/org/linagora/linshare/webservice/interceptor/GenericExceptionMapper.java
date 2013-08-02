package org.linagora.linshare.webservice.interceptor;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.commons.httpclient.HttpStatus;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.webservice.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces(MediaType.APPLICATION_JSON)
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

	private static final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

	@Override
	public Response toResponse(Exception exception) {
		logger.error("A NullPointerException was caught : " + exception.getLocalizedMessage() + ". ", exception);
		ErrorDto errorDto = new ErrorDto(BusinessErrorCode.WEBSERVICE_FAULT.getCode(), "Unexpected exception : " + exception.getClass().toString() + " : " +exception.getMessage());
		ResponseBuilder response = Response.status(HttpStatus.SC_BAD_REQUEST);
		response.entity(errorDto);
		return response.build();
	}
}
