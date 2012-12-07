package org.linagora.linshare.webservice.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.webservice.PluginManagmentRestService;

public class PluginManagmentRestServiceImpl extends WebserviceBase implements PluginManagmentRestService {

	
	@GET
    @Path("/information")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Override
	public String getInformation() {
		return "undefined";
	}
}
