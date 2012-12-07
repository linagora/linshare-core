/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
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
import org.springframework.orm.hibernate3.HibernateTemplate;

abstract class GenericAccountRepositoryImpl<U extends Account> extends AbstractRepositoryImpl<U> implements AccountRepository<U> {

	public GenericAccountRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public U findByLsUuid(String lsUuid) {
		 List<U> users = findByCriteria(Restrictions.eq("lsUuid", lsUuid).ignoreCase());
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
		
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.like("domain.identifier",domain));
		return getHibernateTemplate().findByCriteria(criteria);
	}
	

	@Override
	public boolean exist(String lsUuid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("lsUuid", lsUuid));
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
	public SystemAccount getSystemAccount() {
		DetachedCriteria det = DetachedCriteria.forClass( SystemAccount.class ).add(Restrictions.eq( "lsUuid", "system" ) );
		
		List<U> users = findByCriteria(det);
        if (users == null || users.isEmpty()) {
            return null;
        } else if (users.size() == 1) {
            return (SystemAccount) users.get(0);
        } else {
            throw new IllegalStateException("lsUuid must be unique");
        }
	}
}
