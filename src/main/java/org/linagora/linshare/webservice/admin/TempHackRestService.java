package org.linagora.linshare.webservice.admin;

import org.linagora.linshare.core.exception.BusinessException;

public interface TempHackRestService {

	Long dataUsage(String domainId) throws BusinessException;
}
