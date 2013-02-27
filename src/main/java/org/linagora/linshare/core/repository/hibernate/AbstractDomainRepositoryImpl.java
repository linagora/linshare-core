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
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class AbstractDomainRepositoryImpl extends AbstractRepositoryImpl<AbstractDomain> implements AbstractDomainRepository {

	public AbstractDomainRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(AbstractDomain entity) {
		DetachedCriteria det = DetachedCriteria.forClass(AbstractDomain.class).add(
				Restrictions.eq("identifier", entity.getIdentifier()));
		return det;
	}

	public AbstractDomain findById(String identifier) {
		List<AbstractDomain> abstractDomain = findByCriteria(Restrictions.eq("identifier", identifier));
		
		if (abstractDomain == null || abstractDomain.isEmpty()) {
			return null;
		} else if (abstractDomain.size() == 1) {
			return abstractDomain.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllDomainIdentifiers() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<String>>() {
					public List<String> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d.identifier from AbstractDomain d order by d.authShowOrder asc");
						return query.setCacheable(true).list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractDomain> findAllDomain() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<AbstractDomain>>() {
					public List<AbstractDomain> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from AbstractDomain d order by d.authShowOrder asc");
						return query.setCacheable(true).list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractDomain> findAllTopAndSubDomain() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<AbstractDomain>>() {
					public List<AbstractDomain> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from AbstractDomain d where TYPE = " + DomainType.TOPDOMAIN.toInt()
								+ " or TYPE = " + DomainType.SUBDOMAIN.toInt());
						return query.setCacheable(true).list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractDomain> findAllTopDomain() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<AbstractDomain>>() {
					public List<AbstractDomain> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from AbstractDomain d where TYPE = " + DomainType.TOPDOMAIN.toInt());
						return query.setCacheable(true).list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractDomain> findAllSubDomain() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<AbstractDomain>>() {
					public List<AbstractDomain> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d from AbstractDomain d where TYPE = " + DomainType.SUBDOMAIN.toInt());
						return query.setCacheable(true).list();
					}
				});
	}

	@Override
	public RootDomain getUniqueRootDomain() throws BusinessException {
		RootDomain domain = (RootDomain) this.findById(LinShareConstants.rootDomainIdentifier);
		if(domain == null) {
			throw new BusinessException(BusinessErrorCode.DATABASE_INCOHERENCE_NO_ROOT_DOMAIN,"No root domain found in the database.");
		}
		return domain;
	}

	
}
