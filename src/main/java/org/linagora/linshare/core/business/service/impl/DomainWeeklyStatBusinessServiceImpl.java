/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainWeeklyStat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainDailyStatisticRepository;
import org.linagora.linshare.core.repository.DomainWeeklyStatRepository;

public class DomainWeeklyStatBusinessServiceImpl implements DomainWeeklyStatBusinessService {

	private final DomainWeeklyStatRepository repository;
	private final DomainDailyStatisticRepository domainDailyStatRepository;

	public DomainWeeklyStatBusinessServiceImpl(final DomainWeeklyStatRepository repository,
			final DomainDailyStatisticRepository domainDailyStatRepository) {
		this.repository = repository;
		this.domainDailyStatRepository = domainDailyStatRepository;
	}

	@Override
	public DomainWeeklyStat create(AbstractDomain domain, Date beginDate, Date endDate) throws BusinessException {
		Long actualOperationSum = domainDailyStatRepository.sumOfActualOperationSum(domain, null, beginDate, endDate);
		Long operationCount = domainDailyStatRepository.sumOfOperationCount(domain, null, beginDate, endDate);
		Long createOperationSum = domainDailyStatRepository.sumOfCreateOperationSum(domain, null, beginDate, endDate);
		Long createOperationCount = domainDailyStatRepository.sumOfCreateOperationCount(domain, null, beginDate,
				endDate);
		Long deleteOperationSum = domainDailyStatRepository.sumOfDeleteOperationSum(domain, null, beginDate, endDate);
		Long deleteOperationCount = domainDailyStatRepository.sumOfDeleteOperationCount(domain, null, beginDate,
				endDate);
		Long diffOperationSum = domainDailyStatRepository.sumOfDiffOperationSum(domain, null, beginDate, endDate);
		DomainWeeklyStat entity = new DomainWeeklyStat(domain, domain.getParentDomain(), operationCount,
				deleteOperationCount, createOperationCount, createOperationSum, deleteOperationSum, diffOperationSum,
				actualOperationSum);
		entity.setStatisticDate(endDate);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<DomainWeeklyStat> findBetweenTwoDates(AbstractDomain domain, Date beginDate, Date endDate) {
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
