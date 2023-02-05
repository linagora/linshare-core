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

import org.linagora.linshare.core.business.service.UserMonthlyStatBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserMonthlyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserMonthlyStatRepository;
import org.linagora.linshare.core.repository.UserWeeklyStatRepository;

public class UserMonthlyStatBusinessServiceImpl implements UserMonthlyStatBusinessService {

	private final UserMonthlyStatRepository repository;
	private final UserWeeklyStatRepository userWeeklyStatRepository;

	public UserMonthlyStatBusinessServiceImpl(final UserMonthlyStatRepository repository,
			final UserWeeklyStatRepository userWeeklyStatRepository) {
		this.repository = repository;
		this.userWeeklyStatRepository = userWeeklyStatRepository;
	}

	@Override
	public UserMonthlyStat create(User user, Date beginDate, Date endDate) throws BusinessException {
		Long actualOperationSum = userWeeklyStatRepository.sumOfActualOperationSum(null, user, beginDate, endDate);
		Long actualOperationCount = userWeeklyStatRepository.sumOfOperationCount(null, user, beginDate, endDate);
		Long createOperationCount = userWeeklyStatRepository.sumOfCreateOperationCount(null, user, beginDate, endDate);
		Long createOperationSum = userWeeklyStatRepository.sumOfCreateOperationSum(null, user, beginDate, endDate);
		Long deleteOperationCount = userWeeklyStatRepository.sumOfDeleteOperationCount(null, user, beginDate, endDate);
		Long deleteOperationSum = userWeeklyStatRepository.sumOfDeleteOperationSum(null, user, beginDate, endDate);
		Long diffOperationSum = userWeeklyStatRepository.sumOfDiffOperationSum(null, user, beginDate, endDate);
		UserMonthlyStat entity = new UserMonthlyStat(user, user.getDomain(), user.getDomain().getParentDomain(),
				actualOperationCount, deleteOperationCount, createOperationCount, createOperationSum,
				deleteOperationSum, diffOperationSum, actualOperationSum);
		entity.setStatisticDate(endDate);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<UserMonthlyStat> findBetweenTwoDates(User user, Date beginDate, Date endDate) {
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

}
