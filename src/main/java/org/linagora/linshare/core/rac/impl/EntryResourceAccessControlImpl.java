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

	protected abstract boolean hasDownloadPermission(Account actor,
			Account account, E entry, Object... opt);

	protected abstract boolean hasDownloadTumbnailPermission(Account actor,
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
	protected boolean isAuthorized(Account actor, Account targetedAccount,
			PermissionType permission, E entry, Class<?> clazz, Object... opt) {
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
		} else if (permission.equals(PermissionType.DOWNLOAD)) {
			if (hasDownloadPermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DOWNLOAD_THUMBNAIL)) {
			if (hasDownloadTumbnailPermission(actor, targetedAccount, entry,
					opt))
				return true;
		}
		if (clazz != null) {
			StringBuilder sb = getActorStringBuilder(actor);
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
	public void checkDownloadPermission(Account actor, Account targetedAccount,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		String logMessage = " is not authorized to download the entry ";
		String exceptionMessage = "You are not authorized to download this entry.";
		checkPermission(actor, targetedAccount, clazz, errCode, entry,
				PermissionType.DOWNLOAD, logMessage, exceptionMessage, opt);
	}

	@Override
	public void checkThumbNailDownloadPermission(Account actor,
			Account targetedAccount, Class<?> clazz, BusinessErrorCode errCode,
			E entry, Object... opt) throws BusinessException {
		String logMessage = " is not authorized to get the thumbnail of the entry ";
		String exceptionMessage = "You are not authorized to get the thumbnail of this entry.";
		checkPermission(actor, targetedAccount, clazz, errCode, entry,
				PermissionType.DOWNLOAD_THUMBNAIL, logMessage,
				exceptionMessage, opt);
	}
}
