package org.linagora.linshare.webservice.impl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.linagora.linshare.core.exception.BusinessException;

/**
 * 
 * common utility methods for webservice implementation (rest, soap)
 */
public class WebserviceBase {

	
	//REST
	
	protected WebApplicationException giveRestException(int httpErrorCode, String message) {
		return giveRestException(httpErrorCode,message,null);
	}
	protected WebApplicationException giveRestException(int httpErrorCode, String message,Throwable cause) {
		if(cause==null)
			return new WebApplicationException(Response.status(httpErrorCode).entity(message).build());
		else
		return new WebApplicationException(cause, Response.status(httpErrorCode).entity(message).build());
	}
	
	protected WebApplicationException analyseFaultREST(BusinessException e) {
		
		
		//TODO locale in WebApplicationException ?
		
		WebApplicationException w;
		
		//BusinessException have a look to BusinessErrorCode
		switch (e.getErrorCode()){
		case WEBSERVICE_FAULT: w=giveRestException(HttpStatus.SC_INTERNAL_SERVER_ERROR,e.getMessage(),e);
		case WEBSERVICE_UNAUTHORIZED: w=giveRestException(HttpStatus.SC_FORBIDDEN,e.getMessage());
		case WEBSERVICE_NOT_FOUND: w=giveRestException(HttpStatus.SC_NOT_FOUND,e.getMessage());
		default:
			w=giveRestException(HttpStatus.SC_INTERNAL_SERVER_ERROR,e.getMessage(),e);
		}
		
		return w;
	}
	
	
	//SOAP
	
	public static final String NAME_SPACE_NS = "http://org/linagora/linshare/webservice/";
	
}
