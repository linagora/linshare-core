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
import java.util.Calendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.SecuredUrlRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class SecuredUrlRepositoryImpl extends
		AbstractRepositoryImpl<SecuredUrl> implements SecuredUrlRepository {

	public SecuredUrlRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(SecuredUrl securedUrl) {
		DetachedCriteria det = DetachedCriteria.forClass(SecuredUrl.class).add(
				Restrictions.eq("urlPath", securedUrl.getUrlPath())).add(
				Restrictions.eq("alea", securedUrl.getAlea()));
		return det;
	}

	public SecuredUrl find(String shareId, String url) throws LinShareNotSuchElementException {
		DetachedCriteria det = DetachedCriteria.forClass(SecuredUrl.class).add(
				Restrictions.eq("alea", shareId)).add(
				Restrictions.eq("urlPath", url));
		List<SecuredUrl> securedUrlList = findByCriteria(det);
		if (securedUrlList == null || securedUrlList.isEmpty()) {
           throw new LinShareNotSuchElementException("Secured url not found");
		} else if (securedUrlList.size() == 1) {
			return securedUrlList.get(0);
		} else {
			// This should not append
			throw new IllegalStateException("urlPath and alea must be unique");
		}
	}
	
	public List<SecuredUrl> findBySender(User sender) {
		DetachedCriteria det = DetachedCriteria.forClass(SecuredUrl.class).add(
				Restrictions.eq("sender", sender));
		return findByCriteria(det);
	}

	public List<SecuredUrl> getOutdatedSecuredUrl() {
		return findByCriteria(Restrictions.lt("expirationTime", Calendar
				.getInstance()));
	}

	public List<SecuredUrl> getSecureUrlLinkedToDocument(final Document doc) throws LinShareNotSuchElementException {
		
		return (List<SecuredUrl>)  getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				
				String queryString = "select s from SecuredUrl s join s.documents as docs where docs.identifier= :docid";
				Query query = session.createQuery(queryString);
				
			    query.setParameter("docid", doc.getUuid());
				
				return query.setCacheable(false).list();
			}
		});
		
	}
	
	public List<SecuredUrl> getUpcomingOutdatedSecuredUrl(Integer date) {
    	Calendar calMin = Calendar.getInstance();
    	calMin.add(Calendar.DAY_OF_MONTH, date);
    	
    	Calendar calMax = Calendar.getInstance();
    	calMax.add(Calendar.DAY_OF_MONTH, date+1);
    	
        return findByCriteria(Restrictions.lt("expirationTime", calMax), Restrictions.gt("expirationTime", calMin));
	}

}
