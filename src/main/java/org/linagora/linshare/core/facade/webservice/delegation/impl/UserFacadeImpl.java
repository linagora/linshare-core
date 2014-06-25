package org.linagora.linshare.core.facade.webservice.delegation.impl;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.UserFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.webservice.dto.PasswordDto;

public class UserFacadeImpl extends DelegationGenericFacadeImpl implements
		UserFacade {

	private final UserService userService;

	public UserFacadeImpl(AccountService accountService, UserService userService) {
		super(accountService);
		this.userService = userService;
	}

	@Override
	public void changePassword(PasswordDto password) throws BusinessException {
		User actor = checkAuthentication();
		userService.changePassword(actor.getLsUuid(), actor.getMail(),
				password.getOldPwd(), password.getNewPwd());
	}
}
