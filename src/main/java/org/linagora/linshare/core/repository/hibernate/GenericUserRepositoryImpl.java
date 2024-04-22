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

import java.util.*;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.NotNull;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.view.tapestry.beans.AccountOccupationCriteriaBean;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;

abstract class GenericUserRepositoryImpl<U extends User> extends GenericAccountRepositoryImpl<U> implements UserRepository<U> {

	final private static int AUTO_COMPLETE_LIMIT = 20;

	public GenericUserRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public U create(U entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setLsUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	/* TODO : check call hierarchy to remove research by mail only. too dangerous. */
	@Override
	public U findByMail(String mail) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("mail", mail).ignoreCase());
		criteria.add(Restrictions.eq("destroyed", 0L));
		List<U> users = findByCriteria(criteria);

		if (users == null || users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			return users.get(0);
		} else {
			throw new IllegalStateException("Mail must be unique");
		}
	}

	@Override
	public U findByExternalUid(@NotNull String externalUid) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("ldapUid", externalUid));
		criteria.add(Restrictions.eq("destroyed", 0L));
		List<U> users = findByCriteria(criteria);

		if (users == null || users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			return users.get(0);
		} else {
			throw new IllegalStateException("Ldap uid must be unique");
		}
	}

	@Override
	public List<U> findByDomain(String domainId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.uuid",domainId));
		criteria.add(Restrictions.eq("destroyed", 0L));
		return findByCriteria(criteria);
	}

	@Override
	public List<U> findByCriteria(AccountOccupationCriteriaBean accountCriteria) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed",0L));
		if ((accountCriteria.getActorMails()!=null) && (accountCriteria.getActorMails().size()>0)) {
			criteria.add(Restrictions.in("mail", accountCriteria.getActorMails()));
		}
		if ((accountCriteria.getActorFirstname()!=null) && (accountCriteria.getActorFirstname().length()>0)) {
			criteria.add(Restrictions.like("firstName", accountCriteria.getActorFirstname(), MatchMode.START).ignoreCase());
		}
		if ((accountCriteria.getActorLastname()!=null) && (accountCriteria.getActorLastname().length()>0)) {
			criteria.add(Restrictions.like("lastName", accountCriteria.getActorLastname(), MatchMode.START).ignoreCase());
		}
		if ((accountCriteria.getActorDomain()!=null) && (accountCriteria.getActorDomain().length()>0)) {
			criteria.createAlias("domain", "domain");
			criteria.add(Restrictions.like("domain.uuid", accountCriteria.getActorDomain()).ignoreCase());
		}
		return findByCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findMails(final String beginWith) {
		DetachedCriteria crit = DetachedCriteria.forClass(User.class)
				.add(Restrictions.ilike("mail", beginWith, MatchMode.ANYWHERE))
				.add(Restrictions.eq("destroyed", 0L))
				.setProjection(Projections.property("mail"));

		return listByCriteria(crit);
	}


	@Override
	public Set<Long> findGuestWithModerators(Optional<Integer> greaterThan, Optional<Integer> lessThan, ModeratorRole role) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
		Root<Account> account = query.from(Account.class);
		Join moderators = account.join("moderators", JoinType.LEFT);
		query.multiselect(account.get("id"), cb.count(moderators.get("id")).alias("cnt"));
		if (role != null) {
			query.where(cb.equal(moderators.get("role"), role));
		}
		query.groupBy(account.get("id"));
		query.having(
				cb.and(
						cb.greaterThan(cb.count(moderators.get("id")), greaterThan.orElse(-1).longValue()),
						cb.lessThan(cb.count(moderators.get("id")), lessThan.orElse(Integer.MAX_VALUE).longValue())));
		List<Tuple> resultList = session.createQuery(query).getResultList();
		return resultList.stream().map(r -> (Long)r.get(0)).collect(Collectors.toSet());
	}

	@Override
	public PageContainer<U> findAll(List<AbstractDomain> domains, Order sortOrder, String mail, String firstName,
									String lastName, Boolean restricted, Boolean canCreateGuest, Boolean canUpload, Role role,
									AccountType type, Set<Long> subset, PageContainer<U> container) {
		DetachedCriteria detachedCrit = getAllCriteria(domains, mail, firstName, lastName, restricted, canCreateGuest,
				canUpload, role, type, subset);
		detachedCrit.addOrder(sortOrder);
		Long totalNumberElements = count(domains, mail, firstName, lastName, restricted, canCreateGuest, canUpload, role,
				type, subset);
		return findAll(detachedCrit, totalNumberElements, container);
	}

	private Long count(List<AbstractDomain> domains, String mail, String firstName, String lastName, Boolean restricted,
			Boolean canCreateGuest, Boolean canUpload, Role role, AccountType type, Set<Long> subset) {
		DetachedCriteria detachedCrit = getAllCriteria(domains, mail, firstName, lastName, restricted, canCreateGuest,
				canUpload, role, type, subset);
		detachedCrit.setProjection(Projections.rowCount());
		return (Long) detachedCrit.getExecutableCriteria(getCurrentSession()).uniqueResult();
	}

	private DetachedCriteria getAllCriteria(List<AbstractDomain> domains, String mail, String firstName,
			String lastName, Boolean restricted, Boolean canCreateGuest, Boolean canUpload, Role role, AccountType type,
											Set<Long> subset) {
		DetachedCriteria detachedCrit = DetachedCriteria.forClass(getPersistentClass());
		detachedCrit.createAlias("domain", "d");
		detachedCrit.add(Restrictions.eq("destroyed", 0L));
		detachedCrit.add(Restrictions.not(Restrictions.in("class", Lists.newArrayList(AccountType.ROOT.toInt(),
				AccountType.TECHNICAL_ACCOUNT.toInt()))));
		if (!domains.isEmpty()) {
			detachedCrit.add(Restrictions.in("domain", domains));
		}
		if (!Strings.isNullOrEmpty(mail)) {
			detachedCrit.add(Restrictions.ilike("mail", mail, MatchMode.ANYWHERE));
		}
		if (!Strings.isNullOrEmpty(firstName)) {
			detachedCrit.add(Restrictions.ilike("firstName", firstName, MatchMode.ANYWHERE));
		}
		if (!Strings.isNullOrEmpty(lastName)) {
			detachedCrit.add(Restrictions.ilike("lastName", lastName, MatchMode.ANYWHERE));
		}
		if (Objects.nonNull(restricted)) {
			detachedCrit.add(Restrictions.eq("restricted", restricted));
		}
		if (Objects.nonNull(canCreateGuest)) {
			detachedCrit.add(Restrictions.eq("canCreateGuest", canCreateGuest));
		}
		if (Objects.nonNull(canUpload)) {
			detachedCrit.add(Restrictions.eq("canUpload", canUpload));
		}
		if (Objects.nonNull(role)) {
			detachedCrit.add(Restrictions.eq("role", role));
		}
		if (Objects.nonNull(subset) && subset.size() > 0) {
			detachedCrit.add(Restrictions.in("id", subset));
		}
		if (Objects.nonNull(type)) {
			detachedCrit.add(Restrictions.in("class", type.toInt()));
		}
		return detachedCrit;
	}

	@Override
	public List<U> autoCompleteUser(List<AbstractDomain> domains, String mail) {
		DetachedCriteria detachedCrit = DetachedCriteria.forClass(getPersistentClass());
		detachedCrit.add(Restrictions.eq("destroyed", 0L));
		detachedCrit.add(Restrictions.not(Restrictions.in("class", Lists.newArrayList(AccountType.ROOT.toInt(),
				AccountType.TECHNICAL_ACCOUNT.toInt()))));
		if (Objects.nonNull(domains)) {
			detachedCrit.add(Restrictions.in("domain", domains));
		}
		detachedCrit.addOrder(Order.asc("mail"));
		detachedCrit.add(Restrictions.ilike("mail", mail, MatchMode.ANYWHERE));
		Criteria executableCriteria = detachedCrit.getExecutableCriteria(getCurrentSession());
		executableCriteria.setMaxResults(AUTO_COMPLETE_LIMIT);
		@SuppressWarnings("unchecked")
		List<U> list = listByCriteria(detachedCrit);
		return list;
	}

	@Override
	public List<U> autoCompleteUser(List<AbstractDomain> domains, String firstName, String lastName) {
		DetachedCriteria detachedCrit = DetachedCriteria.forClass(getPersistentClass());
		detachedCrit.add(Restrictions.eq("destroyed", 0L));
		detachedCrit.add(Restrictions.not(Restrictions.in("class", Lists.newArrayList(AccountType.ROOT.toInt(),
				AccountType.TECHNICAL_ACCOUNT.toInt()))));
		if (Objects.nonNull(domains)) {
			detachedCrit.add(Restrictions.in("domain", domains));
		}
		detachedCrit.addOrder(Order.asc("mail"));
		detachedCrit.add(
				Restrictions.or(
					Restrictions.and(
						Restrictions.ilike("firstName", firstName, MatchMode.ANYWHERE),
						Restrictions.ilike("lastName", lastName, MatchMode.ANYWHERE)
					),
					Restrictions.and(
						Restrictions.ilike("firstName", lastName, MatchMode.ANYWHERE),
						Restrictions.ilike("lastName", firstName, MatchMode.ANYWHERE)
					)
				)
		);
		Criteria executableCriteria = detachedCrit.getExecutableCriteria(getCurrentSession());
		executableCriteria.setMaxResults(AUTO_COMPLETE_LIMIT);
		@SuppressWarnings("unchecked")
		List<U> list = listByCriteria(detachedCrit);
		return list;
	}
}
