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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.JwtLongTimeTokenFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.JwtLongTimeService;
import org.linagora.linshare.mongo.entities.PermanentToken;

import com.google.common.base.Strings;

public class JwtLongTimeTokenFacadeImpl extends AdminGenericFacadeImpl implements JwtLongTimeTokenFacade {

	private JwtLongTimeService jwtLongTimeService;

	private final AbstractDomainService abstractDomainService;

	public JwtLongTimeTokenFacadeImpl (final AccountService accountService,
			JwtLongTimeService jwtLongTimeService,
			AbstractDomainService abstractDomainService) {
		super(accountService);
		this.jwtLongTimeService = jwtLongTimeService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public PermanentToken find(String uuid) {
		Validate.notEmpty(uuid, "token uuid must be set");
		Account authUser = checkAuthentication(Role.ADMIN);
		return jwtLongTimeService.find(authUser, authUser, uuid);
	}

	@Override
	public PermanentToken create(PermanentToken permanentToken) throws BusinessException {
		Validate.notEmpty(permanentToken.getLabel(), "label must be set");
		Validate.notNull(permanentToken.getActor());
		Validate.notEmpty(permanentToken.getActor().getUuid(), "actor uuid must be set");
		if (Strings.isNullOrEmpty(permanentToken.getDescription())) {
			permanentToken.setDescription("");
		}
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, permanentToken.getActor().getUuid());
		return jwtLongTimeService.create(authUser, actor, permanentToken);
	}

	@Override
	public List<PermanentToken> findAll(String domainUuid) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		Boolean isRecursive = false;
		if (Strings.isNullOrEmpty(domainUuid)) {
			domainUuid = authUser.getDomain().getUuid();
			isRecursive = true;
		}
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		return jwtLongTimeService.findAllByDomain(authUser, domain, isRecursive);
	}

	@Override
	public List<PermanentToken> findAllByActor(String actorUuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "actorUuid must be set");
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, actorUuid);
		return jwtLongTimeService.findAll(authUser, actor);
	}

	@Override
	public PermanentToken delete(PermanentToken permanentToken, String uuid) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		if (!Strings.isNullOrEmpty(uuid)) {
			permanentToken = jwtLongTimeService.find(authUser, authUser, uuid);
		} else {
			Validate.notNull(permanentToken, "jwtLongTime object must be set");
			Validate.notEmpty(permanentToken.getUuid(), "jwtLongTime uuid must be set");
			permanentToken = jwtLongTimeService.find(authUser, authUser, permanentToken.getUuid());
		}
		Validate.notNull(permanentToken.getActor(), "Actor must be set");
		Account actor = getActor(authUser, permanentToken.getActor().getUuid());
		return jwtLongTimeService.delete(authUser, actor, permanentToken);
	}

	@Override
	public PermanentToken update(PermanentToken permanentToken, String uuid) throws BusinessException {
		Validate.notNull(permanentToken, "permanentToken object must be set");
		if (!Strings.isNullOrEmpty(uuid)) {
			permanentToken.setUuid(uuid);
		}
		Validate.notEmpty(permanentToken.getUuid(), "permanentToken uuid must be set");
		Validate.notNull(permanentToken.getActor(), "Actor must be set");
		Validate.notEmpty(permanentToken.getActor().getUuid(), "actor uuid must be set");
		if (Strings.isNullOrEmpty(permanentToken.getDescription())) {
			permanentToken.setDescription("");
		}
		User authUser = checkAuthentication();
		User actor = getActor(authUser, permanentToken.getActor().getUuid());
		return jwtLongTimeService.update(authUser, actor, permanentToken.getUuid(), permanentToken);
	}

}
