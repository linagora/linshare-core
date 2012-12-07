/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
 */
package org.linagora.linshare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ThreadMemberRepositoryImpl extends AbstractRepositoryImpl<ThreadMember> implements ThreadMemberRepository {

	public ThreadMemberRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ThreadMember entity) {
		return DetachedCriteria.forClass(ThreadMember.class).add(Restrictions.eq("id", entity.getId()));
	}

	@Override
	public ThreadMember findById(String id) {
		List<ThreadMember> entries = findByCriteria(Restrictions.eq("id", id));
        if (entries == null || entries.isEmpty()) {
            return null;
        } else if (entries.size() == 1) {
            return entries.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }

	@Override
    public ThreadMember findUserThreadMember(Thread thread, User user) {
		List<ThreadMember> entries = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ThreadMember.class);
		criteria.add(Restrictions.eq("thread", thread));
		criteria.add(Restrictions.eq("user", user));
		entries = findByCriteria(criteria);
		return entries.get(0);
    }
	@Override
	public List<ThreadMember> findAllUserMemberships(User user) {
		List<ThreadMember> entries = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ThreadMember.class);
		criteria.add(Restrictions.eq("user", user));
		entries = findByCriteria(criteria);
		return entries;
	}
	
	@Override
	public List<ThreadMember> findAllUserAdminMemberships(User user) {
		List<ThreadMember> entries = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ThreadMember.class);
		criteria.add(Restrictions.eq("user", user));
		criteria.add(Restrictions.eq("admin", true));
		entries = findByCriteria(criteria);
		return entries;
	}
	
}
