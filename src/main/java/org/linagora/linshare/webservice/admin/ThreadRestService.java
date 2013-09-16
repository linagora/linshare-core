package org.linagora.linshare.webservice.admin;

import java.util.List;

import javax.ws.rs.Path;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ThreadDto;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

@Path("/rest/admin/threads")
public interface ThreadRestService {

	public List<ThreadDto> getAll() throws BusinessException;

	public ThreadDto get(String uuid) throws BusinessException;

	public List<ThreadMemberDto> getMembers(String uuid)
			throws BusinessException;

	public void addMember(String uuid, ThreadMemberDto member)
			throws BusinessException;

	public void delete(ThreadDto thread) throws BusinessException;
}
