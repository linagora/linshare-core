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
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.repository.SignatureRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class SignatureRepositoryImpl  extends AbstractRepositoryImpl<Signature> implements SignatureRepository {


	public SignatureRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Signature sig) {
		DetachedCriteria det = DetachedCriteria.forClass(Signature.class).add(Restrictions.eq( "uuid", sig.getUuid() ) );
		return det;
	}
	
	
	@Override
    public Signature findByUuid(String uuid) {
        List<Signature> sigs = findByCriteria(Restrictions.eq("uuid", uuid));
        if (sigs == null || sigs.isEmpty()) {
            return null;
        } else if (sigs.size() == 1) {
            return sigs.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }
}
