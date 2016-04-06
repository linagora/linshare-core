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

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.InternalRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class InternalRepositoryImpl extends GenericUserRepositoryImpl<Internal>
		implements InternalRepository {

	private final boolean multidomain;

	public InternalRepositoryImpl(HibernateTemplate hibernateTemplate,
			boolean multidomain) {
		super(hibernateTemplate);
		this.multidomain = multidomain;
	}

	@Override
	public Internal findByLogin(String login) {
		Internal u = null;
		try {
			u = super.findByMail(login);
		} catch (IllegalStateException e) {
			logger.error("you are looking for an account using mail as login : '"
					+ login
					+ "' but your login is not unique, same account logins in different domains.");
			logger.debug("error: " + e.getMessage());
			throw e;
		}

		if (u == null && multidomain) {
			try {
				u = findByLdapUid(login);
			} catch (IllegalStateException e) {
				logger.error("you are looking for an account using LDAP uid as login : '"
						+ login
						+ "' but your login is not unique, same account logins in different domains.");
				throw e;
			}
		}
		return u;
	}

	private Internal findByLdapUid(String ldapUid) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("ldapUid", ldapUid).ignoreCase());
		criteria.add(Restrictions.eq("destroyed", 0L));
		List<Internal> users = findByCriteria(criteria);

		if (users == null || users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			return users.get(0);
		} else {
			throw new IllegalStateException("Ldap uid must be unique");
		}
	}

	@Override
	public Internal findByLoginAndDomain(String domain, String login) {
		Internal u = super.findByMailAndDomain(domain, login);
		if (u == null && multidomain) {
			u = findByDomainAndLdapUid(domain, login);
		}
		return u;
	}

	@Override
	public List<Internal> findAllInconsistent() throws BusinessException {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("inconsistent", true));
		criteria.add(Restrictions.eq("destroyed", 0L));
		return findByCriteria(criteria);
	}

	@Override
	public List<String> findAllUsersUuid() throws BusinessException {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("lsUuid"));
		criteria.add(Restrictions.eq("destroyed", 0L));
		criteria.add(Restrictions.eq("inconsistent", false));
		@SuppressWarnings("unchecked")
		List<String> uuids = listByCriteria(criteria);
		return uuids;
	}

	@Override
	public List<String> findAllInconsistentsUuid() throws BusinessException {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("lsUuid"));
		criteria.add(Restrictions.eq("inconsistent", true));
		criteria.add(Restrictions.eq("destroyed", 0L));
		@SuppressWarnings("unchecked")
		List<String> uuids = listByCriteria(criteria);
		return uuids;
	}

	private Internal findByDomainAndLdapUid(String domain, String ldapUid) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.identifier", domain));
		criteria.add(Restrictions.eq("ldapUid", ldapUid).ignoreCase());
		criteria.add(Restrictions.eq("destroyed", 0L));
		List<Internal> users = findByCriteria(criteria);

		if (users == null || users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			return users.get(0);
		} else {
			throw new IllegalStateException("Ldap uid must be unique");
		}
	}
}
