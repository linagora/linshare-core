package org.linagora.linshare.webservice.admin.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadMemberFacade;
import org.linagora.linshare.webservice.admin.ThreadMemberRestService;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

public class ThreadMemberRestServiceImpl implements ThreadMemberRestService {

	private ThreadMemberFacade threadMemberFacade;

	public ThreadMemberRestServiceImpl(
			final ThreadMemberFacade threadMemberFacade) {
		super();
		this.threadMemberFacade = threadMemberFacade;
	}

	@Path("/{id}")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public ThreadMemberDto get(@PathParam("id") Long id)
			throws BusinessException {
		threadMemberFacade.checkAuthentication();
		return threadMemberFacade.get(id);
	}

	@Path("/{id}")
	@PUT
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public void update(@PathParam("id") Long id, ThreadMemberDto dto)
			throws BusinessException {
		threadMemberFacade.checkAuthentication();
		threadMemberFacade.update(dto);
	}

	@Path("/{id}")
	@DELETE
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public void delete(@PathParam("id") Long id, ThreadMemberDto dto)
			throws BusinessException {
		threadMemberFacade.checkAuthentication();
		threadMemberFacade.delete(dto);
	}
}