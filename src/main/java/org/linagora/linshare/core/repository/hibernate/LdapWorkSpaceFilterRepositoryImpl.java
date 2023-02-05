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
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.LdapWorkSpaceFilterRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class LdapWorkSpaceFilterRepositoryImpl extends AbstractRepositoryImpl<LdapWorkSpaceFilter>
		implements LdapWorkSpaceFilterRepository {

	public LdapWorkSpaceFilterRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(LdapWorkSpaceFilter entity) {
		DetachedCriteria det = DetachedCriteria.forClass(LdapWorkSpaceFilter.class)
				.add(Restrictions.eq("uuid", entity.getUuid()));
		return det;
	}

	@Override
	public LdapWorkSpaceFilter create(LdapWorkSpaceFilter entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public LdapWorkSpaceFilter find(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq("uuid", uuid)));
	}

	@Override
	public List<LdapWorkSpaceFilter> findAllPublicGroupLdapPatterns() {
		return findByCriteria(Restrictions.eq("system", false));
	}

	@Override
	public List<LdapWorkSpaceFilter> findAllSystemGroupLdapPatterns() {
		return findByCriteria(Restrictions.eq("system", true));
	}

	@Override
	public LdapWorkSpaceFilter update(LdapWorkSpaceFilter entity)
			throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}
}
