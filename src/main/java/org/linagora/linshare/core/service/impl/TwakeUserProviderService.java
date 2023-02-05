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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AbstractTwakeUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface TwakeUserProviderService {

	User findUser(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String mail) throws BusinessException;

	List<User> searchUser(
		AbstractDomain domain, AbstractTwakeUserProvider userProvider, String mail, String firstName, String lastName) throws BusinessException;

	List<User> autoCompleteUser(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String pattern) throws BusinessException;

	List<User> autoCompleteUser(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String firstName, String lastName) throws BusinessException;

	Boolean isUserExist(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String mail) throws BusinessException;

	User auth(AbstractTwakeUserProvider userProvider, String login, String userPasswd) throws BusinessException;

	User searchForAuth(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String login) throws BusinessException;
}
