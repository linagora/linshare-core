package org.linagora.linshare.webservice;

import java.util.List;

import javax.ws.rs.Path;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ThreadDto;

@Path("/rest/threads")
public interface ThreadRestService {

	public List<ThreadDto> getAllMyThread() throws BusinessException;
}
