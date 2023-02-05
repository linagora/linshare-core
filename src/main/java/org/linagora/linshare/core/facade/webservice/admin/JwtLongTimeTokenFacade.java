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
package org.linagora.linshare.core.facade.webservice.admin;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.PermanentToken;

public interface JwtLongTimeTokenFacade {

	PermanentToken find(String uuid);

	PermanentToken create(PermanentToken permanentToken) throws BusinessException;

	List<PermanentToken> findAll(String domainUuid) throws BusinessException;

	List<PermanentToken> findAllByActor(String actorUuid) throws BusinessException;

	PermanentToken delete(PermanentToken jwtLongTime, String uuid) throws BusinessException;

	PermanentToken update(PermanentToken permanentToken, String uuid) throws BusinessException;

}
