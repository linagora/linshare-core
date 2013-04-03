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

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
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
	public ThreadMember findById(long id) {
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
		
		if (entries == null || entries.isEmpty()) {
            return null;
        } else if (entries.size() == 1) {
            return entries.get(0);
        } else {
            throw new IllegalStateException("Thread member must be unique");
        }
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
	
	@Override
	public boolean isUserAdminOfAny(User user) {
		List<ThreadMember> entries = null;
		final DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);
		
		// query
		det.add(Restrictions.eq("user", user));
		det.add(Restrictions.eq("admin", true));
		
		// limiting to only one match, fetching all matches is unnecessary
		entries = getHibernateTemplate().execute(
				new HibernateCallback<List<ThreadMember>>() {
					@SuppressWarnings("unchecked")
					@Override
					public List<ThreadMember> doInHibernate(final Session session)
							throws HibernateException, SQLException {
						return det.getExecutableCriteria(session).setCacheable(true)
								.setMaxResults(1).list();
					}
				});
		return entries != null && entries.size() > 0;
		
	}

	@Override
	public boolean isUserAdmin(User user, Thread thread) {
		List<ThreadMember> entries = null;
		DetachedCriteria det = DetachedCriteria.forClass(ThreadMember.class);
		
		// query
		det.add(Restrictions.eq("user", user));
		det.add(Restrictions.eq("thread", thread));
		det.add(Restrictions.eq("admin", true));
		entries = findByCriteria(det);
		return entries != null && entries.size() > 0;
	}
}
