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
