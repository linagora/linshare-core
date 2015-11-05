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
import java.util.List;

import org.linagora.linshare.core.business.service.QuotaBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.linagora.linshare.core.repository.QuotaRepository;

public class QuotaBusinessServiceImpl
		implements QuotaBusinessService {

	private final QuotaRepository repository;
	private final OperationHistoryRepository operationHistoryRepository;

	public QuotaBusinessServiceImpl(
			final QuotaRepository quotaRepository, final OperationHistoryRepository operationHistoryRepository) {
		this.repository = quotaRepository;
		this.operationHistoryRepository = operationHistoryRepository;
	}

	@Override
	public Quota create(Quota entity) throws BusinessException {
		return repository.create(entity);
	}

	@Override
	public Quota update(Quota entity,
			long currentValue) throws BusinessException {
		entity.setLastValue(entity.getCurrentValue());
		entity.setCurrentValue(entity.getCurrentValue() + currentValue);
		return repository.update(entity);
	}

	@Override
	public Quota findByAccount(Account account)
			throws BusinessException {
		return repository.findByAccount(account);
	}

	@Override
	public List<Quota> findByDomain(AbstractDomain domain)
			throws BusinessException {
		return repository.findByDomain(domain);
	}

	@Override
	public List<Quota> findByParentDomain(
			AbstractDomain parentDomain) throws BusinessException {
		return repository.findByParentDomain(parentDomain);
	}

	@Override
	public boolean exist(Account account) {
		return findByAccount(account) != null;
	}

	@Override
	public Quota createOrUpdate(Account account, Date today) {
		long sumOperationValue = operationHistoryRepository.sumOperationValue(account, null, today, null);
		Quota entity;
		if (!exist(account)) {
			entity = new Quota(account, account.getDomain(), account.getDomain().getParentDomain(),
					sumOperationValue, 0);
			entity = create(entity);
		} else {
			entity = findByAccount(account);
			entity = update(entity, sumOperationValue);
		}
		return entity;
	}
}
