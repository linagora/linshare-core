package org.linagora.linshare.core.facade.webservice.user.impl;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserGenericFacadeImp extends GenericFacadeImpl {

	private static final Logger logger = LoggerFactory
			.getLogger(UserGenericFacadeImp.class);

	public UserGenericFacadeImp(AccountService accountService) {
		super(accountService);
	}

	@Override
	public User checkAuthentication() throws BusinessException {
		User actor = super.checkAuthentication();

		if (!(actor.hasSimpleRole()
				|| actor.hasAdminRole()
				|| actor.hasSuperAdminRole()
				)) {
			logger.error("Current actor is trying to access to a forbbiden api : " + actor.getAccountReprentation());
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return actor;
	}
}
