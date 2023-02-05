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
package org.linagora.linshare.core.repository;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GenericStatistic;

public interface GenericStatisticRepository<T extends GenericStatistic> extends AbstractRepository<T> {

	List<T> findBetweenTwoDates(Account account, AbstractDomain domain, AbstractDomain parentDomain, Date beginDate, Date endDate, StatisticType statisticType);

	void deleteBeforeDate(Date date);

	List<Account> findAccountBetweenTwoDates(Date beginDate, Date endDate);

	List<String> findUuidAccountBetweenTwoDates(Date beginDate, Date endDate);

	List<AbstractDomain> findDomainBetweenTwoDates(Date beginDate, Date endDate);

	List<String> findIdentifierDomainBetweenTwoDates(Date beginDate, Date endDate);

	T create(T entity);

	Long sumOfOperationCount(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfDeleteOperationCount(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfCreateOperationCount(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfCreateOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfDeleteOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfDiffOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfActualOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate);
}
