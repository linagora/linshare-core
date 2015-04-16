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

	@Override
	public List<AllowedContact> findByOwner(final User owner) {
		List<AllowedContact> contacts = findByCriteria(Restrictions.eq("owner", owner));
		return contacts;
	}
	
	// TODO
	// FIXME
	// XXX
	@SuppressWarnings("unchecked")
	@Override
	public List<AllowedContact> searchContact(final String mail, final String firstName, final String lastName, final Guest guest) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				String mailQ = mail==null?"":mail;
				String firstNameQ = firstName==null?"":firstName;
				String lastNameQ = lastName==null?"":lastName;
				
				String queryString = "select ac from AllowedContact ac join ac.contact as contact where ac.owner= :guest and LOWER(contact.mail) like :mail and LOWER(contact.firstName) like :firstName and LOWER(contact.lastName) like :lastName";

				Query query = session.createQuery(queryString);
			    query.setParameter("guest", guest);
			    query.setParameter("mail", '%'+mailQ.toLowerCase()+'%');
			    query.setParameter("firstName", '%'+firstNameQ.toLowerCase()+'%');
			    query.setParameter("lastName", '%'+lastNameQ.toLowerCase()+'%');
			    
				
				List<Query> queries = query.setCacheable(false).list();
				return queries;
			}
		});
	}

	@Override
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

	@Override
	public void purge(Guest guest) throws IllegalArgumentException, BusinessException {
		for(AllowedContact contact : this.findByOwner(guest)) {
			this.delete(contact);
		}
	}
}
