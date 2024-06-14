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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.NotNull;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class UserRepositoryImpl extends GenericUserRepositoryImpl<User>
		implements UserRepository<User> {

	public UserRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(User.class).add(
				Restrictions.eq("lsUuid", user.getLsUuid()));
		return det;
	}

	@Override
	public User findByLogin(String login) {
		try {
			return super.findByMail(login);
		} catch (IllegalStateException e) {
			logger.error("you are looking for account using login '" + login + "' but your login is not unique, same account logins in different domains.");;
			logger.debug("error: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public User findByExternalUid(@NotNull String externalUid) {
		try {
			return super.findByExternalUid(externalUid);
		} catch (IllegalStateException e) {
			logger.error("you are looking for account using login '" + externalUid + "' but your login is not unique, same account logins in different domains.");;
			logger.debug("error: " + e.getMessage());
			throw e;
		}
	}
}