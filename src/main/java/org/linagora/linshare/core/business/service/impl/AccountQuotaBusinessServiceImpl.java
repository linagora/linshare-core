/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.business.service.impl;

import java.util.Date;

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.core.repository.DomainQuotaRepository;
import org.linagora.linshare.core.repository.OperationHistoryRepository;

public class AccountQuotaBusinessServiceImpl implements AccountQuotaBusinessService {

	private final AccountQuotaRepository repository;
	private final OperationHistoryRepository operationHistoryRepository;
	private final DomainQuotaRepository domainQuotaRepository;

	public AccountQuotaBusinessServiceImpl(final AccountQuotaRepository repository,
			final OperationHistoryRepository operationHistoryRepository,
			final DomainQuotaRepository domainQuotaRepository) {
		this.repository = repository;
		this.operationHistoryRepository = operationHistoryRepository;
		this.domainQuotaRepository = domainQuotaRepository;
	}

	@Override
	public AccountQuota find(Account account) throws BusinessException {
		return repository.find(account);
	}

	@Override
	public boolean exist(Account account) throws BusinessException{
		return find(account) != null;
	}

	@Override
	public AccountQuota createOrUpdate(Account account, Date today) throws BusinessException {
		Long sumOperationValue = operationHistoryRepository.sumOperationValue(account, null, today, null, null);
		AccountQuota entity;
		if (!exist(account)) {
			DomainQuota domainQuota = domainQuotaRepository.find(account.getDomain());
			if (domainQuota != null) {
				Long quota = domainQuota.getQuota();
				Long quotaWarning = domainQuota.getQuotaWarning();
				Long tailFileMax = domainQuota.getFileSizeMax();
				entity = new AccountQuota(account, account.getDomain(), account.getDomain().getParentDomain(), quota,
						quotaWarning, tailFileMax, sumOperationValue, (long) 0);
				entity = repository.create(entity);
			}
			else{
				throw new BusinessException(account.getDomain().getIdentifier() + " domain does not have a quota yet");
			}
		} else {
			entity = find(account);
			entity = repository.update(entity, sumOperationValue);
		}
		return entity;
	}

	@Override
	public AccountQuota create(AccountQuota entity) throws BusinessException {
		Account account = entity.getAccount();
		if (exist(account)) {
			throw new BusinessException("It must be only one AccountQuota for any entity");
		} else {
			return repository.create(entity);
		}
	}

	@Override
	public AccountQuota update(AccountQuota entity, Long sumOperationValue) throws BusinessException {
		return repository.update(entity, sumOperationValue);
	}

	@Override
	public AccountQuota update(AccountQuota entity) throws BusinessException {
		return repository.update(entity);
	}
}
