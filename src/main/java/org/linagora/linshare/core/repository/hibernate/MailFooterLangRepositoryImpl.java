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
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.repository.MailFooterLangRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class MailFooterLangRepositoryImpl extends
		AbstractRepositoryImpl<MailFooterLang> implements
		MailFooterLangRepository {

	public MailFooterLangRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailFooterLang entity) {
		return DetachedCriteria.forClass(getPersistentClass()).add(
				Restrictions.eq("id", entity.getId()));
	}

	@Override
	public MailFooterLang findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public boolean isMailFooterReferenced(MailFooter footer) {
		return !findByCriteria(Restrictions.eq("mailFooter", footer)).isEmpty();
	}

	@Override
	public List<MailFooterLang> findByMailFooter(MailFooter mailFooter) {
		return findByCriteria(Restrictions.eq("mailFooter", mailFooter));
	}

	@Override
	public List<MailFooterLang> findByMailConfig(MailConfig mailConfig) {
		return findByCriteria(Restrictions.eq("mailConfig", mailConfig));
	}
}
