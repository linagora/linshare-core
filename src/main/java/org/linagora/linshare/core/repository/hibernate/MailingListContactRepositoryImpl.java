/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
		det.add(Restrictions.eq("mailingList", list));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllContactMails(ContactList list) {
		DetachedCriteria det = DetachedCriteria.forClass(ContactListContact.class);
		det.add(Restrictions.eq("mailingList", list));
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
		det.add(Restrictions.eq("mailingList", list));
		return findByCriteria(det);
	}

	@Override
	public void updateEmail(final String currentEmail, final String newEmail)
			throws BusinessException {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException {
				final Query<?> query = session.createQuery(
						"UPDATE MailingListContact SET mail = :newEmail WHERE mail = :currentEmail");
				query.setParameter("newEmail", newEmail);
				query.setParameter("currentEmail", currentEmail);
				long updatedCounter = (long) query.executeUpdate();
				logger.info(updatedCounter + " MailingListContact have been updated.");
				return updatedCounter;
			}
		};
		getHibernateTemplate().execute(action);
	}
}
