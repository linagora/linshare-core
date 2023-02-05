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

import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainWeeklyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainDailyStatisticRepository;
import org.linagora.linshare.core.repository.DomainWeeklyStatRepository;

public class DomainWeeklyStatBusinessServiceImpl implements DomainWeeklyStatBusinessService {

	private final DomainWeeklyStatRepository repository;
	private final DomainDailyStatisticRepository domainDailyStatRepository;

	public DomainWeeklyStatBusinessServiceImpl(final DomainWeeklyStatRepository repository,
			final DomainDailyStatisticRepository domainDailyStatRepository) {
		this.repository = repository;
		this.domainDailyStatRepository = domainDailyStatRepository;
	}

	@Override
	public DomainWeeklyStat create(AbstractDomain domain, Date beginDate, Date endDate) throws BusinessException {
		Long actualOperationSum = domainDailyStatRepository.sumOfActualOperationSum(domain, null, beginDate, endDate);
		Long operationCount = domainDailyStatRepository.sumOfOperationCount(domain, null, beginDate, endDate);
		Long createOperationSum = domainDailyStatRepository.sumOfCreateOperationSum(domain, null, beginDate, endDate);
		Long createOperationCount = domainDailyStatRepository.sumOfCreateOperationCount(domain, null, beginDate,
				endDate);
		Long deleteOperationSum = domainDailyStatRepository.sumOfDeleteOperationSum(domain, null, beginDate, endDate);
		Long deleteOperationCount = domainDailyStatRepository.sumOfDeleteOperationCount(domain, null, beginDate,
				endDate);
		Long diffOperationSum = domainDailyStatRepository.sumOfDiffOperationSum(domain, null, beginDate, endDate);
		DomainWeeklyStat entity = new DomainWeeklyStat(domain, domain.getParentDomain(), operationCount,
				deleteOperationCount, createOperationCount, createOperationSum, deleteOperationSum, diffOperationSum,
				actualOperationSum);
		entity.setStatisticDate(endDate);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<DomainWeeklyStat> findBetweenTwoDates(AbstractDomain domain, Date beginDate, Date endDate) {
		return repository.findBetweenTwoDates(null, domain, null, beginDate, endDate, null);
	}

	@Override
	public void deleteBeforeDate(Date date) {
		repository.deleteBeforeDate(date);

	}

	@Override
	public List<AbstractDomain> findDomainBetweenTwoDates(Date beginDate, Date endDate) {
		return repository.findDomainBetweenTwoDates(beginDate, endDate);
	}

	@Override
	public List<String> findIdentifierDomainBetweenTwoDates(Date beginDate, Date endDate) {
		return repository.findIdentifierDomainBetweenTwoDates(beginDate, endDate);
	}
}
