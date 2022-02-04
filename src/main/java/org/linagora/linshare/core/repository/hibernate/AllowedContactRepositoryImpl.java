/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AllowedContactRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.base.Strings;

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

	@Override
	public List<AllowedContact> searchContact(final Guest owner, final String mail,
			final String firstName, final String lastName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("owner", owner));
		criteria.createAlias("contact", "c");
		Conjunction and = Restrictions.conjunction();
		criteria.add(and);
		if (mail != null) {
			and.add(Restrictions.ilike("c.mail", mail, MatchMode.ANYWHERE));
		}
		if (firstName != null) {
			and.add(Restrictions.ilike("c.firstName", firstName, MatchMode.ANYWHERE));
		}
		if (lastName != null) {
			and.add(Restrictions.ilike("c.lastName", lastName, MatchMode.ANYWHERE));
		}
		return findByCriteria(criteria);
	}

	@Override
	public List<AllowedContact> completeContact(Guest owner, String firstName,
			String lastName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("owner", owner));
		criteria.createAlias("contact", "c");

		Conjunction and1 = Restrictions.conjunction();
		and1.add(Restrictions.ilike("c.firstName", firstName, MatchMode.ANYWHERE));
		and1.add(Restrictions.ilike("c.lastName", lastName, MatchMode.ANYWHERE));

		Conjunction and2 = Restrictions.conjunction();
		and2.add(Restrictions.ilike("c.firstName", lastName, MatchMode.ANYWHERE));
		and2.add(Restrictions.ilike("c.lastName", firstName, MatchMode.ANYWHERE));

		Disjunction or = Restrictions.disjunction();
		or.add(and1);
		or.add(and2);
		criteria.add(or);
		return findByCriteria(criteria);
	}

	@Override
	public List<AllowedContact> completeContact(Guest owner, String pattern) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("owner", owner));
		criteria.createAlias("contact", "c");
		Disjunction or = Restrictions.disjunction();
		criteria.add(or);
		or.add(Restrictions.ilike("c.mail", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("c.firstName", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("c.lastName", pattern, MatchMode.ANYWHERE));
		return findByCriteria(criteria);
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

	@Override
	public List<AllowedContact> findAllRestrictedContacts(User user, String mail, String firstName,
			String lastName) {
		DetachedCriteria detachedCrit = DetachedCriteria.forClass(getPersistentClass());
		detachedCrit.add(Restrictions.eq("owner", user));
		detachedCrit.createAlias("contact", "c");
		if (!Strings.isNullOrEmpty(mail)) {
			detachedCrit.add(Restrictions.ilike("c.mail", mail, MatchMode.ANYWHERE));
		}
		if (!Strings.isNullOrEmpty(firstName)) {
			detachedCrit.add(Restrictions.ilike("c.firstName", firstName, MatchMode.ANYWHERE));
		}
		if (!Strings.isNullOrEmpty(lastName)) {
			detachedCrit.add(Restrictions.ilike("c.lastName", lastName, MatchMode.ANYWHERE));
		}
		return findByCriteria(detachedCrit);
	}

	@Override
	public AllowedContact findRestrictedContact(User owner, String restrictedContactUuid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("owner", owner));
		criteria.createAlias("contact", "c");
		criteria.add(Restrictions.eq("c.lsUuid", restrictedContactUuid));
		return DataAccessUtils.singleResult(findByCriteria(criteria));
	}
}
