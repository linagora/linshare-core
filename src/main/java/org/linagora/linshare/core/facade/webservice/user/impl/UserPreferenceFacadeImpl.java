/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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
