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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class ThreadMemberRepositoryImpl extends
		AbstractRepositoryImpl<WorkgroupMember> implements ThreadMemberRepository {

	public ThreadMemberRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(WorkgroupMember entity) {
		return DetachedCriteria.forClass(WorkgroupMember.class).add(
				Restrictions.eq("id", entity.getId()));
	}

	@Override
	public WorkgroupMember findById(long id) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkgroupMember.class);

		det.add(Restrictions.eq("id", id));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public WorkgroupMember findUserThreadMember(Account thread, User user) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkgroupMember.class);

		det.add(Restrictions.eq("thread", thread));
		det.add(Restrictions.eq("user", user));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public List<WorkgroupMember> findAllUserMemberships(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkgroupMember.class);

		det.add(Restrictions.eq("user", user));
		return findByCriteria(det);
	}

	@Override
	public List<WorkgroupMember> findAllUserAdminMemberships(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkgroupMember.class);

		det.add(Restrictions.eq("user", user));
		det.add(Restrictions.eq("admin", true));
		return findByCriteria(det);
	}

	@Override
	public boolean isUserAdminOfAny(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkgroupMember.class);

		det.add(Restrictions.eq("user", user));
		det.add(Restrictions.eq("admin", true));
		det.setProjection(Projections.rowCount());
		return DataAccessUtils.singleResult(findByCriteria(det)) != null;
	}

	@Override
	public boolean isUserAdmin(User user, WorkGroup workGroup) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkgroupMember.class);
		det.add(Restrictions.eq("user", user));
		det.add(Restrictions.eq("thread", workGroup));
		det.add(Restrictions.eq("admin", true));
		return DataAccessUtils.singleResult(findByCriteria(det)) != null;
	}

	@Override
	public long count(WorkGroup workGroup) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkgroupMember.class);
		det.add(Restrictions.eq("thread", workGroup));
		det.setProjection(Projections.rowCount());
		return DataAccessUtils.longResult(findByCriteria(det));
	}

	@Override
	public List<WorkgroupMember> findAllThreadMembers(WorkGroup workGroup) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkgroupMember.class);
		det.createAlias("user", "member");
		det.add(Restrictions.eq("thread", workGroup));
		det.add(Restrictions.eq("member.destroyed", 0L));
		return findByCriteria(det);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllAccountUuidForThreadMembers(WorkGroup workGroup) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.createAlias("user", "member");
		det.add(Restrictions.eq("thread", workGroup));
		det.add(Restrictions.eq("member.destroyed", 0L));
		det.setProjection(Projections.property("member.lsUuid"));
		List<String> ret = listByCriteria(det);
		return ret;
	}

	@Override
	public List<WorkgroupMember> findAllInconsistentThreadMembers(WorkGroup workGroup) {
		DetachedCriteria det = DetachedCriteria.forClass(WorkgroupMember.class);
		det.createAlias("user", "member");
		det.add(Restrictions.eq("thread", workGroup));
		det.add(Restrictions.gt("member.destroyed", 0L));
		return findByCriteria(det);
	}
}
