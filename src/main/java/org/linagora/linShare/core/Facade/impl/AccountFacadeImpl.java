package org.linagora.linShare.core.Facade.impl;

import org.linagora.linShare.core.Facade.AccountFacade;
import org.linagora.linShare.core.domain.entities.Account;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.AccountService;

public class AccountFacadeImpl implements AccountFacade {
	
	private final AccountService accountService;

	public AccountFacadeImpl(AccountService accountService) {
		super();
		this.accountService = accountService;
	}

	@Override
	public UserVo loadUserDetails(String uid) throws BusinessException {
//		Account account = userService.searchAndCreateUserEntityFromUnkownDirectory(mail);
		Account account = accountService.findUserInDB(uid);
    	if (account != null) {
    		return new UserVo(account);
    	}
    	return null;
	}
	

}
