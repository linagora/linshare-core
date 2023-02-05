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

import org.linagora.linshare.core.business.service.ThreadMonthlyStatBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.ThreadMonthlyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ThreadMonthlyStatRepository;
import org.linagora.linshare.core.repository.ThreadWeeklyStatisticRepository;

public class ThreadMonthlyStatBusinessServiceImpl implements ThreadMonthlyStatBusinessService {

	private final ThreadMonthlyStatRepository repository;
	private final ThreadWeeklyStatisticRepository threadWeeklyStatRepository;

	public ThreadMonthlyStatBusinessServiceImpl(ThreadMonthlyStatRepository repository,
			ThreadWeeklyStatisticRepository threadWeeklyStatRepository) {
		this.repository = repository;
		this.threadWeeklyStatRepository = threadWeeklyStatRepository;
	}

	@Override
	public ThreadMonthlyStat create(WorkGroup workGroup, Date beginDate, Date endDate) throws BusinessException {
		Long actualOperationSum = threadWeeklyStatRepository.sumOfActualOperationSum(null, workGroup, beginDate, endDate);
		Long operationCount = threadWeeklyStatRepository.sumOfOperationCount(null, workGroup, beginDate, endDate);
		Long createOperationSum = threadWeeklyStatRepository.sumOfCreateOperationSum(null, workGroup, beginDate, endDate);
		Long createOperationCount = threadWeeklyStatRepository.sumOfCreateOperationCount(null, workGroup, beginDate,
				endDate);
		Long deleteOperationSum = threadWeeklyStatRepository.sumOfDeleteOperationSum(null, workGroup, beginDate, endDate);
		Long deleteOperationCount = threadWeeklyStatRepository.sumOfDeleteOperationCount(null, workGroup, beginDate,
				endDate);
		Long diffOperationSum = threadWeeklyStatRepository.sumOfDiffOperationSum(null, workGroup, beginDate, endDate);
		ThreadMonthlyStat entity = new ThreadMonthlyStat(workGroup, workGroup.getDomain(),
				workGroup.getDomain().getParentDomain(), operationCount, deleteOperationCount, createOperationCount,
				createOperationSum, deleteOperationSum, diffOperationSum, actualOperationSum);
		entity.setStatisticDate(endDate);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<ThreadMonthlyStat> findBetweenTwoDates(WorkGroup workGroup, Date beginDate, Date endDate) {
		return repository.findBetweenTwoDates(workGroup, null, null, beginDate, endDate, null);
	}

	@Override
	public void deleteBeforeDate(Date date) {
		repository.deleteBeforeDate(date);
	}

	@Override
	public List<Account> findAccountBetweenTwoDates(Date beginDate, Date endDate) {
		return repository.findAccountBetweenTwoDates(beginDate, endDate);
	}
}
