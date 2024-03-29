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

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.domain.entities.DomainMonthlyStat;
import org.linagora.linshare.core.domain.entities.DomainWeeklyStat;
import org.linagora.linshare.core.domain.entities.Statistic;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.ThreadMonthlyStat;
import org.linagora.linshare.core.domain.entities.ThreadWeeklyStat;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.domain.entities.UserMonthlyStat;
import org.linagora.linshare.core.domain.entities.UserWeeklyStat;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.entities.fields.StorageConsumptionStatisticField;
import org.linagora.linshare.core.repository.StatisticRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.collect.Maps;

public class StatisticRepositoryImpl extends GenericStatisticRepositoryImpl<Statistic> implements StatisticRepository{

	@SuppressWarnings("rawtypes")
	private static HashMap<StatisticType, Class> classes = Maps.newHashMap();

	public StatisticRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
		classes.put(StatisticType.USER_DAILY_STAT, UserDailyStat.class);
		classes.put(StatisticType.USER_WEEKLY_STAT, UserWeeklyStat.class);
		classes.put(StatisticType.USER_MONTHLY_STAT, UserMonthlyStat.class);
		classes.put(StatisticType.WORK_GROUP_DAILY_STAT, ThreadDailyStat.class);
		classes.put(StatisticType.WORK_GROUP_WEEKLY_STAT, ThreadWeeklyStat.class);
		classes.put(StatisticType.WORK_GROUP_MONTHLY_STAT, ThreadMonthlyStat.class);
		classes.put(StatisticType.DOMAIN_DAILY_STAT, DomainDailyStat.class);
		classes.put(StatisticType.DOMAIN_WEEKLY_STAT, DomainWeeklyStat.class);
		classes.put(StatisticType.DOMAIN_MONTHLY_STAT, DomainMonthlyStat.class);
	}

	@Override
	public PageContainer<Statistic> findAll(
			AbstractDomain domain, boolean includeNestedDomains,
			String accountUuid,
			SortOrder sortOrder, StorageConsumptionStatisticField sortField,
			StatisticType statisticType,
			LocalDate beginDate, LocalDate endDate,
			PageContainer<Statistic> container) {
		// count matched data
		DetachedCriteria detachedCritCount = getCriteria(domain, includeNestedDomains, beginDate, endDate, statisticType);
		detachedCritCount.setProjection(Projections.rowCount());
		Long totalNumberElements = DataAccessUtils.longResult(findByCriteria(detachedCritCount));
		// retrieve one page.
		DetachedCriteria detachedCritData = getCriteria(domain, includeNestedDomains, beginDate, endDate, statisticType);
		Order order = SortOrder.ASC.equals(sortOrder) ? Order.asc(sortField.toString()) : Order.desc(sortField.toString());
		detachedCritData.addOrder(order);
		PageContainer<Statistic> res = findAll(detachedCritData, totalNumberElements, container);
		return res;
	}

	private DetachedCriteria getCriteria(AbstractDomain domain, boolean includeNestedDomains, LocalDate beginDate, LocalDate endDate, StatisticType statisticType) {
		DetachedCriteria crit = DetachedCriteria.forClass(classes.get(statisticType));
		crit.add(Restrictions.ge("statisticDate", Date.valueOf(beginDate)));
		crit.add(Restrictions.le("statisticDate", Date.valueOf(endDate)));
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
