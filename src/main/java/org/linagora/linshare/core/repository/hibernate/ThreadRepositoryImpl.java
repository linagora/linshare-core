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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ThreadRepositoryImpl extends GenericAccountRepositoryImpl<Thread>
		implements ThreadRepository {

	private static final MatchMode ANYWHERE = MatchMode.ANYWHERE;

	public ThreadRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Thread entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Thread.class);

		// filter enabled thread only.
		det.add(Restrictions.eq("enable", true));
		// query
		det.add(Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public Thread create(Thread entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setLsUuid(UUID.randomUUID().toString());
		entity.setMail(entity.getLsUuid());
		return super.create(entity);
	}

	@Override
	public List<Thread> findAll() {
		DetachedCriteria det = DetachedCriteria.forClass(Thread.class);
		det.add(Restrictions.eq("toUpgrade", false));

		// filter enabled thread only.
		det.add(Restrictions.eq("enable", true));
		// query
		det.add(Restrictions.eq("destroyed", 0L));
		return findByCriteria(det);
	}

	@Override
	public List<Thread> findAllWhereMember(User actor) {
		DetachedCriteria det = DetachedCriteria.forClass(Thread.class);
		det.add(Restrictions.eq("destroyed", 0L));
		det.add(Restrictions.eq("toUpgrade", false));
		// query
		det.createAlias("myMembers", "member");
		det.add(Restrictions.eq("member.user", actor));
		return findByCriteria(det);
	}

	@Override
	public List<Thread> findAllWhereAdmin(User actor) {
		DetachedCriteria det = DetachedCriteria.forClass(Thread.class);
		det.add(Restrictions.eq("destroyed", 0L));
		det.add(Restrictions.eq("toUpgrade", false));

		// query
		det.createAlias("myMembers", "member");
		det.add(Restrictions.eq("member.user", actor));
		det.add(Restrictions.eq("member.admin", true));
		return findByCriteria(det);
	}

	@Override
	public List<Thread> findAllWhereCanUpload(User actor) {
		DetachedCriteria det = DetachedCriteria.forClass(Thread.class);
		det.add(Restrictions.eq("destroyed", 0L));
		det.add(Restrictions.eq("toUpgrade", false));

		// query
		det.createAlias("myMembers", "member");
		det.add(Restrictions.eq("member.user", actor));
		det.add(Restrictions.eq("member.canUpload", true));
		return findByCriteria(det);
	}

	@Override
	public List<Thread> findLatestWhereMember(User actor, int limit) {
		DetachedCriteria det = DetachedCriteria.forClass(Thread.class);
		det.add(Restrictions.eq("destroyed", 0L));
		det.add(Restrictions.eq("toUpgrade", false));

		if (limit < 1)
			 limit = 1;
		// query
		det.createAlias("myMembers", "member");
		det.add(Restrictions.eq("member.user", actor));
		det.addOrder(Order.desc("modificationDate"));
		return findByCriteria(det, limit);
	}

	@Override
	public List<Thread> searchByName(User actor, String pattern) {
		DetachedCriteria det = DetachedCriteria.forClass(Thread.class);
		det.add(Restrictions.eq("destroyed", 0L));
		det.add(Restrictions.eq("toUpgrade", false));

		// query
		det.createAlias("myMembers", "member");
		if (!actor.hasSuperAdminRole())
			det.add(Restrictions.eq("member.user", actor));
		det.addOrder(Order.desc("modificationDate"));
		det.add(Restrictions.ilike("name", pattern, ANYWHERE));
		return findByCriteria(det);
	}

	@Override
	public List<Thread> searchAmongMembers(User actor, String pattern) {
		DetachedCriteria det = DetachedCriteria.forClass(Thread.class);
		det.add(Restrictions.eq("destroyed", 0L));
		det.add(Restrictions.eq("toUpgrade", false));

		Disjunction or = Restrictions.disjunction();

		det.createAlias("myMembers", "member2");
		det.createAlias("member2.user", "u");
		or.add(Restrictions.ilike("u.firstName", pattern, ANYWHERE));
		or.add(Restrictions.ilike("u.lastName", pattern, ANYWHERE));
		det.add(or);

		DetachedCriteria sub = DetachedCriteria.forClass(Thread.class);
		sub.createAlias("myMembers", "member");
		if (!actor.hasSuperAdminRole())
			sub.add(Restrictions.eq("member.user", actor));
		sub.setProjection(Projections.id());

		det.add(Subqueries.propertyIn("id", sub));
		return findByCriteria(det);
	}

	@Override
	public List<String> findAllThreadToUpgrade() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Thread.class);
		criteria.setProjection(Projections.property("lsUuid"));
		criteria.add(Restrictions.eq("destroyed", 0L));
		criteria.add(Restrictions.eq("toUpgrade", true));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@Override
	public Thread setAsUpgraded(Thread entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity must not be null");
		}
		entity.setToUpgrade(false);
		// check that entity is not transient :
		load(entity);
		getHibernateTemplate().update(entity);
		return entity;
	}

}