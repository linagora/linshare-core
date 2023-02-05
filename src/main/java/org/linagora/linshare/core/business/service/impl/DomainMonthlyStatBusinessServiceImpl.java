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

import org.linagora.linshare.core.business.service.DomainMonthlyStatBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainMonthlyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainMonthlyStatRepository;
import org.linagora.linshare.core.repository.DomainWeeklyStatRepository;

public class DomainMonthlyStatBusinessServiceImpl implements DomainMonthlyStatBusinessService {

	private final DomainMonthlyStatRepository repository;
	private final DomainWeeklyStatRepository domainWeeklyStatRepository;

	public DomainMonthlyStatBusinessServiceImpl(final DomainMonthlyStatRepository repository,
			final DomainWeeklyStatRepository domainWeeklyStatRepository) {
		this.repository = repository;
		this.domainWeeklyStatRepository = domainWeeklyStatRepository;
	}

	@Override
	public DomainMonthlyStat create(AbstractDomain domain, Date beginDate, Date endDate) throws BusinessException {
		Long actualOperationSum = domainWeeklyStatRepository.sumOfActualOperationSum(domain, null, beginDate, endDate);
		Long operationCount = domainWeeklyStatRepository.sumOfOperationCount(domain, null, beginDate, endDate);
		Long createOperationSum = domainWeeklyStatRepository.sumOfCreateOperationSum(domain, null, beginDate, endDate);
		Long createOperationCount = domainWeeklyStatRepository.sumOfCreateOperationCount(domain, null, beginDate,
				endDate);
		Long deleteOperationSum = domainWeeklyStatRepository.sumOfDeleteOperationSum(domain, null, beginDate, endDate);
		Long deleteOperationCount = domainWeeklyStatRepository.sumOfDeleteOperationCount(domain, null, beginDate,
				endDate);
		Long diffOperationSum = domainWeeklyStatRepository.sumOfDiffOperationSum(domain, null, beginDate, endDate);
		DomainMonthlyStat entity = new DomainMonthlyStat(domain, domain.getParentDomain(), operationCount, deleteOperationCount,
				createOperationCount, createOperationSum, deleteOperationSum, diffOperationSum, actualOperationSum);
		entity.setStatisticDate(endDate);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<DomainMonthlyStat> findBetweenTwoDates(AbstractDomain domain, Date beginDate, Date endDate) {
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

}
