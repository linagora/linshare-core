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

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.AccountPurgeStepEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;

abstract class GenericAccountRepositoryImpl<U extends Account> extends AbstractRepositoryImpl<U> implements AccountRepository<U> {

	public GenericAccountRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public U findByLsUuid(String lsUuid) {
		Assert.notNull(lsUuid);
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("lsUuid", lsUuid).ignoreCase());
		criteria.add(Restrictions.eq("destroyed", 0L));
		 List<U> users = findByCriteria(criteria);
	        if (users == null || users.isEmpty()) {
	            return null;
	        } else if (users.size() == 1) {
	            return users.get(0);
	        } else {
	            throw new IllegalStateException("lsUuid must be unique");
	        }
	}

	@Override
	public List<U> findByDomain(String domain) {
		Assert.notNull(domain);
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.uuid",domain));
		criteria.add(Restrictions.eq("destroyed", 0L));
		return findByCriteria(criteria);
	}


	@Override
	protected DetachedCriteria getNaturalKeyCriteria(U entity) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
//		det.add(Restrictions.eq("destroyed", false));
		// query
		det.add(Restrictions.eq("lsUuid", entity.getLsUuid()));
		return det;
	}

	@Override
	public List<U> findAll() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		return findByCriteria(criteria);
	}

	@Override
	public boolean exist(String lsUuid) {
		Assert.notNull(lsUuid);
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("lsUuid", lsUuid));
		criteria.add(Restrictions.eq("destroyed", 0L));
		List<U> accounts = null;
		accounts = findByCriteria(criteria);
		if (accounts == null || accounts.isEmpty()) {
			return false;
		} else if (accounts.size() == 1) {
			return true;
		} else {
			throw new IllegalStateException("lsUid must be unique");
		}
	}

	@Override
	public U update(U entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public SystemAccount getBatchSystemAccount() {
		DetachedCriteria det = DetachedCriteria.forClass(SystemAccount.class)
				.add(Restrictions.eq("lsUuid", "system"));
		List<U> users = findByCriteria(det);
		if (users == null || users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			return (SystemAccount) users.get(0);
		} else {
			throw new IllegalStateException("lsUuid must be unique");
		}
	}

	@Override
	public SystemAccount getUploadRequestSystemAccount() {
		DetachedCriteria det = DetachedCriteria.forClass(SystemAccount.class)
				.add(Restrictions.eq("lsUuid", "system-account-uploadrequest"));
		return (SystemAccount) DataAccessUtils
				.singleResult(findByCriteria(det));
	}

	@Override
	public void delete(U entity) throws BusinessException, IllegalArgumentException {
		entity.setDestroyed(getUserDestroyedMaxValue(entity.getDomain(), entity.getMail()) + 1);
		this.update(entity);
	}

	@Override
	public void markToPurge(U entity) throws BusinessException,
			IllegalArgumentException {
			entity.setPurgeStep(AccountPurgeStepEnum.WAIT_FOR_PURGE);
		this.update(entity);
	}

	@Override
	public void purge(U entity) throws BusinessException,
			IllegalArgumentException {
			entity.setPurgeStep(AccountPurgeStepEnum.PURGED);
		this.update(entity);
	}

	@Override
	public U findDeleted(String lsUuid) {
		Assert.notNull(lsUuid);
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("lsUuid", lsUuid).ignoreCase());
		criteria.add(Restrictions.gt("destroyed", 0L));
		return DataAccessUtils.requiredSingleResult(findByCriteria(criteria));
	}

	@Override
	public U findAccountsReadyToPurge(String lsUuid) {
		Assert.notNull(lsUuid);
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("purgeStep", AccountPurgeStepEnum.WAIT_FOR_PURGE));
		criteria.add(Restrictions.gt("destroyed", 0L));
		criteria.add(Restrictions.eq("lsUuid", lsUuid).ignoreCase());
		return DataAccessUtils.requiredSingleResult(findByCriteria(criteria));
	}

	@Override
	public List<String> findAllAccountsReadyToPurge() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("lsUuid"));
		criteria.add(Restrictions.eq("purgeStep", AccountPurgeStepEnum.WAIT_FOR_PURGE));
		criteria.add(Restrictions.gt("destroyed", 0L));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@Override
	public List<String> findAllDeletedAccountsToPurge(Date limit) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("lsUuid"));
		criteria.add(Restrictions.lt("modificationDate", limit));
		criteria.add(Restrictions.eq("purgeStep", AccountPurgeStepEnum.IN_USE));
		criteria.add(Restrictions.gt("destroyed", 0L));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	private long getUserDestroyedMaxValue(AbstractDomain domain, String mail) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("destroyed"));
		criteria.add(Restrictions.eq("mail", mail));
		criteria.add(Restrictions.eq("domain", domain));
		criteria.addOrder(Order.desc("destroyed"));
		@SuppressWarnings("unchecked")
		List<Long> list = listByCriteria(criteria);
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllKnownEmails(String pattern) {
		DetachedCriteria det = DetachedCriteria.forClass(Account.class);
		det.add(Restrictions.ilike("mail", pattern, MatchMode.ANYWHERE));
		det.setProjection(Projections.distinct(Projections.property("mail")));
		return listByCriteria(det);
	}

	@Override
	public List<String> findAllAccountWithMissingQuota() {
		HibernateCallback<List<String>> action = new HibernateCallback<List<String>>() {
			public List<String> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT DISTINCT ls_uuid AS uuid FROM account AS a");
				sb.append(" LEFT JOIN quota AS q");
				sb.append(" ON q.account_id = a.id");
				sb.append(" WHERE destroyed = 0");
				sb.append(" AND q.account_id is null");
				sb.append(";");
				final SQLQuery query = session.createSQLQuery(sb.toString());
				@SuppressWarnings("unchecked")
				List<String> res = query.list();
				return res;
			}
		};
		return getHibernateTemplate().execute(action);
	}
}
