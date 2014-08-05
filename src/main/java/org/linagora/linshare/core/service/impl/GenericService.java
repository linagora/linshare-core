package org.linagora.linshare.core.service.impl;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountPermission;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public abstract class GenericService {
	
	protected static Logger logger = LoggerFactory.getLogger(GenericService.class);

	protected abstract boolean hasReadPermission(Account actor);

	protected abstract boolean hasListPermission(Account actor);

	protected abstract boolean hasDeletePermission(Account actor);

	protected abstract boolean hasCreatePermission(Account actor);

	protected abstract boolean hasUpdatePermission(Account actor);

	protected boolean isAuthorized(Account actor, Account owner, PermissionType permission) {
		return this.isAuthorized(actor, owner, permission, null);
	}
	protected boolean isAuthorized(Account actor, Account owner, PermissionType permission, String resourceName) {
		if (actor.equals(owner)) {
			return true;
		} else if (actor.hasAllRights()) {
			return true;
		} else if (actor.hasDelegationRole()) {
			Validate.notNull(permission);
			if (permission.equals(PermissionType.GET)) {
				return hasReadPermission(actor);
			}else if (permission.equals(PermissionType.LIST)) {
				return hasListPermission(actor);
			}else if (permission.equals(PermissionType.CREATE)) {
				return hasCreatePermission(actor);
			}else if (permission.equals(PermissionType.UPDATE)) {
				return hasUpdatePermission(actor);
			}else if (permission.equals(PermissionType.DELETE)) {
				return hasDeletePermission(actor);
			}
			return false;
		}
		if (resourceName != null) {
			logger.error("Current actor " + actor.getAccountReprentation()
					+ " is trying to access to unauthorized " + resourceName
					+ " owned by : " + owner.getAccountReprentation());
		}
		return false;
	}

	protected boolean hasPermission(Account actor,
			final TechnicalAccountPermissionType permissionType) {
		TechnicalAccountPermission permission = actor.getPermission();
		Set<AccountPermission> accountPermissions = permission
				.getAccountPermissions();
		boolean contains = Iterables.any(accountPermissions,
				new Predicate<AccountPermission>() {
					@Override
					public boolean apply(final AccountPermission input) {
						return input.getPermission() == permissionType;
					}
				});
		logger.debug(permissionType.toString() + " : "
				+ String.valueOf(contains));
		return contains;
	}
}
