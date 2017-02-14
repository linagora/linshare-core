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
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
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
		count += cascadeDefaultQuotaToQuotaOfChildrenDomains(domain, quota, containerType);
		// update default quota of children
		count += cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(domain, quota, containerType);
		// update default quota of children of children if exists
		List<Long> quotaIdList = getQuotaIdforDefaultQuotaInSubDomains(domain, quota, QuotaType.CONTAINER_QUOTA, containerType);
		count += cascadeDefaultQuotaToSubDomainsDefaultQuota(domain, quota, quotaIdList);
		quotaIdList = getQuotaIdforQuotaInSubDomains(domain, quota, QuotaType.CONTAINER_QUOTA, containerType);
		count += cascadeDefaultQuotaToSubDomainsQuota(domain, quota, quotaIdList);
		return count;
	}

	public Long cascadeDefaultQuotaToQuotaOfChildrenDomains(AbstractDomain domain, Long quota, ContainerQuotaType containerType) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE ContainerQuota SET quota = :quota WHERE parentDomain = :parentDomain AND quotaOverride = false AND containerQuotaType = :containerType");
				query.setParameter("quota", quota);
				query.setParameter("parentDomain", domain);
				query.setParameter("containerType", containerType);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of ContainerQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	public Long cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(AbstractDomain domain, Long quota, ContainerQuotaType containerType) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE ContainerQuota SET defaultQuota = :defaultQuota WHERE parentDomain = :parentDomain AND defaultQuotaOverride = false AND containerQuotaType = :containerType");
				query.setParameter("defaultQuota", quota);
				query.setParameter("parentDomain", domain);
				query.setParameter("containerType", containerType);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of ContainerQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	@Override
	public Long cascadeDefaultAccountQuota(AbstractDomain domain, Long quota, ContainerQuotaType containerType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long cascadeMaxFileSize(ContainerQuota container, Long maxFileSize) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE AccountQuota SET maxFileSize = :maxFileSize WHERE containerQuota = :containerQuota AND maxFileSizeOverride = false");
				query.setParameter("maxFileSize", maxFileSize);
				query.setParameter("containerQuota", container);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} maxFileSize of AccountQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	@Override
	public Long cascadeAccountQuota(ContainerQuota container, Long accountQuota) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE AccountQuota SET quota = :quota WHERE containerQuota = :containerQuota AND quotaOverride = false");
				query.setParameter("quota", accountQuota);
				query.setParameter("containerQuota", container);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} accountQuota of AccountQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	@Override
	public Long cascadeDefaultMaxFileSize(AbstractDomain domain, Long maxFileSize, ContainerQuotaType containerType) {
		// update maxFileSize of children
		Long count = 0L;
		count += cascadeDefaultMaxFileSizeToMaxFileSizeOfChildrenDomains(domain, maxFileSize, containerType);
		// update default maxFileSize of children
		count += cascadeDefaultMaxFileSizeToDefaultMaxFileSizeOfChildrenDomains(domain, maxFileSize, containerType);
		// update default quota of children of children if exists
		List<Long> quotaIdList = getQuotaIdforDefaultMaxFileSizeInSubDomains(domain, QuotaType.CONTAINER_QUOTA, containerType);
		count += cascadeDefaultMaxFileSizeToSubDomainsDefaultMaxFileSize(domain, maxFileSize, quotaIdList);
		quotaIdList = getQuotaIdforMaxFileSizeInSubDomains(domain, QuotaType.CONTAINER_QUOTA, containerType);
		count += cascadeDefaultMaxFileSizeToSubDomainsMaxFileSize(domain, maxFileSize, quotaIdList);
		return count;
	}

	public Long cascadeDefaultMaxFileSizeToMaxFileSizeOfChildrenDomains(AbstractDomain domain, Long maxFileSize, ContainerQuotaType containerType) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE ContainerQuota SET maxFileSize = :maxFileSize WHERE parentDomain = :parentDomain AND maxFileSizeOverride = false AND containerQuotaType = :containerType");
				query.setParameter("maxFileSize", maxFileSize);
				query.setParameter("parentDomain", domain);
				query.setParameter("containerType", containerType);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} maxFileSize of ContainerQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	public Long cascadeDefaultMaxFileSizeToDefaultMaxFileSizeOfChildrenDomains(AbstractDomain domain, Long maxFileSize, ContainerQuotaType containerType) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE ContainerQuota SET defaultMaxFileSize = :maxFileSize WHERE parentDomain = :parentDomain AND defaultMaxFileSizeOverride = false AND containerQuotaType = :containerType");
				query.setParameter("maxFileSize", maxFileSize);
				query.setParameter("parentDomain", domain);
				query.setParameter("containerType", containerType);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} defaultMaxFileSize of ContainerQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	public List<Long> getQuotaIdforDefaultMaxFileSizeInSubDomains(AbstractDomain domain, QuotaType type, ContainerQuotaType containerType) {
		HibernateCallback<List<Long>> action = new HibernateCallback<List<Long>>() {
			public List<Long> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT DISTINCT child.id AS child_id FROM quota AS father");
				sb.append(" JOIN quota AS child");
				sb.append(" ON child.domain_parent_id = father.domain_id");
				sb.append(" AND child.quota_type = :domainType ");
				sb.append(" AND father.domain_parent_id = :domainId ");
				sb.append(" AND father.default_max_file_size_override = false");
				sb.append(" WHERE father.quota_type = :domainType");
				if (containerType != null) {
					sb.append(" AND child.container_type = :containerType");
				}
				sb.append(" AND child.default_max_file_size_override = false");
				sb.append(";");
				final SQLQuery query = session.createSQLQuery(sb.toString());
				query.setLong("domainId", domain.getPersistenceId());
				query.addScalar("child_id", LongType.INSTANCE);
				query.setString("domainType", type.name());
				if (containerType != null) {
					query.setString("containerType", containerType.name());
				}
				@SuppressWarnings("unchecked")
				List<Long> res = query.list();
				logger.debug("child_ids :"  + res);
				return res;
			}
		};
		return getHibernateTemplate().execute(action);
	}

	public Long cascadeDefaultMaxFileSizeToSubDomainsDefaultMaxFileSize(AbstractDomain domain, Long maxFileSize, List<Long> quotaIdList) {
		if (quotaIdList == null || quotaIdList.isEmpty()) {
			return 0L;
		}
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE Quota SET default_max_file_size = :maxFileSize WHERE id IN :list_quota_id ;");
				final Query query = session.createSQLQuery(sb.toString());
				query.setLong("maxFileSize", maxFileSize);
				query.setParameterList("list_quota_id", quotaIdList);
				return (long) query.executeUpdate();
			}
		};
		long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} default_max_file_size of ContainerQuota have been updated.", updatedCounter );
		return updatedCounter;
	}

	public List<Long> getQuotaIdforMaxFileSizeInSubDomains(AbstractDomain domain, QuotaType type, ContainerQuotaType containerType) {
		HibernateCallback<List<Long>> action = new HibernateCallback<List<Long>>() {
			public List<Long> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT DISTINCT child.id AS child_id FROM quota AS father");
				sb.append(" JOIN quota AS child");
				sb.append(" ON child.domain_parent_id = father.domain_id");
				sb.append(" AND child.quota_type = :domainType ");
				sb.append(" AND father.domain_parent_id = :domainId ");
				sb.append(" AND father.max_file_size_override = false");
				if (containerType != null) {
					sb.append(" AND father.container_type = :containerType");
				}
				sb.append(" WHERE father.quota_type = :domainType");
				if (containerType != null) {
					sb.append(" AND child.container_type = :containerType");
				}
				sb.append(" AND child.max_file_size_override = false");
				sb.append(";");
				final SQLQuery query = session.createSQLQuery(sb.toString());
				query.setLong("domainId", domain.getPersistenceId());
				query.addScalar("child_id", LongType.INSTANCE);
				query.setString("domainType", type.name());
				if (containerType != null) {
					query.setString("containerType", containerType.name());
				}
				@SuppressWarnings("unchecked")
				List<Long> res = query.list();
				logger.debug("child_ids :"  + res);
				return res;
			}
		};
		return getHibernateTemplate().execute(action);
	}

	public Long cascadeDefaultMaxFileSizeToSubDomainsMaxFileSize(AbstractDomain domain, Long maxFileSize, List<Long> quotaIdList) {
		if (quotaIdList == null || quotaIdList.isEmpty()) {
			return 0L;
		}
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE Quota SET max_file_size = :maxFileSize WHERE id IN :list_quota_id ;");
				final Query query = session.createSQLQuery(sb.toString());
				query.setLong("maxFileSize", maxFileSize);
				query.setParameterList("list_quota_id", quotaIdList);
				return (long) query.executeUpdate();
			}
		};
		long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of DomainQuota have been updated.", updatedCounter );
		return updatedCounter;
	}
}
