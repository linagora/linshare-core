/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import org.apache.commons.lang.Validate;
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
	public List<UserPreference> findAll(String ownerUuid) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		return service.findByAccount(actor, owner);
	}

	@Override
	public UserPreference find(String ownerUuid, String uuid) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		return service.findByUuid(actor, owner, uuid);
	}

	@Override
	public UserPreference create(String ownerUuid, UserPreference dto) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		return service.create(actor, owner, dto);
	}

	@Override
	public UserPreference update(String ownerUuid, String uuid, UserPreference dto) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Validate.notNull(dto, "Missing user preference object");
		Validate.notEmpty(uuid, "Missing user preference uuid.");
		if (dto.getUuid() == null) {
			dto.setUuid(uuid);
		} else {
			Validate.isTrue(dto.getUuid().equals(uuid), "Uuid in path param does not match uuid in dto.");
		}
		return service.update(actor, owner, dto);
	}

	@Override
	public UserPreference delete(String ownerUuid, String uuid) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Validate.notEmpty(uuid, "Missing uuid");
		return service.delete(actor, owner, uuid);
	}

	@Override
	public void deleteAll(String ownerUuid) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		service.deleteAll(actor, owner);
	}

}
