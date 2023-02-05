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
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.repository.RemoteServerRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class LdapConnectionRepositoryImpl extends RemoteServerRepository<LdapConnection> {

	public LdapConnectionRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria detachedCriteria() {
		return DetachedCriteria.forClass(LdapConnection.class);
	}

	@Override
	public boolean isUsed(LdapConnection ldapConnection) {
		DetachedCriteria det = DetachedCriteria
				.forClass(LdapUserProvider.class);
		det.add(Restrictions.eq("ldapConnection", ldapConnection));
		det.setProjection(Projections.rowCount());
		return DataAccessUtils.longResult(findByCriteria(det)) > 0;
	}
}
