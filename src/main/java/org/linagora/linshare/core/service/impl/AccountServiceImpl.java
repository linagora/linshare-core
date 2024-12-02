/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.UserResourceAccessControl;
import org.linagora.linshare.core.repository.AccountContactListsRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class AccountServiceImpl extends GenericServiceImpl<Account,User> implements AccountService {
	
	final private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
	
	private final AccountRepository<Account> accountRepository;
	private final AccountContactListsRepository accountContactListRepository;
	private final GuestBusinessService guestBusinessService;
	private final MailingListBusinessService mailingListBusinessService;
    
	public AccountServiceImpl(AccountRepository<Account> accountRepository,
			 SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			 UserResourceAccessControl rac,
			AccountContactListsRepository accountContactListRepository,
			GuestBusinessService guestBusinessService,
			MailingListBusinessService mailingListBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.accountRepository = accountRepository;
		this.accountContactListRepository = accountContactListRepository;
		this.guestBusinessService = guestBusinessService;
		this.mailingListBusinessService = mailingListBusinessService;
	}

	@Override
	public Account findByLsUuid(String uuid) {
		Account acc = accountRepository.findByLsUuid(uuid);
		if(acc == null) {
			logger.error("Can't find logged user  : " + uuid);
		}
		return acc;
	}

	/**
	 * The goal of this method is to raise an exception when the account is not
	 * found, and to not change the old behavior with the old method findByLsUuid
	 */
	@Override
	public Account findAccountByLsUuid(String uuid) {
		Account account = accountRepository.findByLsUuid(uuid);
		if (account == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Account not found, please check the entered uuid");
		}
		return account;
	}

	@Override
	public Account update(Account account) throws BusinessException {
		return accountRepository.update(account);
	}

	@Override
	public List<String> findAllKnownEmails(Account actor, String pattern) {
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "forbidden");
		}
		return accountRepository.findAllKnownEmails(pattern);
	}

	@Override
	public @Nonnull List<AccountContactLists> findAccountContactListsByAccount(@Nonnull final Account user)
			throws BusinessException {
		return accountContactListRepository.findByAccount(user);
	}

	@Override
	public @Nonnull List<AccountContactLists> findAccountContactListsByAccount(@Nonnull final String accountUuid) {
		Validate.notEmpty(accountUuid, "Account uuid is required");
		Account account = accountRepository.findByLsUuid(accountUuid);
		return mailingListBusinessService.findAccountContactListByAccount(account);
	}
}
