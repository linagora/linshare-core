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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

//TODO FIXME Quota & Statistics
public class OperationHistoryRepositoryImpl extends AbstractRepositoryImpl<OperationHistory>
		implements OperationHistoryRepository {

	public OperationHistoryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public OperationHistory create(OperationHistory entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(OperationHistory entity) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		return criteria.add(Restrictions.eq("id", entity.getId()));
	}

	@Override
	public Long sumOperationValue(Account account, AbstractDomain domain, Date creationDate,
			OperationHistoryTypeEnum operationType, ContainerQuotaType containerQuotaType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		if (operationType != null) {
			criteria.add(Restrictions.eq("operationType", operationType));
		}
		if (creationDate != null) {
			criteria.add(Restrictions.le("creationDate", creationDate));
		}
		if (containerQuotaType != null) {
			criteria.add(Restrictions.eq("containerQuotaType", containerQuotaType));
		}
		criteria.setProjection(Projections.sum("operationValue"));
		List<OperationHistory> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(findByCriteria(criteria));
		}
		return (long) 0;
	}

	@Override
	public Long countOperationValue(Account account, AbstractDomain domain, Date creationDate,
			OperationHistoryTypeEnum operationType, ContainerQuotaType containerQuotaType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		if (creationDate != null) {
			criteria.add(Restrictions.le("creationDate", creationDate));
		}
		if (operationType != null) {
			criteria.add(Restrictions.eq("operationType", operationType));
		}
		if (containerQuotaType != null) {
			criteria.add(Restrictions.eq("containerQuotaType", containerQuotaType));
		}
		criteria.setProjection(Projections.rowCount());
		return DataAccessUtils.longResult(findByCriteria(criteria));
	}

	@Override
	public List<AbstractDomain> findDomainBeforeDate(Date creationDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.lt("creationDate", creationDate));
		criteria.setProjection(Projections.distinct(Projections.property("domain")));
		@SuppressWarnings("unchecked")
		List<AbstractDomain> result = (List<AbstractDomain>) getHibernateTemplate().findByCriteria(criteria);
		return result;
	}

	@Override
	public List<OperationHistory> find(Account account, AbstractDomain domain, ContainerQuotaType containerQuotaType, Date date, OperationHistoryTypeEnum type) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (containerQuotaType != null) {
			criteria.add(Restrictions.eq("containerQuotaType", containerQuotaType));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		if (date != null) {
			criteria.add(Restrictions.le("creationDate", date));
		}
		if (type != null) {
			criteria.add(Restrictions.eq("operationType", type));
		}
		return findByCriteria(criteria);
	}

	@Override
	public List<String> findUuidAccountBeforeDate(Date date, ContainerQuotaType containerQuotaType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("containerQuotaType", containerQuotaType));
		criteria.add(Restrictions.le("creationDate", date));
		criteria.createAlias("account", "ac");
		criteria.setProjection(Projections.distinct(Projections.property("ac.lsUuid")));
		@SuppressWarnings("unchecked")
		List<String> listlsUuid = (List<String>) getHibernateTemplate().findByCriteria(criteria);
		return listlsUuid;
	}

	@Override
	public void deleteBeforeDateByAccount(Date date, Account account) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery("DELETE from OperationHistory WHERE account = :account and creationDate <= :date");
				query.setParameter("date", date);
				query.setParameter("account", account);
				return (long) query.executeUpdate();
			}
		};
		Long execute = getHibernateTemplate().execute(action);
		logger.debug("{} OperationHistory deleted for account {} for operation history created before  {}", execute, account, date);
	}
}
