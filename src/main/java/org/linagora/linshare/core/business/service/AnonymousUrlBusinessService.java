package org.linagora.linshare.core.business.service;

import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.exception.BusinessException;

public interface AnonymousUrlBusinessService {
	
	public AnonymousUrl findByUuid(String uuid);
	
	public AnonymousUrl create(Boolean passwordProtected) throws BusinessException;
	
	public void update (AnonymousUrl anonymousUrl) throws BusinessException;
	
}
