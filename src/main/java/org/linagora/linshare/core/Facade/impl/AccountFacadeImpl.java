package org.linagora.linshare.core.Facade.impl;

import org.linagora.linshare.core.Facade.AccountFacade;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;

public class AccountFacadeImpl implements AccountFacade {
	
	private final AccountService accountService;

	public AccountFacadeImpl(AccountService accountService) {
		super();
		this.accountService = accountService;
	}

	@Override
	public UserVo loadUserDetails(String uid) throws BusinessException {
//		Account account = userService.searchAndCreateUserEntityFromUnkownDirectory(mail);
		Account account = accountService.findByLsUid(uid);
    	if (account != null) {
    		return new UserVo(account);
    	}
    	return null;
	}
	

}
