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
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.exception.BusinessException;

public interface WelcomeMessagesService {

	List<WelcomeMessages> findAll(User actor, String domainId, boolean parent)
			throws BusinessException;

	WelcomeMessages find(Account actor, String uuid) throws BusinessException;
	
	WelcomeMessages createByCopy(User actor, WelcomeMessages wlcm, String domainId)
			throws BusinessException;

	WelcomeMessages create(User actor, WelcomeMessages welcomeMessages) throws BusinessException;

	/**
	 * 
	 * @param actor
	 * @param wlcm
	 * @param domainUuids
	 *            no modification will be done for domain links if null
	 * @return WelcomeMessages
	 * @throws BusinessException
	 */
	WelcomeMessages update(User actor, WelcomeMessages wlcm,
			List<String> domainUuids) throws BusinessException;

	WelcomeMessages delete(User actor, String uuid) throws BusinessException;
}
