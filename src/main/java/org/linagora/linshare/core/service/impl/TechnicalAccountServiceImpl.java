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

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.TechnicalAccountBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.TechnicalAccountPermissionService;
import org.linagora.linshare.core.service.TechnicalAccountService;
import org.linagora.linshare.core.service.UserService;


public class TechnicalAccountServiceImpl implements TechnicalAccountService {

	private final TechnicalAccountBusinessService technicalAccountBusinessService;

	private final TechnicalAccountPermissionService technicalAccountPermissionService;
	
	private final PasswordService passwordService;

	private final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	private final UserService userService;

	public TechnicalAccountServiceImpl(
			final TechnicalAccountBusinessService technicalAccountBusinessService,
			final TechnicalAccountPermissionService technicalAccountPermissionService,
			final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			final PasswordService passwordService,
			final UserService userService) {
		super();
		this.technicalAccountBusinessService = technicalAccountBusinessService;
		this.technicalAccountPermissionService = technicalAccountPermissionService;
		this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
		this.passwordService = passwordService;
		this.userService = userService;
	}

	@Override
	public TechnicalAccount create(Account actor, TechnicalAccount account) throws BusinessException {
		Validate.notNull(actor, "actor must be set.");
		Validate.notNull(account, "account must be set.");
		Validate.notEmpty(account.getLastName(), "last name must be set.");
		// TODO : check rights, log actions.
		// Check role : only uploadprop or delegation
		// mail unicity ?
		TechnicalAccountPermission accountPermission = technicalAccountPermissionService.create(actor, new TechnicalAccountPermission());
		account.setPermission(accountPermission);
		account.setLastName(sanitize(account.getLastName()));
		account = technicalAccountBusinessService.create(actor.getDomainId(), account);
		passwordService.validateAndStorePassword(account, account.getPassword());
		return account;
	}

	private String sanitize (String input) {
		return sanitizerInputHtmlBusinessService.strictClean(input);
	}

	@Override
	public void delete(Account actor, TechnicalAccount account)
			throws BusinessException {
		// TODO : check rights, log actions.
		TechnicalAccountPermission permission = account.getPermission();
		account.setPermission(null);
		technicalAccountBusinessService.update(account);
		if (permission != null) {
			technicalAccountPermissionService.delete(actor, permission);
		}
		technicalAccountBusinessService.delete(account);
	}

	@Override
	public TechnicalAccount find(Account actor, String uuid)
			throws BusinessException {
		// TODO : check rights, log actions.
		TechnicalAccount account = technicalAccountBusinessService.find(uuid);
		if (account == null) {
			throw new BusinessException(BusinessErrorCode.TECHNICAL_ACCOUNT_NOT_FOUND,
					"The technical account does not exist : " + uuid);
		}
		return account;
	}

	@Override
	public Set<TechnicalAccount> findAll(Account actor)
			throws BusinessException {
		// TODO : check rights, log actions.
		return technicalAccountBusinessService.findAll(LinShareConstants.rootDomainIdentifier);
	}

	@Override
	public TechnicalAccount update(Account actor, TechnicalAccount dto)
			throws BusinessException {
		// TODO : check rights, log actions.
		TechnicalAccount entity = find(actor, dto.getLsUuid());
		checkAccountPermission(actor, entity, dto);
		entity.setLastName(sanitize(dto.getLastName()));
		entity.setMail(dto.getMail());
		entity.setEnable(dto.isEnable());
		return technicalAccountBusinessService.update(entity);
	}

	private void checkAccountPermission(Account actor, TechnicalAccount foundAccount, TechnicalAccount account) {
		if (Role.DELEGATION.equals(foundAccount.getRole())) {
			TechnicalAccountPermission permissionDto = account.getPermission();
			permissionDto.setUuid(foundAccount.getPermission().getUuid());
			technicalAccountPermissionService.update(actor, permissionDto);
		}
	}

	@Override
	public void changePassword(User authUser, User actor, String oldPassword, String newPassword)
			throws BusinessException {
		userService.changePassword(authUser, actor, oldPassword, newPassword);
	}
}
