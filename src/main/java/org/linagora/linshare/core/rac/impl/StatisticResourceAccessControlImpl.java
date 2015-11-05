package org.linagora.linshare.core.rac.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Statistic;
import org.linagora.linshare.core.rac.StatisticResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class StatisticResourceAccessControlImpl extends AbstractResourceAccessControlImpl<Account, Account, Statistic>
		implements StatisticResourceAccessControl {

	public StatisticResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account actor, Account owner, Statistic entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor, TechnicalAccountPermissionType.STATISTIC_GET);
		}
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()){
				return true;
			}
			if (owner != null && owner.equals(actor)){
				return true;
			}
			if (actor.hasAdminRole()) {
				if (owner != null){
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
	protected boolean hasListPermission(Account actor, Account owner, Statistic entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor, TechnicalAccountPermissionType.STATISTIC_LIST);
		}
		if (actor.isInternal() || actor.isGuest()) {
			if (actor.hasSystemAccountRole() || actor.hasSuperAdminRole()){
				return true;
			}
			if (owner != null && owner.equals(actor)){
				return true;
			}
			if (actor.hasAdminRole()) {
				if (owner != null){
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
	protected boolean hasDeletePermission(Account actor, Account account, Statistic entry, Object... opt) {
		return actor.hasSystemAccountRole() || actor.hasSuperAdminRole();
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account account, Statistic entry, Object... opt) {
		return actor.hasSystemAccountRole() || actor.hasSuperAdminRole();
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account account, Statistic entry, Object... opt) {
		return false;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(Statistic entry) {
		return "";
	}

	protected boolean isAuthorized(Account actor, Account targetedAccount, PermissionType permission,
			Statistic entry, Class<?> clazz, Object... opt) {
		Validate.notNull(actor);
		Validate.notNull(permission);
		if (actor.hasAllRights())
			return true;
		if (permission.equals(PermissionType.GET)) {
			if (hasReadPermission(actor, targetedAccount, entry, opt)){
				return true;
			}
		} else if (permission.equals(PermissionType.LIST)) {
			if (hasListPermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.CREATE)) {
			if (hasCreatePermission(actor, targetedAccount, entry, opt)){
				return true;
			}
		} else if (permission.equals(PermissionType.UPDATE)) {
			if (hasUpdatePermission(actor, targetedAccount, entry, opt)){
				return true;
			}
		} else if (permission.equals(PermissionType.DELETE)) {
			if (hasDeletePermission(actor, targetedAccount, entry, opt)){
				return true;
			}
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
