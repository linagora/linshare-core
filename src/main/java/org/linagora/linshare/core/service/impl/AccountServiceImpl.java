package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountServiceImpl implements AccountService {
	
	@SuppressWarnings("unused")
	final private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
	
	private final AccountRepository<Account> accountRepository;
    
    @SuppressWarnings("rawtypes")
    private final UserRepository userRepository;

    
	@SuppressWarnings("rawtypes")
	public AccountServiceImpl(AccountRepository accountRepository,
			UserRepository userRepository) {
		super();
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
	}


	@Override
	public Account findByLsUid(String uid) {
		Account acc = accountRepository.findByLsUid(uid);
		// TODO : Remove this : Temporary hook for compatibility
		if (acc == null) {
			acc = userRepository.findByMail(uid);
		}
		if(acc == null) {
			logger.error("Can't find logged user  : " + uid);
		}
		return acc;
	}


	@Override
	public Account update(Account account) throws BusinessException {
		return (Account) accountRepository.update(account);
	}

}
