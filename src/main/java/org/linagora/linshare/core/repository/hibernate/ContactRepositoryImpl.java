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

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.repository.ContactRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class ContactRepositoryImpl extends AbstractRepositoryImpl<Contact> implements ContactRepository {

	public ContactRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Contact findByMail(String mail) {
		List<Contact> users = findByCriteria(Restrictions.eq("mail", mail).ignoreCase());
        if (users == null || users.isEmpty()) {
            return null;
        } else if (users.size() == 1) {
            return users.get(0);
        } else {
            throw new IllegalStateException("Mail must be unique");
        }
	}
	
	

	@Override
	public Contact find(Contact contact) {
		String mail = contact.getMail();
		if(mail == null) {
			 throw new IllegalStateException("Mail must be set");
		}
		return findByMail(mail);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Contact entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Contact.class).add(Restrictions.eq("mail", entity.getMail()));
		return det;
	}

}
