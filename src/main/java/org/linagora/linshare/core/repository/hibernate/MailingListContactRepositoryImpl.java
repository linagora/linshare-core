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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListContactRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class MailingListContactRepositoryImpl extends
		AbstractRepositoryImpl<ContactListContact> implements
		MailingListContactRepository {

	public MailingListContactRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ContactListContact entity) {
		return DetachedCriteria.forClass(ContactListContact.class).add(
				Restrictions.eq("id", entity.getPersistenceId()));
	}

	@Override
	public ContactListContact findById(long id) {
		DetachedCriteria det = DetachedCriteria
				.forClass(ContactListContact.class);
		det.add(Restrictions.eq("id", id));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public ContactListContact findByUuid(String uuid) {
		DetachedCriteria det = DetachedCriteria
				.forClass(ContactListContact.class);
		det.add(Restrictions.eq("uuid", uuid));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public ContactListContact findByMail(ContactList list, String mail) {
		DetachedCriteria det = DetachedCriteria
				.forClass(ContactListContact.class);
		det.add(Restrictions.eq("mail", mail));
		det.add(Restrictions.eq("contactList", list));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllContactMails(ContactList list) {
		DetachedCriteria det = DetachedCriteria.forClass(ContactListContact.class);
		det.add(Restrictions.eq("contactList", list));
		det.setProjection(Projections.property("mail"));
		return listByCriteria(det);
	}

	@Override
	public ContactListContact update(ContactListContact entity)
			throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public ContactListContact create(ContactListContact entity)
			throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public List<ContactListContact> findAllContacts(ContactList list) {
		DetachedCriteria det = DetachedCriteria.forClass(ContactListContact.class);
		det.add(Restrictions.eq("contactList", list));
		return findByCriteria(det);
	}

	@Override
	public void updateEmail(final String currentEmail, final String newEmail)
			throws BusinessException {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException {
				final Query<?> query = session.createQuery(
						"UPDATE ContactListContact SET mail = :newEmail WHERE mail = :currentEmail");
				query.setParameter("newEmail", newEmail);
				query.setParameter("currentEmail", currentEmail);
				long updatedCounter = (long) query.executeUpdate();
				logger.info(updatedCounter + " ContactListContact have been updated.");
				return updatedCounter;
			}
		};
		getHibernateTemplate().execute(action);
	}
}
