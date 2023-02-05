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
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.linagora.linshare.core.domain.entities.LdapPattern;
import org.linagora.linshare.core.domain.entities.UserProvider;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class UserProviderRepositoryImpl extends AbstractRepositoryImpl<UserProvider> implements UserProviderRepository {

	public UserProviderRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public UserProvider create(UserProvider entity)
			throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public UserProvider update(UserProvider entity)
			throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public UserProvider findByUuid(String uuid) {
		UserProvider provider = DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
		Object unproxiedEntity = ((SessionImplementor) getCurrentSession())
				.getPersistenceContext()
				.unproxy(provider);
		return (UserProvider) unproxiedEntity;
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(UserProvider entity) {
		DetachedCriteria det = DetachedCriteria.forClass(UserProvider.class).add(
				Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public boolean isUsed(LdapPattern pattern) {
		DetachedCriteria det = DetachedCriteria
				.forClass(UserProvider.class);
		det.add(Restrictions.eq("pattern", pattern));
		det.setProjection(Projections.rowCount());
		long longResult = DataAccessUtils.longResult(findByCriteria(det));
		return longResult > 0;
	}
}
