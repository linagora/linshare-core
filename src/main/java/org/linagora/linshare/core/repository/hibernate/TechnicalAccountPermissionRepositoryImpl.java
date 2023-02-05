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
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.TechnicalAccountPermissionRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class TechnicalAccountPermissionRepositoryImpl extends
		AbstractRepositoryImpl<TechnicalAccountPermission> implements
		TechnicalAccountPermissionRepository {

	public TechnicalAccountPermissionRepositoryImpl(
			HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public TechnicalAccountPermission find(String uuid) {
		List<TechnicalAccountPermission> entries = findByCriteria(Restrictions
				.eq("uuid", uuid));
		if (entries == null || entries.isEmpty()) {
			return null;
		} else if (entries.size() == 1) {
			return entries.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	public boolean exist(String uuid) {
		if (find(uuid) != null) {
			return true;
		}
		return false;
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(
			TechnicalAccountPermission entity) {
		DetachedCriteria det = DetachedCriteria.forClass(
				TechnicalAccountPermission.class).add(
				Restrictions.eq("uuid", entity.getUuid()));
		return det;
	}

	@Override
	public TechnicalAccountPermission create(TechnicalAccountPermission entity)
			throws BusinessException, IllegalArgumentException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public TechnicalAccountPermission update(TechnicalAccountPermission entity)
			throws BusinessException, IllegalArgumentException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}
}
