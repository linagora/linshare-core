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
package org.linagora.linShare.core.repository.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.repository.GroupRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class GroupRepositoryImpl extends AbstractRepositoryImpl<Group>
		implements GroupRepository {

	public GroupRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	protected DetachedCriteria getNaturalKeyCriteria(Group entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Group.class).add(Restrictions.eq("name", entity.getName()).ignoreCase());
		return det;
	}

	public Group findByName(final String name) {
		List<Group> groups = findByCriteria(Restrictions.eq("name", name).ignoreCase());
		
		if (groups == null || groups.isEmpty()) {
			return null;
		} else if (groups.size() == 1) {
			return groups.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Group> findByUser(final User user) {
		
		return (List<Group>)  getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				
				String queryString = "select g from Group g join g.members as members where members.user= :user order by g.name";
				Query query = session.createQuery(queryString);
				
			    query.setParameter("user", user);
				
				return query.setCacheable(false).list();
			}
		});
	}

}
