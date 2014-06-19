/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.util.Set;

import org.linagora.linshare.core.business.service.TechnicalAccountBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.TechnicalAccountService;

public class TechnicalAccountServiceImpl implements TechnicalAccountService {

	private final TechnicalAccountBusinessService technicalAccountBusinessService;

	private final AccountService accountService;

	public TechnicalAccountServiceImpl(
			final TechnicalAccountBusinessService technicalAccountBusinessService,
			final AccountService accountService) {
		super();
		this.technicalAccountBusinessService = technicalAccountBusinessService;
		this.accountService = accountService;
	}

	@Override
	public TechnicalAccount create(Account actor, String domainId,
			TechnicalAccount account) throws BusinessException {
		return technicalAccountBusinessService.create(domainId, account);
	}

	@Override
	public void delete(Account actor, TechnicalAccount account)
			throws BusinessException {
		technicalAccountBusinessService.delete(account);
	}

	@Override
	public TechnicalAccount find(Account actor, String uuid)
			throws BusinessException {
		return technicalAccountBusinessService.find(uuid);
	}

	@Override
	public Set<TechnicalAccount> findAll(Account actor, String domainIdentifier)
			throws BusinessException {
		return technicalAccountBusinessService.findAll(domainIdentifier);
	}

	@Override
	public TechnicalAccount update(Account actor, TechnicalAccount accountDto)
			throws BusinessException {
		TechnicalAccount technicalAccount = technicalAccountBusinessService.find(accountDto.getLsUuid());
		technicalAccount.setLastName(accountDto.getLastName());
		technicalAccount.setMail(accountDto.getMail());
		technicalAccount.setFirstName(accountDto.getFirstName());
		technicalAccount.setPassword(accountDto.getPassword());
		return technicalAccountBusinessService.update(technicalAccount);
	}
}
