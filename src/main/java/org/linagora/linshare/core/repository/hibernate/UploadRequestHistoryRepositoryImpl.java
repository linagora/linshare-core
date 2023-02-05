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
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.UploadRequestHistory;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestHistoryRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class UploadRequestHistoryRepositoryImpl extends
		AbstractRepositoryImpl<UploadRequestHistory> implements
		UploadRequestHistoryRepository {

	public UploadRequestHistoryRepositoryImpl(
			HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(UploadRequestHistory hist) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass()).add(
				Restrictions.eq("uuid", hist.getUuid()));
		return det;
	}

	@Override
	public UploadRequestHistory findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public UploadRequestHistory create(UploadRequestHistory entity)
			throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public UploadRequestHistory update(UploadRequestHistory entity)
			throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllUuid() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass())
				.setProjection(Projections.property("uuid"))
				.addOrder(Order.asc("creationDate"));
		return listByCriteria(crit);
	}
}
