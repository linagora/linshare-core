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

import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.linagora.linshare.core.repository.ThreadDailyStatisticRepository;
import org.linagora.linshare.core.domain.entities.WorkGroup;

public class ThreadDailyStatBusinessServiceImpl implements ThreadDailyStatBusinessService {

	private final ThreadDailyStatisticRepository repository;
	private final OperationHistoryRepository operationHistoryRepository;

	public ThreadDailyStatBusinessServiceImpl(final ThreadDailyStatisticRepository repository,
			final OperationHistoryRepository operationHistoryRepository) {
		this.repository = repository;
		this.operationHistoryRepository = operationHistoryRepository;
	}

	@Override
	public ThreadDailyStat create(WorkGroup workGroup, Long currentUsedSpace, Date date) throws BusinessException {
		Long createOperationSum = operationHistoryRepository.sumOperationValue(workGroup, null, date,
				OperationHistoryTypeEnum.CREATE, null);
		Long deleteOperationSum = operationHistoryRepository.sumOperationValue(workGroup, null, date,
				OperationHistoryTypeEnum.DELETE, null);
		Long createOperationCount = operationHistoryRepository.countOperationValue(workGroup, null, date,
				OperationHistoryTypeEnum.CREATE, null);
		Long deleteOperationCount = operationHistoryRepository.countOperationValue(workGroup, null, date,
				OperationHistoryTypeEnum.DELETE, null);
		Long operationCount = deleteOperationCount + createOperationCount;
		Long diffOperationSum = createOperationSum + deleteOperationSum;
		ThreadDailyStat entity = new ThreadDailyStat(workGroup, workGroup.getDomain(), workGroup.getDomain().getParentDomain(),
				operationCount, deleteOperationCount, createOperationCount, createOperationSum, deleteOperationSum,
				diffOperationSum, currentUsedSpace);
		entity.setStatisticDate(date);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<ThreadDailyStat> findBetweenTwoDates(WorkGroup workGroup, Date beginDate, Date endDate) {
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
