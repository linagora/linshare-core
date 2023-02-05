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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;
import org.linagora.linshare.core.domain.entities.WorkSpaceProvider;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceProvider;
import org.linagora.linshare.core.repository.WorkSpaceProviderRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class WorkSpaceProviderRepositoryImpl extends AbstractRepositoryImpl<WorkSpaceProvider>
		implements WorkSpaceProviderRepository {

	public WorkSpaceProviderRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(WorkSpaceProvider entity) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkSpaceProvider.class)
				.add(Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public boolean isUsed(LdapWorkSpaceFilter filter) {
		DetachedCriteria det = DetachedCriteria.forClass(LdapWorkSpaceProvider.class);
		det.add(Restrictions.eq("workSpaceFilter", filter));
		det.setProjection(Projections.rowCount());
		long longResult = DataAccessUtils.longResult(findByCriteria(det));
		return longResult > 0;
	}
}
