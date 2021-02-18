/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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

import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainDailyStatisticRepository;
import org.linagora.linshare.core.repository.ThreadDailyStatisticRepository;
import org.linagora.linshare.core.repository.UserDailyStatRepository;

public class DomainDailyStatBusinessServiceImpl implements DomainDailyStatBusinessService {

	private final DomainDailyStatisticRepository repository;
	private final UserDailyStatRepository userDailyStatRepository;
	private final ThreadDailyStatisticRepository threadDailyStatRepository;

	public DomainDailyStatBusinessServiceImpl(final DomainDailyStatisticRepository repository,
			final UserDailyStatRepository userDailyStatRepository,
			final ThreadDailyStatisticRepository threadDailyStatRepository) {
		this.repository = repository;
		this.userDailyStatRepository = userDailyStatRepository;
		this.threadDailyStatRepository = threadDailyStatRepository;
	}

	@Override
	public DomainDailyStat create(AbstractDomain domain, Long currentUsedSpace, Date startDate, Date endDate) throws BusinessException {

		Long createOperationSumUsers = userDailyStatRepository.sumOfCreateOperationSum(domain, null, startDate, endDate);
		Long createOperationSumThreads = threadDailyStatRepository.sumOfCreateOperationSum(domain, null, startDate, endDate);
		Long createOperationSum = createOperationSumUsers + createOperationSumThreads;

		Long createOperationCountUsers = userDailyStatRepository.sumOfCreateOperationCount(domain, null, startDate, endDate);
		Long createOperationCountThreads = threadDailyStatRepository.sumOfCreateOperationCount(domain, null, startDate,
				endDate);
		Long createOperationCount = createOperationCountUsers + createOperationCountThreads;

		Long deleteOperationSumUsers = userDailyStatRepository.sumOfDeleteOperationSum(domain, null, startDate, endDate);
		Long deleteOperationSumThreads = threadDailyStatRepository.sumOfDeleteOperationSum(domain, null, startDate, endDate);
		Long deleteOperationSum = deleteOperationSumUsers + deleteOperationSumThreads;

		Long deleteOperationCountUsers = userDailyStatRepository.sumOfDeleteOperationCount(domain, null, startDate, endDate);
		Long deleteOperationCountThreads = threadDailyStatRepository.sumOfDeleteOperationCount(domain, null, startDate,
				endDate);
		Long deleteOperationCount = deleteOperationCountUsers + deleteOperationCountThreads;

		Long operationCount = deleteOperationCount + createOperationCount;
		Long diffOperationSum = createOperationSum + deleteOperationSum;
		DomainDailyStat entity = new DomainDailyStat(domain, domain.getParentDomain(), operationCount,
				deleteOperationCount, createOperationCount, createOperationSum, deleteOperationSum, diffOperationSum,
				currentUsedSpace);
		entity.setStatisticDate(endDate);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<DomainDailyStat> findBetweenTwoDates(AbstractDomain domain, Date beginDate, Date endDate) {
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
