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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.repository.ModeratorRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class ModeratorRepositoryImpl extends AbstractRepositoryImpl<Moderator> implements ModeratorRepository {

	public ModeratorRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Moderator entity) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("uuid", entity.getUuid()));
		return det;
	}

	@Override
	public Moderator findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq("uuid", uuid)));
	}

	@Override
	public List<Moderator> findAllByGuest(Guest guest, ModeratorRole role, String pattern) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("guest", guest));
		if(Objects.nonNull(role)) {
			det.add(Restrictions.eq("role", role));
		}
		if(!Strings.isNullOrEmpty(pattern)) {
			det.createAlias("account", "a");
			Disjunction or = Restrictions.disjunction();
			det.add(or);
			or.add(Restrictions.ilike("a.mail", pattern, MatchMode.ANYWHERE));
			or.add(Restrictions.ilike("a.firstName", pattern, MatchMode.ANYWHERE));
			or.add(Restrictions.ilike("a.lastName", pattern, MatchMode.ANYWHERE));
		}
		List<Moderator> moderators = findByCriteria(det);
		return moderators;
	}

	@Override
	public Optional<Moderator> findByGuestAndAccount(Account actor, Guest guest) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("account", actor));
		det.add(Restrictions.eq("guest", guest));
		Moderator moderator = DataAccessUtils.singleResult(findByCriteria(det));
		return Optional.ofNullable(moderator);
	}

	@Override
	public Moderator findModeratorByGuestAndAccount(@Nonnull final Account actor, @Nonnull final Guest guest) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("account", actor));
		det.add(Restrictions.eq("guest", guest));
		Moderator moderator = DataAccessUtils.singleResult(findByCriteria(det));
		return moderator;
	}

	@Override
	public void deleteAllModerators(Guest guest) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("guest", guest));
		List<Moderator> moderators = findByCriteria(det);
		moderators.forEach(moderator -> delete(moderator));
		logger.debug("{} Moderators deleted where guest is {}", moderators.size(), guest);
	}
}
