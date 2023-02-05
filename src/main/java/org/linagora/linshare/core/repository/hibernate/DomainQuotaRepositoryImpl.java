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

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.linagora.linshare.core.domain.constants.QuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.repository.DomainQuotaRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DomainQuotaRepositoryImpl extends GenericQuotaRepositoryImpl<DomainQuota>
		implements DomainQuotaRepository {

	public DomainQuotaRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public DomainQuota find(AbstractDomain domain) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("domain", domain));
		DomainQuota quota = DataAccessUtils.singleResult(findByCriteria(criteria));
		if (quota != null) {
			this.getHibernateTemplate().refresh(quota);
		}
		return quota;
	}

	@Override
	public Long sumOfCurrentValueForSubdomains(AbstractDomain domain) {
		return sumOfCurrentValueForAllMySubdomains(domain) + sumOfcurrentValueForSubdomainsOfAllMySubdomains(domain);
	}

	private Long sumOfCurrentValueForAllMySubdomains(AbstractDomain domain) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("parentDomain", domain));
		criteria.setProjection(Projections.sum("currentValue"));
		List<DomainQuota> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(findByCriteria(criteria));
		}
		return 0L;
	}

	private Long sumOfcurrentValueForSubdomainsOfAllMySubdomains(AbstractDomain domain) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("parentDomain", domain));
		criteria.setProjection(Projections.sum("currentValueForSubdomains"));
		List<DomainQuota> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(findByCriteria(criteria));
		}
		return 0L;
	}

	@Override
	public  Long cascadeMaintenanceMode(AbstractDomain domain, boolean maintenance) {
		HibernateCallback<Long> action = null;
		if (domain.isRootDomain()) {
			action = new HibernateCallback<Long>() {
				public Long doInHibernate(final Session session)
						throws HibernateException {
					final Query<?> query = session.createQuery("UPDATE Quota SET maintenance = :maintenance");
					query.setParameter("maintenance", maintenance);
					return (long) query.executeUpdate();
				}
			};
		} else {
			action = new HibernateCallback<Long>() {
				public Long doInHibernate(final Session session)
						throws HibernateException {
					final Query<?> query = session.createQuery("UPDATE Quota SET maintenance = :maintenance WHERE domain = :domain OR parentDomain = :domain");
					query.setParameter("domain", domain);
					query.setParameter("maintenance", maintenance);
					return (long) query.executeUpdate();
				}
			};
		}
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(updatedCounter + " Quota (accounts, domains or containers) have been updated.");
		return updatedCounter;
	}

	@Override
	public Long cascadeDefaultQuota(AbstractDomain domain, Long quota) {
		// update quota of children
		Long count = 0L;
		count += cascadeDefaultQuotaToQuotaOfChildrenDomains(domain, quota);
		// update default quota of children
		count += cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(domain, quota);
		// update default quota of children of children if exists
		List<Long> quotaIdList = getQuotaIdforDefaultQuotaInSubDomains(domain, quota, QuotaType.DOMAIN_QUOTA);
		count += cascadeDefaultQuotaToSubDomainsDefaultQuota(domain, quota, quotaIdList);
		quotaIdList = getQuotaIdforQuotaInSubDomains(domain, quota, QuotaType.DOMAIN_QUOTA);
		count += cascadeDefaultQuotaToSubDomainsQuota(domain, quota, quotaIdList);
		return count;
	}

	public Long cascadeDefaultQuotaToQuotaOfChildrenDomains(AbstractDomain domain, Long quota) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException {
				final Query<?> query = session.createQuery("UPDATE DomainQuota SET quota = :quota WHERE parentDomain = :parentDomain AND quotaOverride = false");
				query.setParameter("quota", quota);
				query.setParameter("parentDomain", domain);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of DomainQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	public Long cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(AbstractDomain domain, Long quota) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException {
				final Query<?> query = session.createQuery("UPDATE DomainQuota SET defaultQuota = :defaultQuota WHERE parentDomain = :parentDomain AND defaultQuotaOverride = false");
				query.setParameter("defaultQuota", quota);
				query.setParameter("parentDomain", domain);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of DomainQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	@Override
	public List<DomainQuota> findAllByParent(AbstractDomain parentDomain) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("parentDomain", parentDomain));
		return findByCriteria(criteria);
	}

	@Override
	public Long cascadeDomainShared(DomainQuota quota, Boolean domainShared) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException {
				final Query<?> query = session.createQuery("UPDATE AccountQuota SET domainShared = :domainShared WHERE domain = :domain AND domainSharedOverride = false");
				query.setParameter("domainShared", domainShared);
				query.setParameter("domain", quota.getDomain());
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} domainShared of AccountQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	@Override
	public List<DomainQuota> findAllByDomain(AbstractDomain domain) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("domain", domain));
		return findByCriteria(criteria);
	}
}
