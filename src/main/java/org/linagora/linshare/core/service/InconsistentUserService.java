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

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface InconsistentUserService {

	List<Internal> findAllInconsistent(User actor) throws BusinessException;

	/**
	 * Update an inconsistent user's domain.
	 * 
	 * @param actor the actor must be superadmin
	 * @param uuid the inconsistent user uuid
	 * @param domain the domain identifier
	 * 
	 * @throws BusinessException
	 */
	void updateDomain(User actor, String uuid, String domain)
			throws BusinessException;

	List<String> findAllUserUuids(Account actor) throws BusinessException;

	List<String> findAllIconsistentsUuid(Account actor) throws BusinessException;
}
