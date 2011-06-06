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

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.MessagesConfiguration;
import org.linagora.linShare.core.repository.MessagesRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class MessagesRepositoryImpl extends AbstractRepositoryImpl<MessagesConfiguration> implements MessagesRepository {

	public MessagesRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

    protected DetachedCriteria getNaturalKeyCriteria(MessagesConfiguration messages) {
        DetachedCriteria det = DetachedCriteria.forClass(MessagesConfiguration.class).add(Restrictions.eq("id", messages.getId()));
        return det;
    }
    
    public MessagesConfiguration loadDefault() {
		List<MessagesConfiguration> messages = findByCriteria(Restrictions.eq("id", 1L));
		
		if (messages != null && messages.size() == 1) {
			return messages.get(0);
		} else {
			throw new IllegalStateException("No default messages found in DB");
		}
	}

}
