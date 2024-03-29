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
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.EntryResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public abstract class EntryResourceAccessControlImpl<R, E extends Entry>
		extends AbstractResourceAccessControlImpl<Account, R, E> implements
		EntryResourceAccessControl<R, E> {

	public EntryResourceAccessControlImpl(
			FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	protected abstract boolean hasDownloadPermission(Account authUser,
			Account account, E entry, Object... opt);

	protected abstract boolean hasDownloadTumbnailPermission(Account authUser,
			Account account, E entry, Object... opt);

	@Override
	protected String getEntryRepresentation(E entry) {
		StringBuilder sb = new StringBuilder(entry.getEntryType().toString());
		sb.append(" (");
		sb.append(entry.getUuid());
		sb.append(") ");
		return sb.toString();
	}

	@Override
	protected Account getOwner(Entry entry, Object... opt) {
		Account owner = entry.getEntryOwner();
		return owner;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountRepresentation();
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected boolean isAuthorized(Account authUser, Account targetedAccount,
			PermissionType permission, E entry, Class<?> clazz, Object... opt) {
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
		} else if (permission.equals(PermissionType.DOWNLOAD)) {
			if (hasDownloadPermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DOWNLOAD_THUMBNAIL)) {
			if (hasDownloadTumbnailPermission(authUser, targetedAccount, entry,
					opt))
				return true;
		}
		if (clazz != null) {
			StringBuilder sb = getAuthUserStringBuilder(authUser);
			sb.append(" is trying to access to unauthorized resource named ");
			sb.append(clazz.toString());
			if (entry != null) {
				appendOwner(sb, entry, opt);
			}
			logger.error(sb.toString());
		}
		return false;
	}

	@Override
	public void checkDownloadPermission(Account authUser, Account targetedAccount,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		String logMessage = " is not authorized to download the entry ";
		String exceptionMessage = "You are not authorized to download this entry.";
		checkPermission(authUser, targetedAccount, clazz, errCode, entry,
				PermissionType.DOWNLOAD, logMessage, exceptionMessage, opt);
	}

	@Override
	public void checkThumbNailDownloadPermission(Account authUser,
			Account targetedAccount, Class<?> clazz, BusinessErrorCode errCode,
			E entry, Object... opt) throws BusinessException {
		String logMessage = " is not authorized to get the thumbnail of the entry ";
		String exceptionMessage = "You are not authorized to get the thumbnail of this entry.";
		checkPermission(authUser, targetedAccount, clazz, errCode, entry,
				PermissionType.DOWNLOAD_THUMBNAIL, logMessage,
				exceptionMessage, opt);
	}
}
