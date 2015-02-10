package org.linagora.linshare.webservice.userv2;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.LogCriteriaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.LogDto;

public interface LogRestService {
	List<LogDto> query(LogCriteriaDto criteria) throws BusinessException;
}
