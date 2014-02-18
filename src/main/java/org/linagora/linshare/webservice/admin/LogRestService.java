package org.linagora.linshare.webservice.admin;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.LogCriteriaDto;
import org.linagora.linshare.webservice.dto.LogDto;

public interface LogRestService {
	List<LogDto> query(LogCriteriaDto criteria) throws BusinessException;
}
