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
import java.util.Set;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ShareEntryGroupRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.collect.Sets;

public class ShareEntryGroupRepositoryImpl extends AbstractRepositoryImpl<ShareEntryGroup>
		implements ShareEntryGroupRepository {

	public ShareEntryGroupRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ShareEntryGroup entity) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public ShareEntryGroup create(ShareEntryGroup entity)
			throws BusinessException, IllegalArgumentException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public ShareEntryGroup update(ShareEntryGroup entity)
			throws BusinessException, IllegalArgumentException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public ShareEntryGroup findByUuid(String uuid) {
		return DataAccessUtils
				.singleResult(findByCriteria(Restrictions.eq("uuid", uuid)));
	}

	@Override
	public List<String> findAllAnonymousShareEntriesAboutToBeNotified(Date dt) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("notified", false));
		det.add(Restrictions.lt("notificationDate", dt));
		// only identifier instead of entity
		det.setProjection(Projections.property("uuid"));
		det.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		// join
		det.createAlias("anonymousShareEntries", "ase");
		// restrict
		det.add(Restrictions.eq("ase.downloaded", Long.valueOf(0)));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(det);
		return list;
	}

	@Override
	public List<String> findAllShareEntriesAboutToBeNotified(Date dt) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("notified", false));
		det.add(Restrictions.lt("notificationDate", dt));
		// only identifier instead of entity
		det.setProjection(Projections.property("uuid"));
		det.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		// join
		det.createAlias("shareEntries", "se");
		// restrict
		det.add(Restrictions.eq("se.downloaded", Long.valueOf(0)));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(det);
		return list;
	}

	@Override
	public Set<String> findAllAboutToBeNotified(){
		Date dt = new Date();
		Set<String> res = Sets.newHashSet();
		// no way to do this using the actual database schema.
		// TODO : merge share entries and anonymous share entries into the same table.
		res.addAll(findAllShareEntriesAboutToBeNotified(dt));
		res.addAll(findAllAnonymousShareEntriesAboutToBeNotified(dt));
		return res;
	}

	@Override
	public List<String> findAllToPurge() {
		HibernateCallback<List<String>> action = new HibernateCallback<List<String>>() {
			@SuppressWarnings("unchecked")
			public List<String> doInHibernate(final Session session) throws HibernateException {
				StringBuilder sb = new StringBuilder();
				sb.append("select seg.uuid from ShareEntryGroup seg");
				sb.append(" left outer join seg.shareEntries se ");
				sb.append(" left outer join seg.anonymousShareEntries ase ");
				sb.append(" where ase is null and se is null");
				final Query query = session.createQuery(sb.toString());
				return 	query.list();
			}
		};
		return getHibernateTemplate().execute(action);
	}

	@Override
	public List<ShareEntryGroup> findAll(Account owner) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("owner", owner));
		@SuppressWarnings("unchecked")
		List<ShareEntryGroup> list = listByCriteria(det);
		return list;
	}
}
