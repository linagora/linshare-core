package org.linagora.linshare.webservice.admin.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.TempHackRestService;

@Path("/temp_hack")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class TempHackRestServiceImp extends WebserviceBase implements
		TempHackRestService {

	private final DomainFacade domainFacade;

	public TempHackRestServiceImp(DomainFacade domainFacade) {
		super();
		this.domainFacade = domainFacade;
	}

	@Path("/consumption")
	@GET
	@Override
	public Long dataUsage(@QueryParam("domain") String domainId)
			throws BusinessException {
		return domainFacade.dataUsage(domainId);
	}
}
