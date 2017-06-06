/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ThreadEntryRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ThreadEntryRepositoryImpl extends
		AbstractRepositoryImpl<ThreadEntry> implements ThreadEntryRepository {

	public ThreadEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ThreadEntry entry) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadEntry.class);

		det.add(Restrictions.eq("uuid", entry.getUuid()));
		return det;
	}

	@Override
	public ThreadEntry findByUuid(String uuid) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadEntry.class);

		det.add(Restrictions.eq("uuid", uuid));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public ThreadEntry create(ThreadEntry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public List<ThreadEntry> findAllThreadEntries(Thread owner) {
		return findByCriteria(Restrictions.eq("entryOwner", owner));
	}

	@Override
	public long count(Thread thread) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadEntry.class);

		det.add(Restrictions.eq("entryOwner", thread));
		det.setProjection(Projections.rowCount());
		return DataAccessUtils.longResult(findByCriteria(det));
	}

	@Override
	public List<ThreadEntry> findAllDistinctEntries(Thread thread) {
		List<ThreadEntry> res = null;
		DetachedCriteria crit = DetachedCriteria.forClass(ThreadEntry.class);
		crit.add(Restrictions.eq("entryOwner", thread));
		crit.addOrder(Order.desc("creationDate"));
		crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		res = findByCriteria(crit);
		return res;
	}

	@Override
	public long getUsedSpace(Account account) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadEntry.class);
		det.add(Restrictions.eq("entryOwner", account));
		det.createAlias("document", "doc");
		ProjectionList columns = Projections.projectionList()
				.add(Projections.sum("doc.size"));
		det.setProjection(columns);
		List<ThreadEntry> result = findByCriteria(det);
		if (result == null || result.isEmpty() || result.get(0) == null) {
			return 0;
		}
		return DataAccessUtils.longResult(result);
	}
}
