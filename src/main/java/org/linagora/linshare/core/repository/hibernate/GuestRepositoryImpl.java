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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.repository.GuestRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

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

	@Override
	public List<String> findAllGuests() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("mail"));
		criteria.add(Restrictions.eq("destroyed", 0L));
		final List<String> list = this.listByCriteria(criteria);
		return list;
	}

	@Override
	public List<String> findGuestsAboutToExpire(int nbDaysBeforeExpiration) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("lsUuid"));
		Calendar dateBeforeExpiration = Calendar.getInstance();
		dateBeforeExpiration.add(Calendar.DATE, nbDaysBeforeExpiration);
		dateBeforeExpiration.set(Calendar.HOUR_OF_DAY, 0);
		dateBeforeExpiration.set(Calendar.MINUTE, 0);
		dateBeforeExpiration.set(Calendar.SECOND, 0);
		dateBeforeExpiration.set(Calendar.MILLISECOND, 0);
		Calendar nextDateBeforeExpiration = Calendar.getInstance();
		nextDateBeforeExpiration.set(Calendar.HOUR_OF_DAY, 0);
		nextDateBeforeExpiration.set(Calendar.MINUTE, 0);
		nextDateBeforeExpiration.set(Calendar.SECOND, 0);
		nextDateBeforeExpiration.set(Calendar.MILLISECOND, 0);
		nextDateBeforeExpiration.add(Calendar.DATE, nbDaysBeforeExpiration + 1);
		criteria.add(Restrictions.between("expirationDate", dateBeforeExpiration.getTime(), nextDateBeforeExpiration.getTime()));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	/**
	 * @see GuestRepository#searchGuestAnyWhere(String, String, String)
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

	/**
	 * Find all guests mails, those passwords are encoded in not bcrypt
	 * @return {@link List} mails of guests
	 */
	@Deprecated(forRemoval = true, since = "4.0")
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllWithDeprecatedPasswordEncoding() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.not(Restrictions.ilike("password", "{bcrypt}", MatchMode.START)))
				.add(Restrictions.eq("destroyed", 0L))
				.setProjection(Projections.property("lsUuid"));
		return listByCriteria(crit);
	}

	@Override
	public Guest findByDomainAndMail(AbstractDomain domain, String mail) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		criteria.add(Restrictions.eq("domain", domain));
		criteria.add(Restrictions.eq("mail", mail));
		return DataAccessUtils.singleResult(findByCriteria(criteria));
	}

	@Override
	public Guest findByMail(final String mail) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		criteria.add(Restrictions.eq("mail", mail));
		return DataAccessUtils.singleResult(this.findByCriteria(criteria));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllGuestsUuids() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("lsUuid"));
		List<String> uuids = listByCriteria(criteria);
		return uuids;
	}

	@Override
	public List<Guest> findAll(Account moderator, Optional<ModeratorRole> moderatorRole, Optional<String> pattern) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Moderator.class);
		criteria.setProjection(Projections.property("guest"));
		criteria.setFetchMode("guest", FetchMode.JOIN);
		criteria.createAlias("guest", "guest");
		criteria.add(Restrictions.eq("guest.destroyed", 0L));

		criteria.setFetchMode("account", FetchMode.JOIN);
		criteria.createAlias("account", "account");
		criteria.add(Restrictions.eq("account", moderator));
		if (moderatorRole.isPresent()) {
			criteria.add(Restrictions.eq("role", moderatorRole.get()));
		}
		if (pattern.isPresent()) {
			Disjunction or = Restrictions.disjunction();
			or.add(Restrictions.ilike("guest.mail", pattern.get(), MatchMode.ANYWHERE));
			or.add(Restrictions.ilike("guest.firstName", pattern.get(), MatchMode.ANYWHERE));
			or.add(Restrictions.ilike("guest.lastName", pattern.get(), MatchMode.ANYWHERE));
			criteria.add(or);
		}
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> findAll(List<AbstractDomain> domains, Account moderator,  Optional<ModeratorRole> moderatorRole,
			Optional<String> pattern) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Moderator.class);
		criteria.setProjection(Projections.distinct(Projections.property("guest")));
		criteria.setFetchMode("guest", FetchMode.JOIN);
		criteria.createAlias("guest", "guest");
		criteria.add(Restrictions.eq("guest.destroyed", 0L));

		criteria.setFetchMode("account", FetchMode.JOIN);
		criteria.createAlias("account", "account");
		criteria.add(Restrictions.eq("account", moderator));

		Disjunction orDomain = Restrictions.disjunction();
		for (AbstractDomain domain : domains) {
			orDomain.add(Restrictions.eq("guest.domain", domain));
		}
		criteria.add(orDomain);
		if (moderatorRole.isPresent()) {
			criteria.add(Restrictions.eq("role", moderatorRole.get()));
		}
		if (pattern.isPresent()) {
			Disjunction or = Restrictions.disjunction();
			or.add(Restrictions.ilike("guest.mail", pattern.get(), MatchMode.ANYWHERE));
			or.add(Restrictions.ilike("guest.firstName", pattern.get(), MatchMode.ANYWHERE));
			or.add(Restrictions.ilike("guest.lastName", pattern.get(), MatchMode.ANYWHERE));
			criteria.add(or);
		}
		return findByCriteria(criteria);
	}

	@Override
	public List<Guest> findAll(List<AbstractDomain> domains, Optional<String> pattern) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed", 0L));
		Disjunction or = Restrictions.disjunction();
		for (AbstractDomain domain : domains) {
			or.add(Restrictions.eq("domain", domain));
		}
		criteria.add(or);
		if (pattern.isPresent()) {
			Disjunction orPattern = Restrictions.disjunction();
			orPattern.add(Restrictions.ilike("mail", pattern.get(), MatchMode.ANYWHERE));
			orPattern.add(Restrictions.ilike("firstName", pattern.get(), MatchMode.ANYWHERE));
			orPattern.add(Restrictions.ilike("lastName", pattern.get(), MatchMode.ANYWHERE));
			criteria.add(orPattern);
		}
		return findByCriteria(criteria);
	}
}
