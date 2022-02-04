/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
