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
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
import org.linagora.linshare.core.upgrade.v4_0.NotifyAllAnonymousWithNewPasswordUpgradeTaskImpl;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class AnonymousUrlRepositoryImpl extends AbstractRepositoryImpl<AnonymousUrl> implements AnonymousUrlRepository {

	public AnonymousUrlRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(AnonymousUrl anonymousUrl) {
		DetachedCriteria det = DetachedCriteria.forClass(AnonymousUrl.class).add(Restrictions.eq("uuid", anonymousUrl.getUuid()));
		return det;
	}

	@Override
	public AnonymousUrl findByUuid(String uuid) {
		DetachedCriteria det = DetachedCriteria.forClass(AnonymousUrl.class).add(Restrictions.eq("uuid", uuid));
		List<AnonymousUrl> anonymousUrlList = findByCriteria(det);
		if (anonymousUrlList == null || anonymousUrlList.isEmpty()) {
			return null;
		} else if (anonymousUrlList.size() == 1) {
			return anonymousUrlList.get(0);
		} else {
			// This should not append
			throw new IllegalStateException("uuid must be unique");
		}
	}

	@Override
	public AnonymousUrl create(AnonymousUrl entity) throws BusinessException {
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public List<String> findAllExpiredEntries() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("uuid"));
		criteria.createAlias("anonymousShareEntries", "ase", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.isNull("ase.anonymousUrl"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}
	
	/**
	 * Method used only for upgrade task {@link NotifyAllAnonymousWithNewPasswordUpgradeTaskImpl}
	 */
	@Deprecated(forRemoval = true, since = "4.0")
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllMyAnonymousUuids() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.not(Restrictions.ilike("password", "{bcrypt}", MatchMode.START)))
				.setProjection(Projections.property("uuid"));
		return listByCriteria(crit);
	}
}
