/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
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

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.GuestRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class GuestRepositoryImpl extends GenericUserRepositoryImpl<Guest> implements GuestRepository {

	public GuestRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Guest user) {
		DetachedCriteria det = DetachedCriteria.forClass(Guest.class).add(Restrictions.eq("lsUuid", user.getLsUuid()));
		return det;
	}

	/**
	 * Search some guests. If given agument is null, it's not considered.
	 * 
	 * @param mail
	 *            user mail.
	 * @param firstName
	 *            user first name.
	 * @param lastName
	 *            user last name.
	 * @param ownerLogin
	 *            login of the user who creates the searched guest(s).
	 * @return a list of matching users.
	 */
	public List<Guest> searchGuest(String mail, String firstName, String lastName, User owner) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Guest.class);
		criteria.add(Restrictions.eq("destroyed", false));
		if (mail != null) {
			criteria.add(Restrictions.like("mail", mail, MatchMode.START).ignoreCase());
		}
		if (firstName != null) {
			criteria.add(Restrictions.like("firstName", firstName, MatchMode.START).ignoreCase());
		}
		if (lastName != null) {
			criteria.add(Restrictions.like("lastName", lastName, MatchMode.START).ignoreCase());
		}
		if (owner != null) {
			criteria.add(Restrictions.eq("owner", owner));
		}
		return findByCriteria(criteria);
	}

	/**
	 * Find outdated guest accounts.
	 * 
	 * @return a list of outdated guests (null if no one found).
	 */
	public List<Guest> findOutdatedGuests() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Guest.class);
		criteria.add(Restrictions.lt("expirationDate", new Date()));
		criteria.add(Restrictions.eq("destroyed", false));
		return findByCriteria(criteria);
	}

	/**
	 * @see GuestRepository#searchGuestAnyWhere(String, String, String, String)
	 */
	public List<Guest> searchGuestAnyWhere(String mail, String firstName, String lastName) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Guest.class);
		criteria.add(Restrictions.eq("destroyed", false));
		if (mail != null) {
			criteria.add(Restrictions.like("mail", mail, MatchMode.ANYWHERE).ignoreCase());
		}
		if (firstName != null) {
			criteria.add(Restrictions.like("firstName", firstName, MatchMode.ANYWHERE).ignoreCase());
		}
		if (lastName != null) {
			criteria.add(Restrictions.like("lastName", lastName, MatchMode.ANYWHERE).ignoreCase());
		}
		return findByCriteria(criteria);
	}

	@Override
	public Guest findByLogin(String login) {
		try {
			return super.findByMail(login);
		} catch (IllegalStateException e) {
			logger.error("you are looking for account using login '"
					+ login
					+ "' but your login is not unique, same account logins in different domains.");;
			logger.debug("error: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public Guest findByLoginAndDomain(String domain, String login) {
		return super.findByMailAndDomain(domain, login);
	}
}
