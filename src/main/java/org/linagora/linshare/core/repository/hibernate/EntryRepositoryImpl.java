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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.EntryRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class EntryRepositoryImpl extends AbstractRepositoryImpl<Entry>
		implements EntryRepository {

	public EntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Entry entry) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass()).add(
				Restrictions.eq("id", entry.getId()));
		return det;
	}

	/**
	 * Find a entry using its uuid.
	 * 
	 * @param uuid
	 * @return found document (null if no document found).
	 */
	@Override
	public Entry findById(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public Entry create(Entry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public Entry update(Entry entity) throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
	}

	@Override
	public List<Entry> getOutdatedEntry() {
		return findByCriteria(Restrictions.lt("expirationDate",
				Calendar.getInstance()));
	}

	@Override
	public List<Entry> findAllMyShareEntries(User owner) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ShareEntry.class);
		criteria.add(Restrictions.eq("entryOwner", owner));
		List<Entry> list = findByCriteria(criteria);
		criteria = DetachedCriteria.forClass(AnonymousShareEntry.class);
		criteria.add(Restrictions.eq("entryOwner", owner));
		list.addAll(findByCriteria(criteria));
		return list;
	}
}
