package org.linagora.linShare.core.service.impl;

import org.linagora.linShare.core.domain.entities.Account;
import org.linagora.linShare.core.repository.AccountRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountServiceImpl implements AccountService {
	
	@SuppressWarnings("unused")
	final private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
	
    @SuppressWarnings("rawtypes")
	private final AccountRepository accountRepository;
    
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
	public Account findUserInDB(String uid) {
		Account acc = accountRepository.findByLsUid(uid);
		// TODO : Remove this : Temporary hook for compatibility
		if (acc == null) {
			acc = userRepository.findByMail(uid);
		}
		return acc;
	}

}
