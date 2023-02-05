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

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.PermanentToken;

public interface JwtLongTimeService {

	PermanentToken create(Account authUser, Account actor, PermanentToken permanentToken) throws BusinessException;

	PermanentToken delete(Account authUser, Account actor, PermanentToken jwtLongTime) throws BusinessException;

	PermanentToken find(Account authUser, Account actor, String uuid) throws BusinessException;

	List<PermanentToken> findAll(Account authUser, Account actor) throws BusinessException;

	List<PermanentToken> findAllByDomain(Account authUser, AbstractDomain domain, Boolean recursive) throws BusinessException;

	PermanentToken update(User authUser, User actor, String uuid, PermanentToken permanentToken);

}
