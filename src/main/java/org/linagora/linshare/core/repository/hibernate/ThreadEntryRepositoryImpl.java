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

import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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

	/**
	 * Find a document using its id.
	 * 
	 * @param id
	 * @return found document (null if no document found).
	 */
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
	public ThreadEntry update(ThreadEntry entity) throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
	}

	@Override
	public List<ThreadEntry> findAllThreadEntries(Thread owner) {
		return findByCriteria(Restrictions.eq("entryOwner", owner));
	}

	@Override
	public int count(Thread thread) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadEntry.class);

		det.add(Restrictions.eq("entryOwner", thread));
		det.setProjection(Projections.rowCount());
		return DataAccessUtils.intResult(findByCriteria(det));
	}

	@Deprecated
	@Override
	public List<ThreadEntry> findAllThreadEntriesTaggedWith(Thread owner,
			String[] names) {
		List<ThreadEntry> res = null;

		for (String name : names) {
			DetachedCriteria criteria = DetachedCriteria
					.forClass(ThreadEntry.class);
			criteria.add(Restrictions.eq("entryOwner", owner));
			criteria.createAlias("tagAssociations", "ta", Criteria.LEFT_JOIN);
			criteria.createAlias("ta.tag", "t", Criteria.LEFT_JOIN);
			criteria.add(Restrictions.eq("t.name", name));
			if (res == null)
				res = findByCriteria(criteria);
			else
				res.retainAll(findByCriteria(criteria));
		}
		return res;
	}

}
