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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.repository.DomainAccessRuleRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DomainAccessRuleRepositoryImpl extends AbstractRepositoryImpl<DomainAccessRule> implements DomainAccessRuleRepository {

	public DomainAccessRuleRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public DomainAccessRule findById(long id) {
		List<DomainAccessRule> domainAccessRule = findByCriteria(Restrictions.eq("id", id));
		if (domainAccessRule == null || domainAccessRule.isEmpty()) {
			return null;
		} else if (domainAccessRule.size() == 1) {
			return domainAccessRule.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainAccessRule entity) {
		DetachedCriteria det = DetachedCriteria.forClass(DomainAccessRule.class);

		det.add( Restrictions.eq("id", entity.getPersistenceId()));
		return det;
	}

	@Override
	public List<DomainAccessRule> findByDomain(AbstractDomain domain) {
		return findByCriteria(Restrictions.eq("domain", domain));
	}

	@Override
	public long countNumberAccessRulesByDomain(AbstractDomain domain) {
		DetachedCriteria det = DetachedCriteria
				.forClass(DomainAccessRule.class);
		det.setProjection(Projections.rowCount());
		det.add(Restrictions.eq("domain", domain));
		return DataAccessUtils.longResult(findByCriteria(det));
	}
}