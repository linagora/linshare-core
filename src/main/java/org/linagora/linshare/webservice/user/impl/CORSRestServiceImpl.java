package org.linagora.linshare.webservice.user.impl;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.dto.SimpleStringValue;
import org.linagora.linshare.webservice.user.CORSRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CORSRestServiceImpl extends WebserviceBase implements CORSRestService {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CORSRestServiceImpl.class);

	@Path("/")
	@OPTIONS
	@Override
	public Response getOptions() {
		ResponseBuilder response = Response.ok();
		response.header("Content-Type", "text/plain; charset=UTF-8");
		response.header("Access-Control-Allow-Origin", "*");
		response.header("Access-Control-Allow-Methods", "*");
		response.header("Access-Control-Allow-Headers", "Authorization");
		return response.build();
	}

	@GET
	@Path("/")
	@Override
	public Response isCorsAuthorized() {
		ResponseBuilder response = Response.ok();
		response.header("Content-Type", "text/plain; charset=UTF-8");
		return response.build();
	}
}
