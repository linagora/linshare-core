/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.util.Set;

import org.linagora.linshare.core.business.service.TechnicalAccountBusinessService;
import org.linagora.linshare.core.business.service.TechnicalAccountPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountPermission;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.TechnicalAccountPermissionService;
import org.linagora.linshare.core.service.TechnicalAccountService;
import org.linagora.linshare.core.utils.HashUtils;

public class TechnicalAccountServiceImpl implements TechnicalAccountService {

	private final TechnicalAccountBusinessService technicalAccountBusinessService;

	private final TechnicalAccountPermissionService technicalAccountPermissionService;

	private final AccountService accountService;

	public TechnicalAccountServiceImpl(
			final TechnicalAccountBusinessService technicalAccountBusinessService,
			final AccountService accountService,
			final TechnicalAccountPermissionService technicalAccountPermissionService) {
		super();
		this.technicalAccountBusinessService = technicalAccountBusinessService;
		this.accountService = accountService;
		this.technicalAccountPermissionService = technicalAccountPermissionService;
	}

	@Override
	public TechnicalAccount create(Account actor, TechnicalAccount account) throws BusinessException {
		// TODO : check rights, log actions.
		TechnicalAccountPermission accountPermission = technicalAccountPermissionService.create(actor, new TechnicalAccountPermission());
		account.setPermission(accountPermission);
		return technicalAccountBusinessService.create(LinShareConstants.rootDomainIdentifier, account);
	}

	@Override
	public void delete(Account actor, TechnicalAccount account)
			throws BusinessException {
		// TODO : check rights, log actions.
		technicalAccountBusinessService.delete(account);
		technicalAccountPermissionService.delete(actor, account.getPermission());
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
	public TechnicalAccount update(Account actor, TechnicalAccount accountDto)
			throws BusinessException {
		// TODO : check rights, log actions.
		TechnicalAccount technicalAccount = technicalAccountBusinessService.find(accountDto.getLsUuid());
		technicalAccount.setLastName(accountDto.getLastName());
		technicalAccount.setMail(accountDto.getMail());
		// TODO
//		technicalAccount.setEnable(accountDto.isEnable());
		TechnicalAccountPermission accountPermission = technicalAccount.getPermission();
		accountPermission.setAccountPermissions(accountDto.getPermission().getAccountPermissions());
		technicalAccountPermissionService.update(actor, accountPermission);
		return technicalAccountBusinessService.update(technicalAccount);
	}

	@Override
	public void changePassword(String uuid, String oldPassword,
							   String newPassword) throws BusinessException {
		TechnicalAccount account = technicalAccountBusinessService.find(uuid);
		if (account == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE,
					"Could not find a user with the uuid " + uuid);
		}

		if (!account.getPassword().equals(
				HashUtils.hashSha1withBase64(oldPassword.getBytes()))) {
			throw new BusinessException(BusinessErrorCode.AUTHENTICATION_ERROR,
					"The supplied password is invalid");
		}

		account.setPassword(HashUtils.hashSha1withBase64(newPassword.getBytes()));
		technicalAccountBusinessService.update(account);
	}
}
