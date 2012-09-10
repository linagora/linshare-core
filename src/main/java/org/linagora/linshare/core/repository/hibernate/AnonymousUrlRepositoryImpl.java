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

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class AnonymousUrlRepositoryImpl extends AbstractRepositoryImpl<AnonymousUrl> implements AnonymousUrlRepository {

	public AnonymousUrlRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(AnonymousUrl anonymousUrl) {
		DetachedCriteria det = DetachedCriteria.forClass(AnonymousUrl.class).add(Restrictions.eq("uuid", anonymousUrl.getUuid()));
		return det;
	}

	
	@Override
	public AnonymousUrl findByUuid(String uuid) {
		DetachedCriteria det = DetachedCriteria.forClass(AnonymousUrl.class).add(Restrictions.eq("uuid", uuid));
		List<AnonymousUrl> anonymousUrlList = findByCriteria(det);
		if (anonymousUrlList == null || anonymousUrlList.isEmpty()) {
			return null;
		} else if (anonymousUrlList.size() == 1) {
			return anonymousUrlList.get(0);
		} else {
			// This should not append
			throw new IllegalStateException("uuid must be unique");
		}
	}


	@Override
	public AnonymousUrl create(AnonymousUrl entity) throws BusinessException {
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}


	@Override
	public List<AnonymousUrl> getAllExpiredUrl() {
		
		HibernateCallback<List<AnonymousUrl>> action = new HibernateCallback<List<AnonymousUrl>>() {
			@SuppressWarnings("unchecked")
			public List<AnonymousUrl> doInHibernate(final Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery("SELECT a from AnonymousUrl as a where not exists " +
						"(SELECT b from AnonymousUrl as b , AnonymousShareEntry as entry where entry.anonymousUrl.id = a.id)");
				return 	query.list();
			}
		};
		
		return (List<AnonymousUrl>) getHibernateTemplate().execute(action);
	}

	
}
