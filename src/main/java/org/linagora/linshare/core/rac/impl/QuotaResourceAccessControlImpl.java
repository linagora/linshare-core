/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.rac.QuotaResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class QuotaResourceAccessControlImpl extends AbstractResourceAccessControlImpl<Account, Account, Quota>
		implements QuotaResourceAccessControl {

	public QuotaResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, Quota entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.QUOTA_GET);
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (authUser.hasSystemAccountRole() || authUser.hasSuperAdminRole()) {
				return true;
			}
			if (actor != null && actor.equals(authUser)) {
				return true;
			}
			if (authUser.hasAdminRole()) {
				if (actor != null) {
					return actor.getDomain().isManagedBy(authUser);
				}
				if (actor == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(authUser);
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, Quota entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.QUOTA_LIST);
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (authUser.hasSystemAccountRole() || authUser.hasSuperAdminRole()) {
				return true;
			}
			if (actor != null && actor.equals(authUser)) {
				return true;
			}
			if (authUser.hasAdminRole()) {
				if (actor != null) {
					return actor.getDomain().isManagedBy(authUser);
				}
				if (actor == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(authUser);
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, Quota entry, Object... opt) {
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, Quota entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.QUOTA_CREATE);
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (authUser.hasSystemAccountRole() || authUser.hasSuperAdminRole()) {
				return true;
			}
			if (authUser.hasAdminRole()) {
				if (actor != null) {
					return actor.getDomain().isManagedBy(authUser);
				}
				if (actor == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(authUser);
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, Quota entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.QUOTA_UPDATE);
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (authUser.hasSystemAccountRole() || authUser.hasSuperAdminRole()) {
				return true;
			}
			if (authUser.hasAdminRole()) {
				if (actor != null) {
					return actor.getDomain().isManagedBy(authUser);
				}
				if (actor == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(authUser);
				}
			}
		}
		return false;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(Quota entry) {
		return "Id :" + entry.getId() + " Account :" + entry.getAccount().getFullName() + " Last Value :"
				+ entry.getLastValue() + " Current Value :" + entry.getCurrentValue();
	}

	protected boolean isAuthorized(Account authUser, Account targetedAccount, PermissionType permission, Quota entry,
			Class<?> clazz, Object... opt) {
		Validate.notNull(authUser);
		Validate.notNull(permission);
		if (authUser.hasAllRights())
			return true;
		if (permission.equals(PermissionType.GET)) {
			if (hasReadPermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.LIST)) {
			if (hasListPermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.CREATE)) {
			if (hasCreatePermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.UPDATE)) {
			if (hasUpdatePermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DELETE)) {
			if (hasDeletePermission(authUser, targetedAccount, entry, opt))
				return true;
		}
		if (clazz != null) {
			StringBuilder sb = getAuthUserStringBuilder(authUser);
			sb.append(" is trying to access to unauthorized resource named ");
			sb.append(clazz.toString());
			appendOwner(sb, entry, opt);
			logger.error(sb.toString());
		}
		return false;
	}

	@Override
	protected Account getOwner(Quota entry, Object... opt) {
		return entry.getAccount();
	}
}
