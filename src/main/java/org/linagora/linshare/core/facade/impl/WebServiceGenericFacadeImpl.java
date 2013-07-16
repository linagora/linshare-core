package org.linagora.linshare.core.facade.impl;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceGenericFacade;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class WebServiceGenericFacadeImpl implements WebServiceGenericFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(WebServiceGenericFacadeImpl.class);

	protected final AccountService accountService;

	public WebServiceGenericFacadeImpl(AccountService accountService) {
		super();
		this.accountService = accountService;
	}

	protected User getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		// get logged in username
		String name = (auth != null) ? auth.getName() : null;

		logger.debug("Authentication with principal : " + name);
		if (name == null)
			return null;
		User user = (User) accountService.findByLsUuid(name);
		logger.debug("Authenticated user : " + user.getAccountReprentation());
		return user;
	}

	@Override
	public User checkAuthentication() throws BusinessException {
		User actor = getAuthentication();

		if (actor == null)
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_UNAUTHORIZED,
					"You are not authorized to use this service");
		return actor;
	}
}
