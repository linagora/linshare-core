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
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailFooterRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class MailFooterRepositoryImpl extends
		AbstractRepositoryImpl<MailFooter> implements MailFooterRepository {

	public MailFooterRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailFooter entry) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		det.add(Restrictions.eq("uuid", entry.getUuid()));
		return det;
	}

	/**
	 * Find a MailFooter using its uuid.
	 * 
	 * @param uuid
	 * @return found MailFooter (null if no MailFooter found).
	 */
	@Override
	public MailFooter findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public MailFooter update(MailFooter entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}
}
