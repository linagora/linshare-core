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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DomainAccessPolicyRepositoryImpl extends AbstractRepositoryImpl<DomainAccessPolicy> implements DomainAccessPolicyRepository {

	public DomainAccessPolicyRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public DomainAccessPolicy create(DomainAccessPolicy entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		return super.create(entity);
	}

	@Override
	public DomainAccessPolicy update(DomainAccessPolicy entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public DomainAccessPolicy findById(long id) {
		return DataAccessUtils.singleResult(findByCriteria(
				Restrictions.eq("id", id)));
		}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainAccessPolicy entity) {
		DetachedCriteria det = DetachedCriteria.forClass(DomainAccessPolicy.class).add(
				Restrictions.eq("id", entity.getPersistenceId()));
		return det;
	}
}
