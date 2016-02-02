/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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

package org.linagora.linshare.core.rac.impl;

import org.apache.commons.lang.Validate;
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
	protected boolean hasReadPermission(Account actor, Account owner, Quota entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor, TechnicalAccountPermissionType.QUOTA_GET);
		}
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()) {
				return true;
			}
			if (owner != null && owner.equals(actor)) {
				return true;
			}
			if (actor.hasAdminRole()) {
				if (owner != null) {
					return owner.getDomain().isManagedBy(actor);
				}
				if (owner == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(actor);
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account owner, Quota entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor, TechnicalAccountPermissionType.QUOTA_LIST);
		}
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()) {
				return true;
			}
			if (owner != null && owner.equals(actor)) {
				return true;
			}
			if (actor.hasAdminRole()) {
				if (owner != null) {
					return owner.getDomain().isManagedBy(actor);
				}
				if (owner == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(actor);
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account owner, Quota entry, Object... opt) {
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account owner, Quota entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor, TechnicalAccountPermissionType.QUOTA_CREATE);
		}
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()) {
				return true;
			}
			if (actor.hasAdminRole()) {
				if (owner != null) {
					return owner.getDomain().isManagedBy(actor);
				}
				if (owner == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(actor);
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account owner, Quota entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor, TechnicalAccountPermissionType.QUOTA_UPDATE);
		}
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()) {
				return true;
			}
			if (actor.hasAdminRole()) {
				if (owner != null) {
					return owner.getDomain().isManagedBy(actor);
				}
				if (owner == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(actor);
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

	protected boolean isAuthorized(Account actor, Account targetedAccount, PermissionType permission, Quota entry,
			Class<?> clazz, Object... opt) {
		Validate.notNull(actor);
		Validate.notNull(permission);
		if (actor.hasAllRights())
			return true;
		if (permission.equals(PermissionType.GET)) {
			if (hasReadPermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.LIST)) {
			if (hasListPermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.CREATE)) {
			if (hasCreatePermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.UPDATE)) {
			if (hasUpdatePermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DELETE)) {
			if (hasDeletePermission(actor, targetedAccount, entry, opt))
				return true;
		}
		if (clazz != null) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is trying to access to unauthorized resource named ");
			sb.append(clazz.toString());
			appendOwner(sb, entry, opt);
			logger.error(sb.toString());
		}
		return false;
	}
}
