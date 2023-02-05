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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class FunctionalityRepositoryImpl extends AbstractRepositoryImpl<Functionality> implements FunctionalityRepository {

	public FunctionalityRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Functionality create(Functionality entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		return super.create(entity);
	}

	@Override
	public Functionality update(Functionality entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public Functionality findById(long id) {
		return DataAccessUtils.singleResult(findByCriteria(
				Restrictions.eq("id", id)));
	}

	@Override
	public Functionality findByDomain(AbstractDomain domain, String identifier) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("domain", domain));
		det.add(Restrictions.eq("identifier", identifier));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public Set<Functionality> findAll(AbstractDomain domain) {
		List<Functionality> fonc = findByCriteria(Restrictions.eq("domain", domain));
		Set<Functionality> ret= new HashSet<Functionality>();
		if (fonc != null) {
			 ret.addAll(fonc);
		}
		return ret;
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Functionality entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Functionality.class).add(
				Restrictions.eq("id", entity.getId()));
		return det;
	}
}
