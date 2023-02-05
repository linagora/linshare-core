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

import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.linagora.linshare.core.repository.UserDailyStatRepository;

public class UserDailyStatBusinessServiceImpl implements UserDailyStatBusinessService {

	private final UserDailyStatRepository repository;
	private final OperationHistoryRepository operationHistoryRepository;

	public UserDailyStatBusinessServiceImpl(final UserDailyStatRepository repository,
			final OperationHistoryRepository operationHistoryRepository) {
		this.repository = repository;
		this.operationHistoryRepository = operationHistoryRepository;
	}

	@Override
	public void deleteBeforeDate(Date creationDate) {
		repository.deleteBeforeDate(creationDate);
	}

	@Override
	public UserDailyStat create(User user, Long currentUsedSpace, Date date) throws BusinessException {
		Long sumCreateOperationValue = operationHistoryRepository.sumOperationValue(user, null, date,
				OperationHistoryTypeEnum.CREATE, null);
		Long sumDeleteOperationValue = operationHistoryRepository.sumOperationValue(user, null, date,
				OperationHistoryTypeEnum.DELETE, null);
		Long countCreateOperationValue = operationHistoryRepository.countOperationValue(user, null, date,
				OperationHistoryTypeEnum.CREATE, null);
		Long countDeleteOperationValue = operationHistoryRepository.countOperationValue(user, null, date,
				OperationHistoryTypeEnum.DELETE, null);
		Long countOperationValue = countDeleteOperationValue + countCreateOperationValue;
		Long diffOperationValue = sumCreateOperationValue + sumDeleteOperationValue;
		UserDailyStat entity = new UserDailyStat(user, user.getDomain(), user.getDomain().getParentDomain(),
				countOperationValue, countDeleteOperationValue, countCreateOperationValue, sumCreateOperationValue,
				sumDeleteOperationValue, diffOperationValue, currentUsedSpace);
		entity.setStatisticDate(date);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<Account> findAccountBetweenTwoDates(Date beginDate, Date endDate) {
		return repository.findAccountBetweenTwoDates(beginDate, endDate);
	}

	@Override
	public List<UserDailyStat> findBetweenTwoDates(User user, Date beginDate, Date endDate) {
		return repository.findBetweenTwoDates(user, null, null, beginDate, endDate, null);
	}

	@Override
	public List<String> findUuidAccountBetweenTwoDates(Date beginDate, Date endDate) {
		return repository.findUuidAccountBetweenTwoDates(beginDate, endDate);
	}
}
