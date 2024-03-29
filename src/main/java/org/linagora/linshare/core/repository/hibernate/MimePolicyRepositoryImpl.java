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
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MimePolicyRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class MimePolicyRepositoryImpl extends
		AbstractRepositoryImpl<MimePolicy> implements
		MimePolicyRepository {

	public MimePolicyRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public MimePolicy findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MimePolicy entity) {
		return DetachedCriteria.forClass(getPersistentClass()).add(
				Restrictions.eq("id", entity.getId()));
	}

	@Override
	public MimePolicy create(MimePolicy entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public MimePolicy update(MimePolicy entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public MimePolicy enableAll(final MimePolicy entity) throws BusinessException {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session) throws HibernateException {
				final Query<?> query = session.createQuery("UPDATE MimeType SET enable = true WHERE mimePolicy = :mimePolicy");
				query.setParameter("mimePolicy", entity);
				return (long) query.executeUpdate();
			}
		};
		getHibernateTemplate().execute(action);
		return load(entity);
	}

	@Override
	public MimePolicy disableAll(final MimePolicy entity) throws BusinessException {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session) throws HibernateException {
				final Query<?> query = session.createQuery("UPDATE MimeType SET enable = false WHERE mimePolicy = :mimePolicy");
				query.setParameter("mimePolicy", entity);
				return (long) query.executeUpdate();
			}
		};
		getHibernateTemplate().execute(action);
		return load(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllUuid() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass())
				.setProjection(Projections.property("uuid"));
		return listByCriteria(crit);
	}
}
