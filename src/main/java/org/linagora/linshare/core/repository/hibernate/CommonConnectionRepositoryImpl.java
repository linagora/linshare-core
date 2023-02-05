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

import java.util.Iterator;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.linagora.linshare.core.domain.constants.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class CommonConnectionRepositoryImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonConnectionRepositoryImpl.class);

	private final HibernateTemplate hibernateTemplate;

	public CommonConnectionRepositoryImpl(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public Optional<ServerType> findServerTypeByUuid(String uuid) {
		HibernateCallback<ServerType> action = new HibernateCallback<>() {
			public ServerType doInHibernate(final Session session) throws HibernateException {
				Query<ServerType> query = session.createQuery("SELECT serverType FROM RemoteServer WHERE uuid = :uuid");
				query.setParameter("uuid", uuid);
				Iterator<ServerType> iterator = query.iterate();
				if (!iterator.hasNext()) {
					LOGGER.debug("No remote server found for: " + uuid);
					LOGGER.debug("Query: " + query.getQueryString());
					return null;
				}
				return iterator.next();
			}
		};
		return Optional.ofNullable(hibernateTemplate.execute(action));
	}
}
