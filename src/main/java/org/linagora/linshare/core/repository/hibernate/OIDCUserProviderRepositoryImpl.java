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
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.OIDCUserProvider;
import org.linagora.linshare.core.repository.OIDCUserProviderRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class OIDCUserProviderRepositoryImpl extends AbstractRepositoryImpl<OIDCUserProvider>
		implements OIDCUserProviderRepository {

	public OIDCUserProviderRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Optional<OIDCUserProvider> findByDomainDiscriminator(String discriminator) {
		return Optional.ofNullable(
				DataAccessUtils.singleResult(findByCriteria(Restrictions.eq("domainDiscriminator", discriminator))));
	}

	@Override
	public List<OIDCUserProvider> findAllByDomainDiscriminator(List<String> discriminators) {
		DetachedCriteria det = DetachedCriteria.forClass(OIDCUserProvider.class);
		det.add(Restrictions.in("domainDiscriminator", discriminators));
		return findByCriteria(det);
	}

	@Override
	public boolean isDomainDiscriminatorAlreadyInUse(String discriminator, AbstractDomain domain) {
		DetachedCriteria det = DetachedCriteria.forClass(OIDCUserProvider.class);
		det.add(Restrictions.eq("domainDiscriminator", discriminator));
		det.add(Restrictions.ne("domain", domain));
		return DataAccessUtils.singleResult(findByCriteria(det)) != null;
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(OIDCUserProvider entity) {
		DetachedCriteria det = DetachedCriteria.forClass(OIDCUserProvider.class)
				.add(Restrictions.eq("id", entity.getId()));
		return det;
	}
}
