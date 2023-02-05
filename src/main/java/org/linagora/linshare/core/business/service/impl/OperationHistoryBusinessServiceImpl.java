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
package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.OperationHistoryRepository;

public class OperationHistoryBusinessServiceImpl implements OperationHistoryBusinessService {

	private final OperationHistoryRepository repository;

	public OperationHistoryBusinessServiceImpl(final OperationHistoryRepository operationHistoryRepository) {
		this.repository = operationHistoryRepository;
	}

	@Override
	public OperationHistory create(OperationHistory entity) throws BusinessException {
		return repository.create(entity);
	}

	@Override
	public List<AbstractDomain> findDomainBeforeDate(Date creationDate) {
		return repository.findDomainBeforeDate(creationDate);
	}

//	Method only use by the tests, any other utility?
	@Override
	public List<OperationHistory> find(Account account, AbstractDomain domain, ContainerQuotaType containerQuotaType, Date date)
			throws BusinessException {
		return repository.find(account, domain, containerQuotaType, date, null);
	}

	@Override
	public Long sumOperationValue(Account account, AbstractDomain domain, Date date,
			OperationHistoryTypeEnum operationType, ContainerQuotaType containerQuotaType) throws BusinessException {
		return repository.sumOperationValue(account, domain, date, operationType, containerQuotaType);
	}

	@Override
	public List<String> findUuidAccountBeforeDate(Date creationDate, ContainerQuotaType containerQuotaType) {
		return repository.findUuidAccountBeforeDate(creationDate, containerQuotaType);
	}

	@Override
	public void deleteBeforeDateByAccount(Date date, Account account) {
		repository.deleteBeforeDateByAccount(date, account);
	}
}
