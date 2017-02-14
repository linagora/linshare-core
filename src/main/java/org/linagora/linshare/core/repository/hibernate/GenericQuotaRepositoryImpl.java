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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.QuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.GenericQuotaRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public abstract class GenericQuotaRepositoryImpl<T extends Quota> extends AbstractRepositoryImpl<T>
		implements GenericQuotaRepository<T> {

	public GenericQuotaRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public T create(T entity) throws BusinessException {
		entity.setLastValue(0L);
		entity.setCurrentValue(0L);
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setBatchModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public T update(T entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public T updateByBatch(T entity) throws BusinessException {
		entity.setBatchModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public T update(T entity, Long curentValue) throws BusinessException{
		entity.setLastValue(entity.getCurrentValue());
		entity.setCurrentValue(curentValue + entity.getCurrentValue());
		return super.update(entity);
	}

	@Override
	public T find(String uuid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("uuid", uuid));
		return DataAccessUtils.singleResult(findByCriteria(criteria));
	}

	@Override
	public List<T> findAll(AbstractDomain domain) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("domain", domain));
		return findByCriteria(criteria);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(T entity) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		return criteria.add(Restrictions.eq("id", entity.getId()));
	}

	public List<Long> getQuotaIdforDefaultQuotaInSubDomains(AbstractDomain domain, Long quota, QuotaType type) {
		return getQuotaIdforDefaultQuotaInSubDomains(domain, quota, type, null);
	}

	public List<Long> getQuotaIdforDefaultQuotaInSubDomains(AbstractDomain domain, Long quota, QuotaType type, ContainerQuotaType containerType) {
		HibernateCallback<List<Long>> action = new HibernateCallback<List<Long>>() {
			public List<Long> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT DISTINCT child.id AS child_id FROM quota AS father");
				sb.append(" JOIN quota AS child");
				sb.append(" ON child.domain_parent_id = father.domain_id");
				sb.append(" AND child.quota_type = :domainType ");
				sb.append(" AND father.domain_parent_id = :domainId ");
				sb.append(" AND father.default_quota_override = false");
				sb.append(" WHERE father.quota_type = :domainType");
				if (containerType != null) {
					sb.append(" AND child.container_type = :containerType");
				}
				sb.append(" AND child.default_quota_override = false");
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

	public Long cascadeDefaultQuotaToSubDomainsDefaultQuota(AbstractDomain domain, Long quota, List<Long> quotaIdList) {
		if (quotaIdList == null || quotaIdList.isEmpty()) {
			return 0L;
		}
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE Quota SET default_quota = :quota WHERE id IN :list_quota_id ;");
				final Query query = session.createSQLQuery(sb.toString());
				query.setLong("quota", quota);
				query.setParameterList("list_quota_id", quotaIdList);
				return (long) query.executeUpdate();
			}
		};
		long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} defaultQuota of DomainQuota have been updated.", updatedCounter );
		return updatedCounter;
	}

	public List<Long> getQuotaIdforQuotaInSubDomains(AbstractDomain domain, Long quota, QuotaType type) {
		return getQuotaIdforQuotaInSubDomains(domain, quota, type, null);
	}

	public List<Long> getQuotaIdforQuotaInSubDomains(AbstractDomain domain, Long quota, QuotaType type, ContainerQuotaType containerType) {
		HibernateCallback<List<Long>> action = new HibernateCallback<List<Long>>() {
			public List<Long> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT DISTINCT child.id AS child_id FROM quota AS father");
				sb.append(" JOIN quota AS child");
				sb.append(" ON child.domain_parent_id = father.domain_id");
				sb.append(" AND child.quota_type = :domainType ");
				sb.append(" AND father.domain_parent_id = :domainId ");
				sb.append(" AND father.quota_override = false");
				if (containerType != null) {
					sb.append(" AND father.container_type = :containerType");
				}
				sb.append(" WHERE father.quota_type = :domainType");
				if (containerType != null) {
					sb.append(" AND child.container_type = :containerType");
				}
				sb.append(" AND child.quota_override = false");
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

	public Long cascadeDefaultQuotaToSubDomainsQuota(AbstractDomain domain, Long quota, List<Long> quotaIdList) {
		if (quotaIdList == null || quotaIdList.isEmpty()) {
			return 0L;
		}
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE Quota SET quota = :quota WHERE id IN :list_quota_id ;");
				final Query query = session.createSQLQuery(sb.toString());
				query.setLong("quota", quota);
				query.setParameterList("list_quota_id", quotaIdList);
				return (long) query.executeUpdate();
			}
		};
		long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of DomainQuota have been updated.", updatedCounter );
		return updatedCounter;
	}

	protected  Date getYesterdayBegin() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		return calendar.getTime();
	}

	protected Date getYesterdayEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		return calendar.getTime();
	}

	protected Date getTodayBegin() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		return calendar.getTime();
	}

	protected Date getTodayEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		return calendar.getTime();
	}
}
