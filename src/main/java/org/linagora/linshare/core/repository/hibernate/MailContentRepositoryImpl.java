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

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailContentRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class MailContentRepositoryImpl extends
		AbstractRepositoryImpl<MailContent> implements MailContentRepository {

	public MailContentRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailContent entry) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("uuid", entry.getUuid()));
		return det;
	}

	@Override
	public MailContent findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public MailContent update(MailContent entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public List<MailContent> findAll(AbstractDomain domain, Language lang, MailContentType type) {
		Conjunction and = Restrictions.conjunction();
		and.add(Restrictions.eq("domain", domain));
		and.add(Restrictions.eq("mailContentType", type.toInt()));
		and.add(Restrictions.eq("language", lang.toInt()));
		return findByCriteria(and);
	}
}
