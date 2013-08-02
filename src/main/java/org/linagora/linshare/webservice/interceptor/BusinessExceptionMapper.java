package org.linagora.linshare.webservice.interceptor;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.commons.httpclient.HttpStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces(MediaType.APPLICATION_JSON)
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

	private static final Logger logger = LoggerFactory.getLogger(BusinessExceptionMapper.class);

	@Override
	public Response toResponse(BusinessException exception) {
		logger.error("A BusinessException was caught : code=" + exception.getErrorCode().toString() + ",  "+ exception.getLocalizedMessage());
		logger.debug("Stacktrace: ", exception);
		ErrorDto errorDto = new ErrorDto(exception.getErrorCode().getCode(), exception.getMessage());
		ResponseBuilder response = Response.status(HttpStatus.SC_BAD_REQUEST);
		response.entity(errorDto);
		return response.build();
	}
}
