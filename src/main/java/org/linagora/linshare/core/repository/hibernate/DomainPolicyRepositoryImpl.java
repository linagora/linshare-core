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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DomainPolicyRepositoryImpl extends AbstractRepositoryImpl<DomainPolicy> implements DomainPolicyRepository {

	public DomainPolicyRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public DomainPolicy create(DomainPolicy policy){
		if (policy != null){
			policy.setCreationDate(new Date());
			policy.setModificationDate(new Date());
		}
		return super.create(policy);
	}

	@Override
	public DomainPolicy update(DomainPolicy policy){
		if (policy != null){
			policy.setModificationDate(new Date());
		}
		return super.update(policy);
	}

	@Override
	public DomainPolicy findById(String identifier) {
		return DataAccessUtils.singleResult(findByCriteria(
				Restrictions.eq("uuid", identifier)));
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainPolicy entity) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("uuid", entity.getUuid()));

		return det;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllIdentifiers() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass())
				.setProjection(Projections.property("uuid"));

		return listByCriteria(crit);
	}
}
