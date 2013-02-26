package org.linagora.linshare.webservice.impl;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceUserFacade;
import org.linagora.linshare.webservice.UserRestService;
import org.linagora.linshare.webservice.dto.UserDto;

public class UserRestServiceImpl extends WebserviceBase implements UserRestService {

	private final WebServiceUserFacade webServiceUserFacade;

	public UserRestServiceImpl(final WebServiceUserFacade webServiceUserFacade) {
		this.webServiceUserFacade = webServiceUserFacade;
	}

	@Path("/list")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	// application/xml application/json
	@Override
	public List<UserDto> getUsers() throws BusinessException {
		List<UserDto> users = null;
		try {
			webServiceUserFacade.checkAuthentication();
			users = webServiceUserFacade.getUsers();
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
		return users;
	}

}
