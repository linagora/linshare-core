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

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import javax.annotation.Nonnull;
import org.linagora.linshare.core.batches.impl.gdpr.GDPRConstants;
import org.linagora.linshare.core.domain.constants.AccountPurgeStepEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.util.Assert;

abstract class GenericAccountRepositoryImpl<U extends Account> extends AbstractRepositoryImpl<U> implements AccountRepository<U> {

	public GenericAccountRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public U findByLsUuid(String lsUuid) {
		Assert.notNull(lsUuid, "lsUuid must not be null");
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
	public U findActivateAndDestroyedByLsUuid(String lsUuid) {
		Assert.notNull(lsUuid, "lsUuid must not be null");
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("lsUuid", lsUuid).ignoreCase());
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
		Assert.notNull(domain, "domain must not be null");
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.uuid",domain));
		criteria.add(Restrictions.eq("destroyed", 0L));
		return findByCriteria(criteria);
	}
	
	@Override
	public U findByDomainAndMail(@Nonnull final String domainUuid, @Nonnull final String mail) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.uuid", domainUuid));
		criteria.add(Restrictions.eq("mail", mail).ignoreCase());
		criteria.add(Restrictions.eq("destroyed", 0L));
		List<U> accounts = findByCriteria(criteria);
		if (accounts == null || accounts.isEmpty()) {
			return null;
		} else if (accounts.size() == 1) {
			return accounts.get(0);
		} else {
			logger.error("Mail: {}  must be unique in domain {}", domainUuid, mail);
			throw new IllegalStateException("Mail must be unique in domain");
		}
	}

	@Override
	public U findByDomainAndExternalUid(@Nonnull final String domainUuid, @Nonnull final String externalUid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.uuid", domainUuid));
		criteria.add(Restrictions.eq("ldapUid", externalUid));
		criteria.add(Restrictions.eq("destroyed", 0L));
		List<U> accounts = findByCriteria(criteria);
		if (accounts == null || accounts.isEmpty()) {
			return null;
		} else if (accounts.size() == 1) {
			return accounts.get(0);
		} else {
			logger.error("ExternalUid: {}  must be unique in domain {}", domainUuid, externalUid);
			throw new IllegalStateException("ExternalUid must be unique in domain");
		}
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
		Assert.notNull(lsUuid, "lsUuid must not be null");
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
	public SystemAccount getAnonymousShareSystemAccount() {
		DetachedCriteria det = DetachedCriteria.forClass(SystemAccount.class)
				.add(Restrictions.eq("lsUuid", "system-anonymous-share-account"));
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
		Assert.notNull(lsUuid, "lsUuid must not be null");
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("lsUuid", lsUuid).ignoreCase());
		criteria.add(Restrictions.gt("destroyed", 0L));
		return DataAccessUtils.singleResult(findByCriteria(criteria));
	}

	@Override
	public U findAccountsReadyToPurge(String lsUuid) {
		Assert.notNull(lsUuid, "lsUuid must not be null");
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

	@Override
	public List<String> findAllNonAnonymizedPurgedAccounts(Date modificationDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("lsUuid"));
		criteria.add(Restrictions.lt("modificationDate", modificationDate));
		criteria.add(Restrictions.eq("purgeStep", AccountPurgeStepEnum.PURGED));
		criteria.add(Restrictions.ne("mail", GDPRConstants.MAIL_ANONYMIZATION));
		return listByCriteria(criteria);
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
					throws HibernateException {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT DISTINCT ls_uuid AS uuid FROM account AS a");
				sb.append(" LEFT JOIN quota AS q");
				sb.append(" ON q.account_id = a.id");
				sb.append(" WHERE destroyed = 0");
				sb.append(" AND q.account_id is null");
				sb.append(";");
				@SuppressWarnings("unchecked")
				final NativeQuery<String> query = session.createNativeQuery(sb.toString());
				List<String> res = query.list();
				return res;
			}
		};
		return getHibernateTemplate().execute(action);
	}

	@Override
	public List<String> findAllModeratorUuidsByGuest(Account guest) {
		HibernateCallback<List<String>> action = new HibernateCallback<>() {
			public List<String> doInHibernate(final Session session) throws HibernateException {
				String query = "SELECT a.lsUuid FROM Account a JOIN Moderator m ON (a.id = m.account) WHERE (m.guest = :guest)";
				@SuppressWarnings("unchecked")
				Query<String> getQuery = session.createQuery(query);
				getQuery.setParameter("guest", guest);
				List<String> accounts = getQuery.getResultList();
				return accounts;
			}
		};
		List<String> accounts = getHibernateTemplate().execute(action);
		return accounts;
	}
}
