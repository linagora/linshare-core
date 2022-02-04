/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.rac.JwtLongTimeResourceAccessControl;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.PermanentToken;

public class JwtLongTimeResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, Account, PermanentToken> implements JwtLongTimeResourceAccessControl {

	protected AccountRepository<Account> accountRepository;

	protected DomainPermissionBusinessService permissionService;

	protected FunctionalityReadOnlyService functionalityReadOnlyService;

	protected AbstractDomainService abstractDomainService;

	public JwtLongTimeResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService,
			AccountRepository<Account> accountRepository,
			DomainPermissionBusinessService permissionService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			AbstractDomainService abstractDomainService) {
		super(functionalityService);
		this.accountRepository = accountRepository;
		this.permissionService = permissionService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account account, PermanentToken entry, Object... opt) {
		Functionality functionality = functionalityReadOnlyService.getJwtLongTimeFunctionality(authUser.getDomain());
		if (!functionality.getActivationPolicy().getStatus()) {
			return false;
		}
		if (authUser.hasAdminRole() || authUser.hasSuperAdminRole()) {
			AbstractDomain domain = abstractDomainService.findById(account.getDomainId());
			return permissionService.isAdminforThisDomain(authUser, domain);
		} else if (authUser.isInternal() || authUser.isGuest()) {
			if (account != null && authUser.equals(account)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account account, PermanentToken entry, Object... opt) {
		if (account.hasDelegationRole()) {
			return false;
		}
		Functionality functionality = functionalityReadOnlyService.getJwtLongTimeFunctionality(account.getDomain());
		if (!functionality.getActivationPolicy().getStatus()) {
			return false;
		}
		if (account.hasSuperAdminRole()) {
			return true;
		}
		if (account.isInternal() || account.isGuest()) {
			if (account != null && account.equals(authUser)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account actor) {
		return actor.getAccountRepresentation();
	}

	@Override
	protected Account getOwner(PermanentToken entry, Object... opt) {
		return accountRepository.findByLsUuid(entry.getActor().getUuid());
	}

	@Override
	protected String getEntryRepresentation(PermanentToken entry) {
		return entry.getUuid();
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account account, PermanentToken entry, Object... opt) {
		return defaultPermissionCheck(authUser, account, entry, null, true);
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account account, PermanentToken entry, Object... opt) {
		return defaultPermissionCheck(authUser, account, entry, null, true);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account account, PermanentToken entry, Object... opt) {
		return defaultPermissionCheck(authUser, account, entry, null, true);
	}

	@Override
	protected boolean defaultPermissionCheck(Account authUser, Account account, PermanentToken entry,
			TechnicalAccountPermissionType permission, boolean checkActorIsEntryOwner) {
		if (account.hasDelegationRole()) {
			return false;
		}
		Functionality functionality = functionalityReadOnlyService.getJwtLongTimeFunctionality(account.getDomain());
		if (!functionality.getActivationPolicy().getStatus()) {
			return false;
		}
		if (account.hasSuperAdminRole()) {
			return true;
		}
		if (account.hasAdminRole() && functionality.getConfigurationPolicy().getStatus()) {
			AbstractDomain domain = abstractDomainService.findById(entry.getDomain().getUuid());
			return permissionService.isAdminforThisDomain(account, domain);
		}
		if (account.isInternal() || account.isGuest()) {
			if (account != null && account.equals(authUser)) {
				if (checkActorIsEntryOwner) {
					return account.equals(getOwner(entry));
				}
				return true;
			}
		}
		return false;
	}

}
