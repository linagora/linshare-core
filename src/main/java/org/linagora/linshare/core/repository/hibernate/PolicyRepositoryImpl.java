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
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.repository.PolicyRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class PolicyRepositoryImpl extends AbstractRepositoryImpl<Policy> implements PolicyRepository {

	
	public PolicyRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Policy entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Policy.class).add(Restrictions.eq("id",entity.getId()));
		return det;
	}

	@Override
	public Policy findById(long id) {
		List<Policy> policy= findByCriteria(Restrictions.eq("id", id));
		if (policy == null || policy.isEmpty()) {
			return null;
		} else if (policy.size() == 1) {
			return policy.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}
}
