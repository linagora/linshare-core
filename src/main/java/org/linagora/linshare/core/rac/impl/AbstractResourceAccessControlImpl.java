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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountPermission;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AbstractResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResourceAccessControlImpl<A, R, E> implements
		AbstractResourceAccessControl<A, R, E> {

	protected final FunctionalityReadOnlyService functionalityService;

	public AbstractResourceAccessControlImpl(
			FunctionalityReadOnlyService functionalityService) {
		super();
		this.functionalityService = functionalityService;
	}

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected abstract boolean hasReadPermission(Account authUser, A account,
			E entry, Object... opt);

	protected abstract boolean hasListPermission(Account authUser, A account,
			E entry, Object... opt);

	protected abstract boolean hasDeletePermission(Account authUser, A account,
			E entry, Object... opt);

	protected abstract boolean hasCreatePermission(Account authUser, A account,
			E entry, Object... opt);

	protected abstract boolean hasUpdatePermission(Account authUser, A account,
			E entry, Object... opt);

	protected abstract String getTargetedAccountRepresentation(A actor);

	protected abstract A getOwner(E entry, Object... opt);

	protected R getRecipient(E entry) {
		return null;
	}

	protected String getRecipientRepresentation(E entry) {
		return null;
	}

	protected abstract String getEntryRepresentation(E entry);

	protected void appendOwner(StringBuilder sb, E entry, Object... opt) {
		A owner = getOwner(entry, opt);
		if (owner != null) {
			String or = getOwnerRepresentation(owner);
			if (or != null) {
				sb.append(" owned by : ");
				sb.append(or);
			}
		}
	}

	protected String getOwnerRepresentation(A owner) {
		return null;
	}

	protected StringBuilder getAuthUserStringBuilder(Account authUser) {
		StringBuilder sb = new StringBuilder("authUser ");
		sb.append(authUser.getAccountRepresentation());
		return sb;
	}

	protected boolean isAuthorized(Account authUser, A actor,
			PermissionType permission, E entry, Class<?> clazz, Object... opt) {
		Validate.notNull(authUser);
		Validate.notNull(actor);
		Validate.notNull(permission);
		if (authUser.hasAllRights()) {
			logger.trace("authUser has all rights");
			return true;
		}
		if (permission.equals(PermissionType.GET)) {
			if (hasReadPermission(authUser, actor, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.LIST)) {
			if (hasListPermission(authUser, actor, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.CREATE)) {
			if (hasCreatePermission(authUser, actor, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.UPDATE)) {
			if (hasUpdatePermission(authUser, actor, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DELETE)) {
			if (hasDeletePermission(authUser, actor, entry, opt))
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

	protected boolean hasPermission(Account authUser,
			final TechnicalAccountPermissionType permissionType) {
		TechnicalAccountPermission p = authUser.getPermission();
		boolean contains = false;
		for (AccountPermission permission : p.getAccountPermissions()) {
			if (permission.getPermission().equals(permissionType)) {
				contains = true;
				break;
			}
		}
		logger.debug(permissionType.toString() + " : " + contains);
		return contains;
	}

	protected boolean defaultPermissionCheck(Account authUser, Account actor, E entry,
			TechnicalAccountPermissionType permission) {
		return defaultPermissionCheck(authUser, actor, entry, permission, true);
	}

	/**
	 * Only the entry actor has all rights (create, read, list, update, delete,
	 * download and thumb nail download.
	 * 
	 * @param authUser
	 * @param actor
	 * @param entry
	 * @param permission
	 * @param checkActorIsEntryOwner
	 * @return boolean
	 */
	protected boolean defaultPermissionCheck(Account authUser, Account actor, E entry,
			TechnicalAccountPermissionType permission, boolean checkActorIsEntryOwner) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, permission);
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (actor != null && authUser.equals(actor)) {
				if (checkActorIsEntryOwner) {
					return authUser.equals(getOwner(entry));
				}
				return true;
			}
		}
		return false;
	}

	protected void checkPermission(Account authUser, A actor,
			Class<?> clazz, BusinessErrorCode errCode, E entry,
			PermissionType permissionType, String logMessage,
			String exceptionMessage, Object... opt) throws BusinessException {
		if (!isAuthorized(authUser, actor, permissionType, entry, clazz,
				opt)) {
			StringBuilder sb = getAuthUserStringBuilder(authUser);
			sb.append(logMessage);
			if (entry != null) {
				String entryRepresentation = getEntryRepresentation(entry);
				sb.append(entryRepresentation);
			}
			if (actor != null) {
				sb.append(" for targeted account : ");
				sb.append(getTargetedAccountRepresentation(actor));
			}
			logger.error(sb.toString());
			throw new BusinessException(errCode, exceptionMessage);
		}
	}

	@Override
	public void checkReadPermission(Account authUser, A actor,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		String logMessage = " is not authorized to get the entry ";
		String exceptionMessage = "You are not authorized to get this entry.";
		checkPermission(authUser, actor, clazz, errCode, entry,
				PermissionType.GET, logMessage, exceptionMessage, opt);
	}

	@Override
	public void checkListPermission(Account authUser, A actor,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		String logMessage = " is not authorized to list all entries ";
		String exceptionMessage = "You are not authorized to list all entries.";
		checkPermission(authUser, actor, clazz, errCode, entry,
				PermissionType.LIST, logMessage, exceptionMessage, opt);
	}

	@Override
	public void checkCreatePermission(Account authUser, A actor,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		String logMessage = " is not authorized to create an entry ";
		String exceptionMessage = "You are not authorized to create an entry.";
		checkPermission(authUser, actor, clazz, errCode, entry,
				PermissionType.CREATE, logMessage, exceptionMessage, opt);
	}

	@Override
	public void checkUpdatePermission(Account authUser, A actor,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		String logMessage = " is not authorized to update the entry ";
		String exceptionMessage = "You are not authorized to update this entry.";
		checkPermission(authUser, actor, clazz, errCode, entry,
				PermissionType.UPDATE, logMessage, exceptionMessage, opt);
	}

	@Override
	public void checkDeletePermission(Account authUser, A actor,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		String logMessage = " is not authorized to delete the entry ";
		String exceptionMessage = "You are not authorized to delete this entry.";
		checkPermission(authUser, actor, clazz, errCode, entry,
				PermissionType.DELETE, logMessage, exceptionMessage, opt);
	}

}
