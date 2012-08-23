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


import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class UserRepositoryImpl extends GenericUserRepositoryImpl<User> implements UserRepository<User> {

    public UserRepositoryImpl(HibernateTemplate hibernateTemplate) {
        super(hibernateTemplate);
    }

    @Override
    protected DetachedCriteria getNaturalKeyCriteria(User user) {
        DetachedCriteria det = DetachedCriteria.forClass(User.class).add(Restrictions.eq("lsUid", user.getLsUuid()));
        return det;
    }
} 