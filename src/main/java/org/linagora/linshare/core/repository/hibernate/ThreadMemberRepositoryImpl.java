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

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ThreadMemberRepositoryImpl extends
		AbstractRepositoryImpl<ThreadMember> implements ThreadMemberRepository {

	public ThreadMemberRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ThreadMember entity) {
		return DetachedCriteria.forClass(ThreadMember.class).add(
				Restrictions.eq("id", entity.getId()));
	}

	@Override
	public ThreadMember findById(long id) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);

		det.add(Restrictions.eq("id", id));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public ThreadMember findUserThreadMember(Account thread, User user) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);

		det.add(Restrictions.eq("thread", thread));
		det.add(Restrictions.eq("user", user));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public List<ThreadMember> findAllUserMemberships(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);

		det.add(Restrictions.eq("user", user));
		return findByCriteria(det);
	}

	@Override
	public List<ThreadMember> findAllUserAdminMemberships(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);

		det.add(Restrictions.eq("user", user));
		det.add(Restrictions.eq("admin", true));
		return findByCriteria(det);
	}

	@Override
	public boolean isUserAdminOfAny(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);

		det.add(Restrictions.eq("user", user));
		det.add(Restrictions.eq("admin", true));
		det.setProjection(Projections.rowCount());
		return DataAccessUtils.singleResult(findByCriteria(det)) != null;
	}

	@Override
	public boolean isUserAdmin(User user, Thread thread) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);

		det.add(Restrictions.eq("user", user));
		det.add(Restrictions.eq("thread", thread));
		det.add(Restrictions.eq("admin", true));
		return DataAccessUtils.singleResult(findByCriteria(det)) != null;
	}

	@Override
	public long count(Thread thread) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);

		det.add(Restrictions.eq("thread", thread));
		det.setProjection(Projections.rowCount());
		return DataAccessUtils.longResult(findByCriteria(det));
	}

	@Override
	public List<ThreadMember> findAllThreadMembers(Thread thread) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);
		det.createAlias("user", "member");
		det.add(Restrictions.eq("thread", thread));
		det.add(Restrictions.eq("member.destroyed", 0L));
		return findByCriteria(det);
	}

	@Override
	public List<String> findAllAccountUuidForThreadMembers(Thread thread) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.createAlias("user", "member");
		det.add(Restrictions.eq("thread", thread));
		det.add(Restrictions.eq("member.destroyed", 0L));
		det.setProjection(Projections.property("member.lsUuid"));
		@SuppressWarnings("unchecked")
		List<String> ret = (List<String>)listByCriteria(det);
		return ret;
	}

	@Override
	public List<ThreadMember> findAllInconsistentThreadMembers(Thread thread) {
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);
		det.createAlias("user", "member");
		det.add(Restrictions.eq("thread", thread));
		det.add(Restrictions.gt("member.destroyed", 0L));
		return findByCriteria(det);
	}
}
