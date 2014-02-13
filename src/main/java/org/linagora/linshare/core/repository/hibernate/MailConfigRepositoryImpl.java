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

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailContentType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailConfigRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class MailConfigRepositoryImpl extends
		AbstractRepositoryImpl<MailConfig> implements MailConfigRepository {

	public MailConfigRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailConfig entry) {
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
	public MailConfig findByUuid(String uuid) {
		DetachedCriteria det = DetachedCriteria.forClass(MailConfig.class);
		det.add(Restrictions.eq("uuid", uuid));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public MailConfig create(MailConfig entity) throws BusinessException {
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public MailConfig update(MailConfig entity) throws BusinessException {
		return super.update(entity);
	}

	@Override
	public List<MailConfig> findAllMailConfig() {
		return super.findAll();
	}

	@Override
	public List<MailConfig> findMailConfig(AbstractDomain domain) {
		return findByCriteria(Restrictions.eq("domain", domain));
	}

	@SuppressWarnings("unchecked")
	@Override
	public MailContent getMailContent(AbstractDomain domain, Language lang,
			MailContentType mailContentType) {
		
		DetachedCriteria crit = DetachedCriteria
				.forClass(MailContentLang.class);

		crit.add(Restrictions.eq("mailContentType", mailContentType.toInt()));
		crit.add(Restrictions.eq("language", lang.toInt()));
		crit.add(Restrictions.eq("mailConfig", domain.getCurrentMailConfiguration()));
	
		List<MailContentLang> l = listByCriteria(crit);
		MailContentLang mcl = DataAccessUtils.singleResult(l);

		return mcl == null ? null : mcl.getMailContent();
		
		
		
//		DetachedCriteria crit = DetachedCriteria
//				.forClass(MailContentLang.class);
//		Conjunction and = Restrictions.conjunction();
//
//		and.add(Restrictions.eq("mailContentType", mailContentType.toInt()));
//		and.add(Restrictions.eq("language", lang.toInt()));
//
//		crit.add(and);
//		crit.add(Subqueries.propertyIn(
//				"id",
//				DetachedCriteria.forClass(MailConfig.class)
//						.add(Restrictions.eq("id", domain
//								.getCurrentMailConfiguration().getId()))
//						.createAlias("mailContents", "mcl")
//						.setProjection(Property.forName("mcl.id"))));
//
//		List<MailContentLang> l = listByCriteria(crit);
//		MailContentLang mcl = DataAccessUtils.singleResult(l);
//
//		return mcl == null ? null : mcl.getMailContent();
		
	}
}
