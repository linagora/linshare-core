package org.linagora.linshare.core.business.service;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface DomainPermissionBusinessService {

	boolean isAdminforThisDomain(Account actor, String domainId)
			throws BusinessException;

	boolean isAdminforThisDomain(Account actor, AbstractDomain domain);

	boolean isAdminForThisUser(Account actor, User user);

}
