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
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailConfigRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class MailConfigRepositoryImpl extends
		AbstractRepositoryImpl<MailConfig> implements MailConfigRepository {

	public MailConfigRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailConfig entry) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("uuid", entry.getUuid()));
		return det;
	}

	/**
	 * Find a MailConfig using its uuid.
	 * 
	 * @param uuid
	 * @return found MailConfig (null if no MailConfig found).
	 */
	@Override
	public MailConfig findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public MailConfig create(MailConfig entity) throws BusinessException {
		entity.setUuid(UUID.randomUUID().toString());
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		return super.create(entity);
	}

	@Override
	public MailConfig update(MailConfig entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public boolean isMailLayoutReferenced(MailLayout layout) {
		return !findByCriteria(Restrictions.eq("mailLayoutHtml", layout))
				.isEmpty();
	}
}
