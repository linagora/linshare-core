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
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SafeDetailFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SafeDetailService;
import org.linagora.linshare.mongo.entities.SafeDetail;

import com.google.common.base.Strings;

public class SafeDetailFacadeImpl extends GenericFacadeImpl implements SafeDetailFacade {

	private final SafeDetailService safeDetailService;

	public SafeDetailFacadeImpl(AccountService accountService,
			final SafeDetailService safeDetailService) {
		super(accountService);
		this.safeDetailService = safeDetailService;
	}

	@Override
	public List<SafeDetail> findAll(String actorUuid) throws BusinessException {
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return safeDetailService.findAll(authUser, actor);
	}

	@Override
	public SafeDetail delete(String actorUuid, String uuid, SafeDetail safeDetail) throws BusinessException {
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			safeDetail = safeDetailService.find(authUser, actor, uuid);
		} else {
			Validate.notNull(safeDetail, "safeDetail must be set");
			Validate.notEmpty(safeDetail.getUuid(), "safeDetail uuid must be set");
			safeDetail = safeDetailService.find(authUser, actor, safeDetail.getUuid());
		}
		return safeDetailService.delete(authUser, actor, safeDetail);
	}

	@Override
	public SafeDetail find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required safeDetail uuid");
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return safeDetailService.find(authUser, actor, uuid);
	}

	@Override
	public SafeDetail create(String actorUuid, SafeDetail safeDetail) throws BusinessException {
		Validate.notEmpty(safeDetail.getContainerUuid(), "Missing required container uuid");
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return safeDetailService.create(authUser, actor, safeDetail);
	}
}
