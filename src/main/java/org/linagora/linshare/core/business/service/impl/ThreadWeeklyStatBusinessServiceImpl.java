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

import org.linagora.linshare.core.business.service.ThreadWeeklyStatisticBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.ThreadWeeklyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ThreadDailyStatisticRepository;
import org.linagora.linshare.core.repository.ThreadWeeklyStatisticRepository;

public class ThreadWeeklyStatBusinessServiceImpl implements ThreadWeeklyStatisticBusinessService {

	private final ThreadWeeklyStatisticRepository repository;
	private final ThreadDailyStatisticRepository threadDailyStatRepository;

	public ThreadWeeklyStatBusinessServiceImpl(ThreadWeeklyStatisticRepository repository,
			ThreadDailyStatisticRepository threadDailyStatBusinessService) {
		this.repository = repository;
		this.threadDailyStatRepository = threadDailyStatBusinessService;
	}

	@Override
	public ThreadWeeklyStat create(WorkGroup workGroup, Date beginDate, Date endDate) throws BusinessException {
		Long actualOperationSum = threadDailyStatRepository.sumOfActualOperationSum(null, workGroup, beginDate, endDate);
		Long operationCount = threadDailyStatRepository.sumOfOperationCount(null, workGroup, beginDate, endDate);
		Long createOperationSum = threadDailyStatRepository.sumOfCreateOperationSum(null, workGroup, beginDate, endDate);
		Long createOperationCount = threadDailyStatRepository.sumOfCreateOperationCount(null, workGroup, beginDate,
				endDate);
		Long deleteOperationSum = threadDailyStatRepository.sumOfDeleteOperationSum(null, workGroup, beginDate, endDate);
		Long deleteOperationCount = threadDailyStatRepository.sumOfDeleteOperationCount(null, workGroup, beginDate,
				endDate);
		Long diffOperationSum = threadDailyStatRepository.sumOfDiffOperationSum(null, workGroup, beginDate, endDate);
		ThreadWeeklyStat entity = new ThreadWeeklyStat(workGroup, workGroup.getDomain(), workGroup.getDomain().getParentDomain(),
				operationCount, deleteOperationCount, createOperationCount, createOperationSum, deleteOperationSum,
				diffOperationSum, actualOperationSum);
		entity.setStatisticDate(endDate);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<ThreadWeeklyStat> findBetweenTwoDates(WorkGroup workGroup, Date beginDate, Date endDate) {
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

	@Override
	public List<String> findUuidAccountBetweenTwoDates(Date beginDate, Date endDate) {
		return repository.findUuidAccountBetweenTwoDates(beginDate, endDate);
	}

}
