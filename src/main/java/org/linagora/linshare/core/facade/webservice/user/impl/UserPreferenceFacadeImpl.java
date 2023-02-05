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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.UserPreferenceFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserPreferenceService;
import org.linagora.linshare.mongo.entities.UserPreference;

public class UserPreferenceFacadeImpl extends UserGenericFacadeImp implements UserPreferenceFacade {

	protected UserPreferenceService service;

	public UserPreferenceFacadeImpl(AccountService accountService, UserPreferenceService service) {
		super(accountService);
		this.service = service;
	}

	@Override
	public List<UserPreference> findAll(String actorUuid) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		return service.findByAccount(authUser, actor);
	}

	@Override
	public UserPreference find(String actorUuid, String uuid) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		return service.findByUuid(authUser, actor, uuid);
	}

	@Override
	public UserPreference create(String actorUuid, UserPreference dto) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		return service.create(authUser, actor, dto);
	}

	@Override
	public UserPreference update(String actorUuid, String uuid, UserPreference dto) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		Validate.notNull(dto, "Missing user preference object");
		Validate.notEmpty(uuid, "Missing user preference uuid.");
		if (dto.getUuid() == null) {
			dto.setUuid(uuid);
		} else {
			Validate.isTrue(dto.getUuid().equals(uuid), "Uuid in path param does not match uuid in dto.");
		}
		return service.update(authUser, actor, dto);
	}

	@Override
	public UserPreference delete(String actorUuid, String uuid) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		Validate.notEmpty(uuid, "Missing uuid");
		return service.delete(authUser, actor, uuid);
	}

	@Override
	public void deleteAll(String actorUuid) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		service.deleteAll(authUser, actor);
	}

}
