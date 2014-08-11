package org.linagora.linshare.core.service.impl;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountPermission;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public abstract class GenericEntryService {

	protected static Logger logger = LoggerFactory
			.getLogger(GenericEntryService.class);

	protected abstract boolean hasReadPermission(Account actor);

	protected abstract boolean hasListPermission(Account actor);

	protected abstract boolean hasDeletePermission(Account actor);

	protected abstract boolean hasCreatePermission(Account actor);

	protected abstract boolean hasUpdatePermission(Account actor);

	protected boolean isAuthorized(Account actor, Account owner,
			PermissionType permission) {
		return this.isAuthorized(actor, owner, permission, null, null);
	}

	protected boolean isAuthorized(Account actor, Account owner,
			PermissionType permission, Object entry) {
		return this.isAuthorized(actor, owner, permission, entry, null);
	}

	protected void preChecks(Account actor, Account owner) {
		Validate.notNull(actor, "Missing actor account");
		Validate.notEmpty(actor.getLsUuid(), "Missing actor uuid");
		Validate.notNull(owner, "Missing owner account");
		Validate.notEmpty(owner.getLsUuid(), "Missing owner uuid");
		if (logger.isDebugEnabled()) {
			logger.debug("Current actor " + actor.getAccountReprentation());
			logger.debug("Current owner " + actor.getAccountReprentation());
		}
	}

	protected boolean isAuthorized(Account actor, Account owner,
			PermissionType permission, Object entry, String resourceName) {
		if (actor.equals(owner)) {
			return true;
		} else if (actor.hasAllRights()) {
			return true;
		} else if (actor.hasDelegationRole()) {
			Validate.notNull(permission);
			if (permission.equals(PermissionType.GET)) {
				return hasReadPermission(actor);
			} else if (permission.equals(PermissionType.LIST)) {
				return hasListPermission(actor);
			} else if (permission.equals(PermissionType.CREATE)) {
				return hasCreatePermission(actor);
			} else if (permission.equals(PermissionType.UPDATE)) {
				return hasUpdatePermission(actor);
			} else if (permission.equals(PermissionType.DELETE)) {
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

	protected void checkReadPermission(Account actor, Entry entry,
			BusinessErrorCode errCode) throws BusinessException {
		Account owner = entry.getEntryOwner();
		if (!isAuthorized(actor, owner, PermissionType.GET, entry)) {
			logger.error(actor.getAccountReprentation()
					+ " is not authorized to get the entry "
					+ entry.getEntryType().toString() + " (" + entry.getUuid()
					+ ") owned by : " + owner.getAccountReprentation());
			throw new BusinessException(errCode,
					"You are not authorized to get this entry.");
		}
	}

	protected void checkListPermission(Account actor, Account owner,
			EntryType type, BusinessErrorCode errCode) throws BusinessException {
		if (!isAuthorized(actor, owner, PermissionType.LIST)) {
			logger.error(actor.getAccountReprentation()
					+ " is not authorized to list all entries "
					+ type.toString() + " owned by : "
					+ owner.getAccountReprentation());
			throw new BusinessException(errCode,
					"You are not authorized to list all entries.");
		}
	}

	protected void checkCreatePermission(Account actor, Account owner,
			EntryType type, BusinessErrorCode errCode) throws BusinessException {
		if (!isAuthorized(actor, owner, PermissionType.CREATE)) {
			logger.error(actor.getAccountReprentation()
					+ " is not authorized to create entry " + type.toString()
					+ " for user : " + owner.getAccountReprentation());
			throw new BusinessException(errCode,
					"You are not authorized to create entry.");
		}
	}

	protected void checkUpdatePermission(Account actor, Entry entry,
			BusinessErrorCode errCode) throws BusinessException {
		Account owner = entry.getEntryOwner();
		if (!isAuthorized(actor, owner, PermissionType.UPDATE, entry)) {
			logger.error(actor.getAccountReprentation()
					+ " is not authorized to update the entry "
					+ entry.getEntryType().toString() + " (" + entry.getUuid()
					+ ") owned by : " + owner.getAccountReprentation());
			throw new BusinessException(errCode,
					"You are not authorized to update this entry.");
		}
	}

	protected void checkDeletePermission(Account actor, Entry entry,
			BusinessErrorCode errCode) throws BusinessException {
		Account owner = entry.getEntryOwner();
		if (!isAuthorized(actor, owner, PermissionType.DELETE, entry)) {
			logger.error(actor.getAccountReprentation()
					+ " is not authorized to delete the entry "
					+ entry.getEntryType().toString() + " (" + entry.getUuid()
					+ ") owned by : " + owner.getAccountReprentation());
			throw new BusinessException(errCode,
					"You are not authorized to delete this entry.");
		}
	}
}
