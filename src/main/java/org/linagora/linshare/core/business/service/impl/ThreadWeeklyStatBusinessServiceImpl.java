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
