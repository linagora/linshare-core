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

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.GuestRepository;
import org.springframework.dao.support.DataAccessUtils;
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

	@Override
	public void evict(Guest entity) {
		getHibernateTemplate().evict(entity);
	}

	/**
	 * Search some guests. If given agument is null, it's not considered.
	 * @param mail
	 *            user mail.
	 * @param firstName
	 *            user first name.
	 * @param lastName
	 *            user last name.
	 * @param ownerLogin
	 *            login of the user who creates the searched guest(s).
	 * 
	 * @return a list of matching users.
	 */
	public List<Guest> searchGuest(Account owner, String mail, String firstName, String lastName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		if (mail != null) {
			criteria.add(Restrictions.ilike("mail", mail, MatchMode.ANYWHERE));
		}
		if (firstName != null) {
			criteria.add(Restrictions.ilike("firstName", firstName, MatchMode.ANYWHERE));
		}
		if (lastName != null) {
			criteria.add(Restrictions.ilike("lastName", lastName, MatchMode.ANYWHERE));
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
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.lt("expirationDate", new Date()));
		criteria.add(Restrictions.eq("destroyed",0L));
		return findByCriteria(criteria);
	}

	@Override
	public List<String> findOutdatedGuestIdentifiers() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("lsUuid"));
		criteria.add(Restrictions.lt("expirationDate", new Date()));
		criteria.add(Restrictions.eq("destroyed", 0L));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	/**
	 * @see GuestRepository#searchGuestAnyWhere(String, String, String, String)
	 */
	public List<Guest> searchGuestAnyWhere(String mail, String firstName, String lastName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		Conjunction and = Restrictions.conjunction();
		criteria.add(and);
		if (mail != null) {
			and.add(Restrictions.ilike("mail", mail, MatchMode.ANYWHERE));
		}
		if (firstName != null) {
			and.add(Restrictions.ilike("firstName", firstName, MatchMode.ANYWHERE));
		}
		if (lastName != null) {
			and.add(Restrictions.ilike("lastName", lastName, MatchMode.ANYWHERE));
		}
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> searchGuestAnyWhere(String firstName, String lastName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));

		Conjunction and1 = Restrictions.conjunction();
		and1.add(Restrictions.ilike("firstName", lastName, MatchMode.ANYWHERE));
		and1.add(Restrictions.ilike("lastName", firstName, MatchMode.ANYWHERE));

		Conjunction and2 = Restrictions.conjunction();
		and2.add(Restrictions.ilike("firstName", firstName, MatchMode.ANYWHERE));
		and2.add(Restrictions.ilike("lastName", lastName, MatchMode.ANYWHERE));

		Disjunction or = Restrictions.disjunction();
		or.add(and1);
		or.add(and2);
		criteria.add(or);
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> searchGuestAnyWhere(String pattern) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		Disjunction or = Restrictions.disjunction();
		criteria.add(or);
		or.add(Restrictions.ilike("mail", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("firstName", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("lastName", pattern, MatchMode.ANYWHERE));
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

	@Override
	public List<Guest> search(List<AbstractDomain> domains, String mail, String firstName, String lastName, Account owner) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		if (mail != null) {
			criteria.add(Restrictions.ilike("mail", mail, MatchMode.ANYWHERE));
		}
		if (firstName != null) {
			criteria.add(Restrictions.ilike("firstName", firstName, MatchMode.ANYWHERE));
		}
		if (lastName != null) {
			criteria.add(Restrictions.ilike("lastName", lastName, MatchMode.ANYWHERE));
		}
		if (owner != null) {
			criteria.add(Restrictions.eq("owner", owner));
		}
		Disjunction or = Restrictions.disjunction();
		for (AbstractDomain domain : domains) {
			or.add(Restrictions.eq("domain", domain));
		}
		criteria.add(or);
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> search(List<AbstractDomain> domains, String pattern) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		Disjunction or = Restrictions.disjunction();
		criteria.add(or);
		or.add(Restrictions.ilike("mail", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("firstName", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("lastName", pattern, MatchMode.ANYWHERE));
		Disjunction or2 = Restrictions.disjunction();
		for (AbstractDomain domain : domains) {
			or2.add(Restrictions.eq("domain", domain));
		}
		criteria.add(or);
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> searchMyGuests(List<AbstractDomain> domains, String pattern, Account owner)
			throws BusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		Disjunction or = Restrictions.disjunction();
		criteria.add(or);
		or.add(Restrictions.ilike("mail", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("firstName", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("lastName", pattern, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("owner", owner));
		Disjunction or2 = Restrictions.disjunction();
		for (AbstractDomain domain : domains) {
			or2.add(Restrictions.eq("domain", domain));
		}
		criteria.add(or);
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> searchExceptGuests(List<AbstractDomain> domains, String pattern, Account owner)
			throws BusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		Disjunction or = Restrictions.disjunction();
		criteria.add(or);
		or.add(Restrictions.ilike("mail", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("firstName", pattern, MatchMode.ANYWHERE));
		or.add(Restrictions.ilike("lastName", pattern, MatchMode.ANYWHERE));
		criteria.add(Restrictions.ne("owner", owner));
		Disjunction or2 = Restrictions.disjunction();
		for (AbstractDomain domain : domains) {
			or2.add(Restrictions.eq("domain", domain));
		}
		criteria.add(or);
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> findAll(List<AbstractDomain> domains) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		Disjunction or = Restrictions.disjunction();
		for (AbstractDomain domain : domains) {
			or.add(Restrictions.eq("domain", domain));
		}
		criteria.add(or);
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> findAllMyGuests(Account owner) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		criteria.add(Restrictions.eq("owner", owner));
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> findAllOthersGuests(List<AbstractDomain> domains, Account owner) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		criteria.add(Restrictions.ne("owner", owner));
		Disjunction or = Restrictions.disjunction();
		for (AbstractDomain domain : domains) {
			or.add(Restrictions.eq("domain", domain));
		}
		criteria.add(or);
		return findByCriteria(criteria);
	}

	@Override
	public Guest findByDomainAndMail(AbstractDomain domain, String mail) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		criteria.add(Restrictions.eq("domain", domain));
		criteria.add(Restrictions.eq("mail", mail));
		return DataAccessUtils.singleResult(findByCriteria(criteria));
	}
}
