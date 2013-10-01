package org.linagora.linshare.webservice.admin;

import javax.ws.rs.Path;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

@Path("/members")
public interface ThreadMemberRestService {

	public ThreadMemberDto get(Long id) throws BusinessException;

	public void update(Long id, ThreadMemberDto dto) throws BusinessException;

	public void delete(Long id, ThreadMemberDto dto) throws BusinessException;
}
