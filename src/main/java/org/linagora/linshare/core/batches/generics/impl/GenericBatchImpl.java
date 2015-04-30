package org.linagora.linshare.core.batches.generics.impl;

import org.linagora.linshare.core.batches.generics.GenericBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericBatchImpl<T> implements GenericBatch<T> {

	private static final Logger logger = LoggerFactory
			.getLogger(GenericBatchImpl.class);

	protected final AccountRepository<Account> accountRepository;

	public GenericBatchImpl(AccountRepository<Account> accountRepository) {
		super();
		this.accountRepository = accountRepository;
	}

	protected String getStringPosition(long total, long position) {
		return position + "/" + total + ":";
	}

	protected void logDebug(long total, long position, String message) {
		logger.debug(getStringPosition(total, position) + message);
	}

	protected void logInfo(long total, long position, String message) {
		logger.info(getStringPosition(total, position) + message);
	}

	protected void logError(long total, long position, String message) {
		logger.error(getStringPosition(total, position) + message);
	}

	protected SystemAccount getSystemAccount() {
		return accountRepository.getBatchSystemAccount();
	}
}
