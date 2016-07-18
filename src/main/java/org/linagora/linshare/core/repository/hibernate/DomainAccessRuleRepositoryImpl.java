/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.repository.DomainAccessRuleRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainAccessRuleRepositoryImpl extends AbstractRepositoryImpl<DomainAccessRule> implements DomainAccessRuleRepository {

	public DomainAccessRuleRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public DomainAccessRule findById(long id) {
		List<DomainAccessRule> domainAccessRule = findByCriteria(Restrictions.eq("id", id));
		if (domainAccessRule == null || domainAccessRule.isEmpty()) {
			return null;
		} else if (domainAccessRule.size() == 1) {
			return domainAccessRule.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainAccessRule entity) {
		DetachedCriteria det = DetachedCriteria.forClass(DomainAccessRule.class);

		det.add( Restrictions.eq("id", entity.getPersistenceId()));
		return det;
	}

	@Override
	public List<DomainAccessRule> findByDomain(AbstractDomain domain) {
		return findByCriteria(Restrictions.eq("domain", domain));
	}

	@Override
	public long countNumberAccessRulesByDomain(AbstractDomain domain) {
		DetachedCriteria det = DetachedCriteria
				.forClass(DomainAccessRule.class);
		det.setProjection(Projections.rowCount());
		det.add(Restrictions.eq("domain", domain));
		return DataAccessUtils.longResult(findByCriteria(det));
	}
}