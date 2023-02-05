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
package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.RemoteServer;
import org.linagora.linshare.core.exception.BusinessException;

import java.util.List;

public interface RemoteServerService<T extends RemoteServer> {

	T create(T remoteServer)
			throws BusinessException;

	List<T> findAll()
			throws BusinessException;

	T find(String uuid)
			throws BusinessException;

	T update(T remoteServer)
			throws BusinessException;

	T delete(String uuid)
			throws BusinessException;

	boolean isUsed(String uuid);

	List<AbstractDomain> findAllDomainsByRemoteServer(T remoteServer);
}
