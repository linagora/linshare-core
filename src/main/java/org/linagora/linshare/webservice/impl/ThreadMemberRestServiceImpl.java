package org.linagora.linshare.webservice.impl;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceThreadFacade;
import org.linagora.linshare.webservice.ThreadMemberRestService;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

public class ThreadMemberRestServiceImpl extends WebserviceBase implements ThreadMemberRestService {
	
	private WebServiceThreadFacade webServiceThreadFacade;

	public ThreadMemberRestServiceImpl(final WebServiceThreadFacade webServiceThreadFacade) {
		this.webServiceThreadFacade = webServiceThreadFacade;
	}

	@Path("/{uuid}/members")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	// application/xml application/json
	@Override
	public List<ThreadMemberDto> getAllThreadMembers(@PathParam("uuid") String uuid) throws BusinessException {
		List<ThreadMemberDto> members = null;
		try {
			webServiceThreadFacade.checkAuthentication();
			members = webServiceThreadFacade.getAllThreadMembers(uuid);
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
		return members;
	}

}
