package org.linagora.linshare.webservice.admin;

import java.util.List;

import javax.ws.rs.Path;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.LogCriteriaDto;
import org.linagora.linshare.webservice.dto.LogDto;

@Path("/logs")
public interface LogRestService {
	List<LogDto> query(LogCriteriaDto criteria) throws BusinessException;
}
