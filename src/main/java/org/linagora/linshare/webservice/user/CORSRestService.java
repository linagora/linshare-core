package org.linagora.linshare.webservice.user;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/rest/cors")
public interface CORSRestService {
	
	Response getOptions();
	
	Response isCorsAuthorized();
	
}
