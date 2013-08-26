/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainPatternRepositoryImpl extends
		AbstractRepositoryImpl<DomainPattern> implements
		DomainPatternRepository {

	public DomainPatternRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainPattern entity) {
		DetachedCriteria det = DetachedCriteria.forClass(DomainPattern.class)
				.add(Restrictions.eq("identifier", entity.getIdentifier()));
		return det;
	}

	@Override
	public DomainPattern findById(String identifier) {
		List<DomainPattern> patterns = findByCriteria(Restrictions.eq(
				"identifier", identifier));

		if (patterns == null || patterns.isEmpty()) {
			return null;
		} else if (patterns.size() == 1) {
			return patterns.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DomainPattern> findAllSystemDomainPattern() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session
						.createQuery("select d from DomainPattern d where d.system = true");
				return query.setCacheable(true).list();

			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DomainPattern> findAllUserDomainPattern() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session
						.createQuery("select d from DomainPattern d where d.system = false");
				return query.setCacheable(true).list();

			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DomainPattern> findAllDomainPattern() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session
						.createQuery("select d from DomainPattern d");
				return query.setCacheable(true).list();

			}
		});
	}

}
