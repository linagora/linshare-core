package org.linagora.linshare.core.rac.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.rac.QuotaResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class QuotaResourceAccessControlImpl
		extends AbstractResourceAccessControlImpl<Account, Account, Quota>
		implements QuotaResourceAccessControl {

	public QuotaResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account actor, Account owner, Quota entry, Object... opt) {
		if (actor.hasDelegationRole())
			return hasPermission(actor, TechnicalAccountPermissionType.QUOTA_GET);
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()) {
				return true;
			}
			if (owner != null && owner.equals(actor))
				return true;
			if (actor.hasAdminRole()) {
				if (owner != null)
					return owner.getDomain().isManagedBy(actor);
				if (owner == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(actor);
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account owner, Quota entry, Object... opt) {
		if (actor.hasDelegationRole())
			return hasPermission(actor, TechnicalAccountPermissionType.QUOTA_LIST);
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()) {
				return true;
			}
			if (owner != null && owner.equals(actor))
				return true;
			if (actor.hasAdminRole()) {
				if (owner != null)
					return owner.getDomain().isManagedBy(actor);
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
		if (actor.hasDelegationRole())
			return hasPermission(actor, TechnicalAccountPermissionType.QUOTA_CREATE);
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()){
				return true;
			}
			if (actor.hasAdminRole()) {
				if (owner != null)
					return owner.getDomain().isManagedBy(actor);
				if (owner == null && opt != null && opt.length > 0) {
					return ((AbstractDomain) opt[0]).isManagedBy(actor);
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account owner, Quota entry, Object... opt) {
		if (actor.hasDelegationRole())
			return hasPermission(actor, TechnicalAccountPermissionType.QUOTA_UPDATE);
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()){
				return true;
			}
			if (actor.hasAdminRole()) {
				if (owner != null)
					return owner.getDomain().isManagedBy(actor);
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

	protected boolean isAuthorized(Account actor, Account targetedAccount, PermissionType permission,
			Quota entry, Class<?> clazz, Object... opt) {
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
