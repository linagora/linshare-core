/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.AllowedMimeType;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.AllowedMimeTypeRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class AllowedMimeTypeRepositoryImpl extends AbstractRepositoryImpl<AllowedMimeType> implements AllowedMimeTypeRepository{

	public AllowedMimeTypeRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(AllowedMimeType entity) {
		DetachedCriteria det = DetachedCriteria.forClass( AllowedMimeType.class )
		.add(Restrictions.eq( "id", entity.getId() ) );
		return det;
	}

	public List<AllowedMimeType> findByMimeType(String mimetype) {
        List<AllowedMimeType> mimes = findByCriteria(Restrictions.eq("mimetype", mimetype));
        return mimes;
	}

	public void saveOrUpdateMimeType(List<AllowedMimeType> list)
			throws BusinessException {
		for (AllowedMimeType allowedMimeType : list) {
			getHibernateTemplate().saveOrUpdate(allowedMimeType);
		}
	}
} 
