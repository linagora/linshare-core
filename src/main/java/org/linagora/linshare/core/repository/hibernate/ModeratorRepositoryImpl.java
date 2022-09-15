/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
	public void deleteAllModerators(Guest guest) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("guest", guest));
		List<Moderator> moderators = findByCriteria(det);
		moderators.forEach(moderator -> delete(moderator));
		logger.debug("{} Moderators deleted where guest is {}", moderators.size(), guest);
	}
}
