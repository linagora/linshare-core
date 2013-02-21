package org.linagora.linshare.webservice.impl;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceThreadFacade;
import org.linagora.linshare.webservice.ThreadRestService;
import org.linagora.linshare.webservice.dto.ThreadDto;

public class ThreadRestServiceImpl extends WebserviceBase implements ThreadRestService {

	private final WebServiceThreadFacade webServiceThreadFacade;

	public ThreadRestServiceImpl(final WebServiceThreadFacade webServiceThreadFacade) {
		this.webServiceThreadFacade = webServiceThreadFacade;
	}

	/**
	 * get the files of the user
	 */
	@Path("/list")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	// application/xml application/json
	@Override
	public List<ThreadDto> getAllMyThread() throws BusinessException {
		List<ThreadDto> threads = null;
		try {
			webServiceThreadFacade.checkAuthentication();
			threads = webServiceThreadFacade.getAllMyThread();
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
		return threads; 
	}


}
