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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.springframework.dao.support.DataAccessUtils;
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
		criteria.add(Restrictions.eq("destroyed", false));

		 List<U> users = findByCriteria(criteria);
	        if (users == null || users.isEmpty()) {
	            return null;
	        } else if (users.size() == 1) {
	            return users.get(0);
	        } else {
	            throw new IllegalStateException("lsUuid must be unique");
	        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<U> findByDomain(String domain) {
		Assert.notNull(domain);
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.identifier",domain));
		criteria.add(Restrictions.eq("destroyed", false));
		return getHibernateTemplate().findByCriteria(criteria);
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
		criteria.add(Restrictions.eq("destroyed", false));
		return findByCriteria(criteria);
	}

	@Override
	public boolean exist(String lsUuid) {
		Assert.notNull(lsUuid);
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("lsUuid", lsUuid));
		criteria.add(Restrictions.eq("destroyed", false));
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
	public U create(U entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setLsUuid(UUID.randomUUID().toString());
		return super.create(entity);
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
		entity.setDestroyed(true);
		this.update(entity);
	}

	@Override
	public List<U> findAllDestroyedAccounts() {
		return findByCriteria(Restrictions.eq("destroyed", true));
	}
}
