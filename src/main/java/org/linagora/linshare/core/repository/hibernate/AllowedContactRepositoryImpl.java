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
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AllowedContactRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class AllowedContactRepositoryImpl extends AbstractRepositoryImpl<AllowedContact>
		implements AllowedContactRepository {

	public AllowedContactRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	protected DetachedCriteria getNaturalKeyCriteria(AllowedContact entity) {
		DetachedCriteria det = DetachedCriteria.forClass(AllowedContact.class).add(
				Restrictions.eq("owner", entity.getOwner())).add(
				Restrictions.eq("contact", entity.getContact()));
		return det;
	}

	public List<AllowedContact> findByOwner(final User owner) {
		List<AllowedContact> contacts = findByCriteria(Restrictions.eq("owner", owner));
		return contacts;
	}
	
	@SuppressWarnings("unchecked")
	public List<AllowedContact> searchContact(final String mail, final String firstName, final String lastName, final Guest guest) {
		
		return (List<AllowedContact>)  getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				String mailQ = mail==null?"":mail;
				String firstNameQ = firstName==null?"":firstName;
				String lastNameQ = lastName==null?"":lastName;
				
				String queryString = "select ac from AllowedContact ac join ac.contact as contact where ac.owner= :guest and LOWER(contact.login) like :login and LOWER(contact.firstName) like :firstName and LOWER(contact.lastName) like :lastName";

				Query query = session.createQuery(queryString);
			    query.setParameter("guest", guest);
			    query.setParameter("login", '%'+mailQ.toLowerCase()+'%');
			    query.setParameter("firstName", '%'+firstNameQ.toLowerCase()+'%');
			    query.setParameter("lastName", '%'+lastNameQ.toLowerCase()+'%');
			    
				
				List<Query> queries = query.setCacheable(false).list();
				return queries;
			}
		});
	}

	public void deleteAllByUserBothSides(final User user) {
		List<AllowedContact> results = new ArrayList<AllowedContact>();
		results.addAll(findByCriteria(Restrictions.eq("owner", user)));
		results.addAll(findByCriteria(Restrictions.eq("contact", user)));
		for (AllowedContact allowedContact : results) {
			try {
				delete(allowedContact);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
	}
}
