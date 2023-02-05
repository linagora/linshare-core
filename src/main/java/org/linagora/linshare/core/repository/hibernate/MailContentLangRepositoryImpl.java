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
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.repository.MailContentLangRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class MailContentLangRepositoryImpl extends
		AbstractRepositoryImpl<MailContentLang> implements
		MailContentLangRepository {

	public MailContentLangRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailContentLang entity) {
		return DetachedCriteria.forClass(getPersistentClass()).add(
				Restrictions.eq("id", entity.getId()));
	}

	@Override
	public MailContentLang findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public MailContent findMailContent(MailConfig cfg, Language lang,
			MailContentType type) {
		Disjunction and = Restrictions.disjunction();

		and.add(Restrictions.eq("mailConfig", cfg));
		and.add(Restrictions.eq("mailContentType", type));
		and.add(Restrictions.eq("language", lang));
		return DataAccessUtils.singleResult(findByCriteria(and))
				.getMailContent();
	}

	@Override
	public boolean isMailContentReferenced(MailContent content) {
		return !findByCriteria(Restrictions.eq("mailContent", content))
				.isEmpty();
	}

	@Override
	public List<MailContentLang> findByMailContent(MailContent mailContent) {
		return findByCriteria(Restrictions.eq("mailContent", mailContent));
	}
}
