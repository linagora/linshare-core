package org.linagora.linshare.webservice;

import java.util.List;

import javax.ws.rs.Path;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

@Path("/rest/threadss")
public interface ThreadMemberRestService {

	List<ThreadMemberDto> getAllThreadMembers(String uuid) throws BusinessException;

}
