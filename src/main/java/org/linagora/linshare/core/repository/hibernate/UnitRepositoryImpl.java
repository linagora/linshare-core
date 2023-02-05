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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Unit;
import org.linagora.linshare.core.repository.UnitRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

@SuppressWarnings("rawtypes")
public class UnitRepositoryImpl extends AbstractRepositoryImpl<Unit> implements UnitRepository {

	public UnitRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Unit findById(long id) {
		List<Unit> unit= findByCriteria(Restrictions.eq("id", id));
		if (unit == null || unit.isEmpty()) {
			return null;
		} else if (unit.size() == 1) {
			return unit.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Unit entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Unit.class).add(Restrictions.eq("id",entity.getPersistenceId()));
		return det;
	}

	
}
