package org.linagora.linshare.core.rac.impl;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountPermission;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AbstractResourceAccessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public abstract class AbstractResourceAccessControlImpl<O, E> implements
		AbstractResourceAccessControl<O, E> {

	protected static Logger logger = LoggerFactory
			.getLogger(AbstractResourceAccessControlImpl.class);

	protected abstract boolean hasReadPermission(Account actor);

	protected abstract boolean hasListPermission(Account actor);

	protected abstract boolean hasDeletePermission(Account actor);

	protected abstract boolean hasCreatePermission(Account actor);

	protected abstract boolean hasUpdatePermission(Account actor);

	protected O getOwner(E entry) {
		return null;
	}

	protected String getOwnerRepresentation(O owner) {
		return null;
	}

	protected O getRecipient(E entry) {
		return null;
	}

	protected String getRecipientRepresentation(E entry) {
		return null;
	}

	protected abstract String getEntryRepresentation(E entry);

	protected boolean isAuthorized(Account actor, O owner,
			PermissionType permission) {
		return this.isAuthorized(actor, owner, permission, null, null);
	}

	protected boolean isAuthorized(Account actor, O owner,
			PermissionType permission, E entry) {
		return this.isAuthorized(actor, owner, permission, entry, null);
	}

	private void appendOwner(O owner, StringBuilder sb) {
		if (owner != null) {
			String or = getOwnerRepresentation(owner);
			if (or != null) {
				sb.append(" owned by : ");
				sb.append(or);
			}
		}
	}

	private void appendForAccount(O owner, StringBuilder sb) {
		if (owner != null) {
			String or = getOwnerRepresentation(owner);
			if (or != null) {
				sb.append(" for account : ");
				sb.append(or);
			}
		}
	}

	private StringBuilder getActorStringBuilder(Account actor) {
		StringBuilder sb = new StringBuilder("Actor ");
		sb.append(actor.getAccountReprentation());
		return sb;
	}

	protected boolean isAuthorized(Account actor, O owner,
			PermissionType permission, E entry, String resourceName) {
		O recipient = getRecipient(entry);
		if (owner != null && actor.equals(owner)) {
			return true;
		} else if (recipient != null && actor.equals(recipient)) {
			return true;
		} else if (actor.hasAllRights()) {
			return true;
		} else if (actor.hasDelegationRole()) {
			Validate.notNull(permission);
			if (permission.equals(PermissionType.GET)) {
				if (hasReadPermission(actor))
					return true;
			} else if (permission.equals(PermissionType.LIST)) {
				if (hasListPermission(actor))
					return true;
			} else if (permission.equals(PermissionType.CREATE)) {
				if (hasCreatePermission(actor))
					return true;
			} else if (permission.equals(PermissionType.UPDATE)) {
				if (hasUpdatePermission(actor))
					return true;
			} else if (permission.equals(PermissionType.DELETE)) {
				if (hasDeletePermission(actor))
					return true;
			}
		}
		if (resourceName != null) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is trying to access to unauthorized ");
			sb.append(resourceName);
			appendOwner(getOwner(entry), sb);
			logger.error(sb.toString());
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

	public void checkReadPermission(Account actor, E entry,
			BusinessErrorCode errCode) throws BusinessException {
		O owner = getOwner(entry);
		if (!isAuthorized(actor, owner, PermissionType.GET, entry)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to get the entry ");
			sb.append(getEntryRepresentation(entry));
			appendOwner(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to get this entry.");
		}
	}

	public void checkListPermission(Account actor, O owner, EntryType type,
			BusinessErrorCode errCode) throws BusinessException {
		if (!isAuthorized(actor, owner, PermissionType.LIST)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to list all entries ");
			sb.append(type.toString());
			appendOwner(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to list all entries.");
		}
	}

	public void checkCreatePermission(Account actor, O owner, EntryType type,
			BusinessErrorCode errCode) throws BusinessException {
		if (!isAuthorized(actor, owner, PermissionType.CREATE)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to create entry ");
			sb.append(type.toString());
			appendForAccount(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to create entry.");
		}
	}

	public void checkUpdatePermission(Account actor, E entry,
			BusinessErrorCode errCode) throws BusinessException {
		O owner = getOwner(entry);
		if (!isAuthorized(actor, owner, PermissionType.UPDATE, entry)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to create entry ");
			sb.append(getEntryRepresentation(entry));
			appendForAccount(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to update this entry.");
		}
	}

	public void checkDeletePermission(Account actor, E entry,
			BusinessErrorCode errCode) throws BusinessException {
		O owner = getOwner(entry);
		if (!isAuthorized(actor, owner, PermissionType.DELETE, entry)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to delete the entry ");
			sb.append(getEntryRepresentation(entry));
			appendOwner(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to delete this entry.");
		}
	}
}
