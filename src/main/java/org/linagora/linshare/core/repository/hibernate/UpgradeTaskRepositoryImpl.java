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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.UpgradeTask;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UpgradeTaskRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class UpgradeTaskRepositoryImpl extends AbstractRepositoryImpl<UpgradeTask> implements UpgradeTaskRepository {

	public UpgradeTaskRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public List<UpgradeTask> findAll() {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.addOrder(Order.asc("taskOrder"));
		return findByCriteria(det);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(UpgradeTask entity) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("id",entity.getId()));
		return det;
	}

	@Override
	public UpgradeTask find(UpgradeTaskType identifier) throws BusinessException {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"identifier", identifier)));
	}

	@Override
	public List<UpgradeTask> findAllHidden(boolean hidden) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("hidden", hidden));
		det.addOrder(Order.asc("taskOrder"));
		return findByCriteria(det);
	}
}
