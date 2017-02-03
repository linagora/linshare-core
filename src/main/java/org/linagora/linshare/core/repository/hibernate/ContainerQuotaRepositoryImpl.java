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
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.QuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.repository.ContainerQuotaRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ContainerQuotaRepositoryImpl extends GenericQuotaRepositoryImpl<ContainerQuota>
		implements ContainerQuotaRepository {

	public ContainerQuotaRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public ContainerQuota find(AbstractDomain domain, ContainerQuotaType ContainerQuotaType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("domain", domain));
		criteria.add(Restrictions.eq("containerQuotaType", ContainerQuotaType));
		ContainerQuota quota = DataAccessUtils.singleResult(findByCriteria(criteria));
		if (quota != null) {
			this.getHibernateTemplate().refresh(quota);
		}
		return quota;
	}

	@Override
	public Long sumOfCurrentValue(DomainQuota domainQuota) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("domainQuota", domainQuota));
		criteria.add(Restrictions.ge("batchModificationDate", getTodayBegin()));
		criteria.setProjection(Projections.sum("currentValue"));
		List<ContainerQuota> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(findByCriteria(criteria));
		}
		return 0L;
	}

	@Override
	public Long cascadeMaintenanceMode(ContainerQuota container, boolean maintenance) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE AccountQuota SET maintenance = :maintenance WHERE containerQuota = :containerQuota");
				query.setParameter("containerQuota", container);
				query.setParameter("maintenance", maintenance);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(updatedCounter + " AccountQuota have been updated.");
		return updatedCounter;
	}

	@Override
	public Long cascadeDefaultQuota(AbstractDomain domain, Long quota, ContainerQuotaType containerType) {
		// update quota of children
		Long count = 0L;
		count += cascadeDefaultQuotaToQuotaOfChildrenDomains(domain, quota);
		// update default quota of children
		count += cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(domain, quota);
		// update default quota of children of children if exists
		List<Long> quotaIdList = getQuotaIdforDefaultQuotaInSubDomains(domain, quota, QuotaType.CONTAINER_QUOTA, containerType);
		count += cascadeDefaultQuotaToSubDomainsDefaultQuota(domain, quota, quotaIdList);
		quotaIdList = getQuotaIdforQuotaInSubDomains(domain, quota, QuotaType.CONTAINER_QUOTA, containerType);
		count += cascadeDefaultQuotaToSubDomainsQuota(domain, quota, quotaIdList);
		return count;
	}

	public Long cascadeDefaultQuotaToQuotaOfChildrenDomains(AbstractDomain domain, Long quota) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE ContainerQuota SET quota = :quota WHERE parentDomain = :parentDomain AND quotaOverride = false");
				query.setParameter("quota", quota);
				query.setParameter("parentDomain", domain);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of ContainerQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	public Long cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(AbstractDomain domain, Long quota) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE ContainerQuota SET defaultQuota = :defaultQuota WHERE parentDomain = :parentDomain AND defaultQuotaOverride = false");
				query.setParameter("defaultQuota", quota);
				query.setParameter("parentDomain", domain);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of ContainerQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	@Override
	public Long cascadeDefaultMaxFileSize(AbstractDomain domain, Long quota, ContainerQuotaType containerType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long cascadeDefaultAccountQuota(AbstractDomain domain, Long quota, ContainerQuotaType containerType) {
		// TODO Auto-generated method stub
		return null;
	}
}
