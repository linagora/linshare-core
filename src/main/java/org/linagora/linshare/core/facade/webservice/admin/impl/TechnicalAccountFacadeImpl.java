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

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.TechnicalAccountFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.TechnicalAccountDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.TechnicalAccountService;
import org.linagora.linshare.utils.Version;

import com.google.common.collect.Sets;

public class TechnicalAccountFacadeImpl extends AdminGenericFacadeImpl
		implements TechnicalAccountFacade {

	private final TechnicalAccountService technicalAccountService;

	public TechnicalAccountFacadeImpl(final AccountService accountService,
			final TechnicalAccountService technicalAccountService) {
		super(accountService);
		this.technicalAccountService = technicalAccountService;
	}

	@Override
	public TechnicalAccountDto create(TechnicalAccountDto dto, Version version) throws BusinessException {
		User authUser = checkAuth();
		Validate.notNull(dto);
		Validate.notEmpty(dto.getName(), "name must be set.");
		if (version.equals(Version.V1)) {
			// In Admin API V1 we never use 'mail' attribute so we force it to null
			dto.setMail(null);
		} else {
			Validate.notEmpty(dto.getMail(), "mail must be set.");
		}
		TechnicalAccount technicalAccount = new TechnicalAccount(dto);
		TechnicalAccount create = technicalAccountService.create(authUser, technicalAccount);
		return new TechnicalAccountDto(create);
	}

	@Override
	public TechnicalAccountDto delete(String uuid) throws BusinessException {
		User authUser = checkAuth();
		Validate.notEmpty(uuid, "uuid must be set.");
		TechnicalAccount account = technicalAccountService.find(authUser, uuid);
		technicalAccountService.delete(authUser, account);
		return new TechnicalAccountDto(account);
	}

	@Override
	public TechnicalAccountDto delete(TechnicalAccountDto dto) throws BusinessException {
		User authUser = checkAuth();
		Validate.notNull(dto, "dto must be set.");
		Validate.notEmpty(dto.getUuid(), "uuid must be set.");
		TechnicalAccount account = technicalAccountService.find(authUser, dto.getUuid());
		technicalAccountService.delete(authUser, account);
		return new TechnicalAccountDto(account);
	}

	@Override
	public TechnicalAccountDto find(String uuid) throws BusinessException {
		User authUser = checkAuth();
		Validate.notEmpty(uuid, "uuid must be set.");
		TechnicalAccount account = technicalAccountService.find(authUser, uuid);
		return new TechnicalAccountDto(account);
	}

	@Override
	public Set<TechnicalAccountDto> findAll() throws BusinessException {
		User authUser = checkAuth();
		Set<TechnicalAccountDto> res = Sets.newHashSet();
		Set<TechnicalAccount> all = technicalAccountService.findAll(authUser);
		for (TechnicalAccount entity : all) {
			res.add(new TechnicalAccountDto(entity));
		}
		return res;
	}

	@Override
	public TechnicalAccountDto update(TechnicalAccountDto dto)
			throws BusinessException {
		User authUser = checkAuth();
		Validate.notNull(dto, "dto must be set.");
		Validate.notEmpty(dto.getUuid(), "uuid must be set.");
		Validate.notEmpty(dto.getName(), "name must be set.");
		Validate.notEmpty(dto.getMail(), "mail must be set.");
		TechnicalAccount newUser = TechnicalAccountDto.toObject(dto);
		boolean unlock = dto.isLocked() != null & !dto.isLocked();
		TechnicalAccount account = technicalAccountService.update(authUser, newUser, unlock);
		return new TechnicalAccountDto(account);
	}

	@Override
	public void changePassword(String uuid, PasswordDto password) throws BusinessException {
		User authUser = checkAuth();
		Validate.notNull(password, "Password mus be set");
		Validate.notEmpty(password.getOldPwd(), "The old password is required");
		Validate.notEmpty(password.getNewPwd(), "The new password is required");
		Validate.notEmpty(uuid, "uuid must be set.");
		TechnicalAccount account = technicalAccountService.find(authUser, uuid);
		technicalAccountService.changePassword(authUser, account, password.getOldPwd(), password.getNewPwd());
	}

	/**
	 * Helpers
	 */
	
	private User checkAuth() throws BusinessException {
		return checkAuthentication(Role.SUPERADMIN);
	}
}
