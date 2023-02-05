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

import org.linagora.linshare.core.business.service.UserWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserWeeklyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserDailyStatRepository;
import org.linagora.linshare.core.repository.UserWeeklyStatRepository;

public class UserWeeklyStatBusinessServiceImpl implements UserWeeklyStatBusinessService {

	private final UserWeeklyStatRepository repository;
	private final UserDailyStatRepository userDailyStatRepository;

	public UserWeeklyStatBusinessServiceImpl(final UserWeeklyStatRepository repository,
			final UserDailyStatRepository userDailyStatRepository) {
		this.repository = repository;
		this.userDailyStatRepository = userDailyStatRepository;
	}

	@Override
	public UserWeeklyStat create(User user, Date beginDate, Date endDate) throws BusinessException {
		Long actualOperationSum = userDailyStatRepository.sumOfActualOperationSum(null, user, beginDate, endDate);
		Long operationCount = userDailyStatRepository.sumOfOperationCount(null, user, beginDate, endDate);
		Long createOperationSum = userDailyStatRepository.sumOfCreateOperationSum(null, user, beginDate, endDate);
		Long createOperationCount = userDailyStatRepository.sumOfCreateOperationCount(null, user, beginDate, endDate);
		Long deleteOperationSum = userDailyStatRepository.sumOfDeleteOperationSum(null, user, beginDate, endDate);
		Long deleteOperationCount = userDailyStatRepository.sumOfDeleteOperationCount(null, user, beginDate, endDate);
		Long diffOperationSum = userDailyStatRepository.sumOfDiffOperationSum(null, user, beginDate, endDate);
		UserWeeklyStat entity = new UserWeeklyStat(user, user.getDomain(), user.getDomain().getParentDomain(),
				operationCount, deleteOperationCount, createOperationCount, createOperationSum, deleteOperationSum,
				diffOperationSum, actualOperationSum);
		entity.setStatisticDate(endDate);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<UserWeeklyStat> findBetweenTwoDates(User user, Date beginDate, Date endDate) {
		return repository.findBetweenTwoDates(user, null, null, beginDate, endDate, null);
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
