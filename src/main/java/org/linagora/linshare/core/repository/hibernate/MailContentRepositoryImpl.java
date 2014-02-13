/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.repository.hibernate;

import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailTemplateEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailContentRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class MailContentRepositoryImpl extends
		AbstractRepositoryImpl<MailContent> implements MailContentRepository {

	public MailContentRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailContent entry) {
		DetachedCriteria det = DetachedCriteria.forClass(MailConfig.class);
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
	public MailContent findByUuid(String uuid) {
		DetachedCriteria det = DetachedCriteria.forClass(MailContent.class);
		det.add(Restrictions.eq("uuid", uuid));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public MailContent create(MailContent entity) throws BusinessException {
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public MailContent update(MailContent entity) throws BusinessException {
		return super.update(entity);
	}

	@Override
	public List<MailContent> findAllMailContent() {
		return super.findAll();
	}

	@Override
	public List<MailContent> findMailContent(AbstractDomain domain) {
		return findByCriteria(Restrictions.eq("domain", domain));
	}

	@Override
	public MailContent getMailContent(AbstractDomain domain, Language lang,
			MailContentType mailContentType) {
		Conjunction and = Restrictions.conjunction();

		and.add(Restrictions.eq("domain", domain));
		and.add(Restrictions.eq("mailContentType", mailContentType.toInt()));
		and.add(Restrictions.eq("language", lang.toInt()));

		return DataAccessUtils.singleResult(findByCriteria(and));
	}

}
