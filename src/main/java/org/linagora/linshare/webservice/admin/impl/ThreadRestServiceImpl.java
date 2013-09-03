package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadFacade;
import org.linagora.linshare.webservice.admin.ThreadRestService;
import org.linagora.linshare.webservice.dto.ThreadDto;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

public class ThreadRestServiceImpl implements ThreadRestService {

	private ThreadFacade threadFacade;

	public ThreadRestServiceImpl(final ThreadFacade threadFacade) {
		super();
		this.threadFacade = threadFacade;
	}

	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public List<ThreadDto> getAll() throws BusinessException {
		threadFacade.checkAuthentication();
		return threadFacade.getAll();
	}

	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public ThreadDto get(@PathParam("uuid") String uuid)
			throws BusinessException {
		threadFacade.checkAuthentication();
		return threadFacade.get(uuid);
	}

	@Path("/{uuid}/members")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public List<ThreadMemberDto> getMembers(@PathParam("uuid") String uuid)
			throws BusinessException {
		threadFacade.checkAuthentication();
		return threadFacade.getMembers(uuid);
	}

	@Path("/{uuid}/members")
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public void addMember(@PathParam("uuid") String uuid, ThreadMemberDto member)
			throws BusinessException {
		threadFacade.checkAuthentication();
		threadFacade.addMember(uuid, member);
	}

	@Path("/{uuid}")
	@DELETE
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public void delete(@PathParam("uuid") String uuid) throws BusinessException {
		threadFacade.checkAuthentication();
		threadFacade.delete(uuid);
	}
}
