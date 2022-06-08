/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.core.rac.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.UserResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

import com.google.common.collect.Lists;


public class UserResourceAccessControlImpl extends AbstractResourceAccessControlImpl<Account, Account, User>
		implements UserResourceAccessControl {

	private DomainPermissionBusinessService permissionService;

	public UserResourceAccessControlImpl(
			FunctionalityReadOnlyService functionalityService,
			DomainPermissionBusinessService permissionService) {
		super(functionalityService);
		this.permissionService = permissionService;
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, User entry, Object... opt) {
		if (authUser.hasAdminRole() || authUser.hasSuperAdminRole()) {
			if (authUser.equals(actor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, User entry, Object... opt) {
		if (authUser.hasAdminRole()) {
			if (checkAdminOfDomain(actor, opt)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkAdminOfDomain(Account actor, Object... opt) {
		@SuppressWarnings("unchecked")
		List<AbstractDomain> domains = (List<AbstractDomain>) opt[0];
		List<String> notAllowedDomains = Lists.newArrayList();
		for (AbstractDomain domain : domains) {
			if (!permissionService.isAdminforThisDomain(actor, domain)) {
				notAllowedDomains.add(domain.getUuid());
			}
		}
		if (!notAllowedDomains.isEmpty()) {
			String errMsg = String.format("You are not allowed to list users of these domains: %1$s", notAllowedDomains);
			throw new BusinessException(BusinessErrorCode.USER_FORBIDDEN, errMsg);
		}
		return true;
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, User entry, Object... opt) {
		if (authUser.hasAdminRole() || authUser.hasSuperAdminRole()) {
			if (authUser.equals(actor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, User entry, Object... opt) {
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, User entry, Object... opt) {
		if (authUser.hasAdminRole() || authUser.hasSuperAdminRole()) {
			if (authUser.equals(actor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected Account getOwner(User entry, Object... opt) {
		if (entry != null) {
			return entry.getOwner();
		}
		return null;
	}

	@Override
	protected String getEntryRepresentation(User entry) {
		return entry.getAccountRepresentation();
	}

}
