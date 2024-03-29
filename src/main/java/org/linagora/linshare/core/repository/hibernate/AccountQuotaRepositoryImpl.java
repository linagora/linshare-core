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
package org.linagora.linshare.core.repository.hibernate;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.fields.AccountQuotaDtoField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class AccountQuotaRepositoryImpl extends GenericQuotaRepositoryImpl<AccountQuota>
		implements AccountQuotaRepository {

	public AccountQuotaRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public AccountQuota find(Account account) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("account", account));
		AccountQuota quota = DataAccessUtils.singleResult(findByCriteria(criteria));
		if (quota != null) {
			this.getHibernateTemplate().refresh(quota);
		}
		return quota;
	}

	@Override
	public List<String> findDomainUuidByBatchModificationDate(Date startDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.ge("batchModificationDate", startDate));
		// why do we check if the batchModificationDate is not in the future ?
		criteria.add(Restrictions.le("batchModificationDate", new Date()));
		criteria.createAlias("domain", "do");
		criteria.setProjection(Projections.distinct(Projections.property("do.uuid")));
		@SuppressWarnings("unchecked")
		List<String> listIdentifier = (List<String>) getHibernateTemplate().findByCriteria(criteria);
		return listIdentifier;
	}

	/**
	 * currentValue sum of all account quota in a container
	 */
	@Override
	public Long sumOfCurrentValue(ContainerQuota containerQuota) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("containerQuota", containerQuota));
		criteria.setProjection(Projections.sum("currentValue"));
		List<AccountQuota> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(list);
		}
		return 0L;
	}

	@Override
	public PageContainer<AccountQuota> findAll(
			AbstractDomain domain, boolean includeNestedDomains,
			SortOrder sortOrder, AccountQuotaDtoField sortField,
			Optional<Long> greaterThanOrEqualTo, Optional<Long> lessThanOrEqualTo,
			Optional<ContainerQuotaType> containerQuotaType,
			Optional<LocalDate> beginDate, Optional<LocalDate> endDate,
			PageContainer<AccountQuota> container) {
		// count matched data
		DetachedCriteria detachedCritCount = getCriteria(domain, includeNestedDomains, beginDate, endDate, greaterThanOrEqualTo, lessThanOrEqualTo, containerQuotaType);
		detachedCritCount.setProjection(Projections.rowCount());
		Long totalNumberElements = DataAccessUtils.longResult(findByCriteria(detachedCritCount));
		// retrieve one page.
		DetachedCriteria detachedCritData = getCriteria(domain, includeNestedDomains, beginDate, endDate, greaterThanOrEqualTo, lessThanOrEqualTo, containerQuotaType);
		Order order = null;
		switch (sortField) {
			case yesterdayUsedSpace:
				order = SortOrder.ASC.equals(sortOrder) ? Order.asc("lastValue") : Order.desc("lastValue");
				break;
			case usedSpace:
				order = SortOrder.ASC.equals(sortOrder) ? Order.asc("currentValue") : Order.desc("currentValue");
				break;
			default:
				order = SortOrder.ASC.equals(sortOrder) ? Order.asc(sortField.toString()) : Order.desc(sortField.toString());
				break;
		}
		detachedCritData.addOrder(order);
		PageContainer<AccountQuota> res = findAll(detachedCritData, totalNumberElements, container);
		return res;
	}

	private DetachedCriteria getCriteria(AbstractDomain domain, boolean includeNestedDomains,
			Optional<LocalDate> beginDate, Optional<LocalDate> endDate,
			Optional<Long> greaterThanOrEqualTo, Optional<Long> lessThanOrEqualTo,
			Optional<ContainerQuotaType> containerQuotaType) {
		// only user quota ? not wg quota ?
		DetachedCriteria crit = DetachedCriteria.forClass(AccountQuota.class);
		if (beginDate.isPresent()) {
			crit.add(Restrictions.ge("batchModificationDate", java.sql.Date.valueOf(beginDate.get())));
		}
		if (endDate.isPresent()) {
			crit.add(Restrictions.le("batchModificationDate", java.sql.Date.valueOf(endDate.get())));
		}
		if (greaterThanOrEqualTo.isPresent()) {
			crit.add(Restrictions.ge("currentValue", greaterThanOrEqualTo.get()));
		}
		if (lessThanOrEqualTo.isPresent()) {
			crit.add(Restrictions.le("currentValue", lessThanOrEqualTo.get()));
		}
		if (containerQuotaType.isPresent()) {
			crit.createAlias("containerQuota", "containerQuota");
			crit.add(Restrictions.eq("containerQuota.containerQuotaType", containerQuotaType.get()));
		}
		if (includeNestedDomains) {
			if (!domain.isRootDomain()) {
				crit.add(
					Restrictions.or(
						Restrictions.eq("domain", domain),
						Restrictions.eq("parentDomain", domain)
					)
				);
			}
		} else {
			crit.add(Restrictions.eq("domain", domain));
		}
		return crit;
	}
}
