package org.linagora.linshare.webservice.user;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.linagora.linshare.webservice.dto.SimpleStringValue;

@Path("/rest/cors")
public interface CORSRestService {
	
	Response getOptions();
	
	Response isCorsAuthorized();
	
}
