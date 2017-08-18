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
package org.linagora.linshare.core.repository.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.QuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.repository.DomainQuotaRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

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
						throws HibernateException, SQLException {
					final Query query = session.createQuery("UPDATE Quota SET maintenance = :maintenance");
					query.setParameter("maintenance", maintenance);
					return (long) query.executeUpdate();
				}
			};
		} else {
			action = new HibernateCallback<Long>() {
				public Long doInHibernate(final Session session)
						throws HibernateException, SQLException {
					final Query query = session.createQuery("UPDATE Quota SET maintenance = :maintenance WHERE domain = :domain OR parentDomain = :domain");
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
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE DomainQuota SET quota = :quota WHERE parentDomain = :parentDomain AND quotaOverride = false");
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
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE DomainQuota SET defaultQuota = :defaultQuota WHERE parentDomain = :parentDomain AND defaultQuotaOverride = false");
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
}
