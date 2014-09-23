/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.util.Iterator;

import org.apache.commons.lang.Validate;
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

public abstract class AbstractResourceAccessControlImpl<O, R, E> implements
		AbstractResourceAccessControl<O, R, E> {

	protected static Logger logger = LoggerFactory
			.getLogger(AbstractResourceAccessControlImpl.class);

	protected abstract boolean hasReadPermission(Account actor, O owner,
			E entry, Object... opt);

	protected abstract boolean hasListPermission(Account actor, O owner,
			E entry, Object... opt);

	protected abstract boolean hasDeletePermission(Account actor, O owner,
			E entry, Object... opt);

	protected abstract boolean hasCreatePermission(Account actor, O owner,
			E entry, Object... opt);

	protected abstract boolean hasUpdatePermission(Account actor, O owner,
			E entry, Object... opt);

	protected O getOwner(E entry) {
		return null;
	}

	protected String getOwnerRepresentation(O owner) {
		return null;
	}

	protected R getRecipient(E entry) {
		return null;
	}

	protected String getRecipientRepresentation(E entry) {
		return null;
	}

	protected abstract String getEntryRepresentation(E entry);

	protected boolean isAuthorized(Account actor, O owner,
			PermissionType permission, Object... opt) {
		return this.isAuthorized(actor, owner, permission, null, null, opt);
	}

	protected boolean isAuthorized(Account actor, O owner,
			PermissionType permission, E entry, Object... opt) {
		return this.isAuthorized(actor, owner, permission, entry, null, opt);
	}

	protected void appendOwner(O owner, StringBuilder sb) {
		if (owner != null) {
			String or = getOwnerRepresentation(owner);
			if (or != null) {
				sb.append(" owned by : ");
				sb.append(or);
			}
		}
	}

	protected void appendForAccount(O owner, StringBuilder sb) {
		if (owner != null) {
			String or = getOwnerRepresentation(owner);
			if (or != null) {
				sb.append(" for account : ");
				sb.append(or);
			}
		}
	}

	protected StringBuilder getActorStringBuilder(Account actor) {
		StringBuilder sb = new StringBuilder("Actor ");
		sb.append(actor.getAccountReprentation());
		return sb;
	}

	protected boolean isAuthorized(Account actor, O owner,
			PermissionType permission, E entry, String resourceName,
			Object... opt) {
		Validate.notNull(permission);

		if (actor.hasSuperAdminRole() && actor.hasSystemAccountRole())
			return true;
		if (permission.equals(PermissionType.GET)) {
			if (hasReadPermission(actor, owner, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.LIST)) {
			if (hasListPermission(actor, owner, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.CREATE)) {
			if (hasCreatePermission(actor, owner, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.UPDATE)) {
			if (hasUpdatePermission(actor, owner, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DELETE)) {
			if (hasDeletePermission(actor, owner, entry, opt))
				return true;
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
		TechnicalAccountPermission p = actor.getPermission();
		boolean contains = false;

		Iterator<AccountPermission> it = p.getAccountPermissions().iterator();
		while (!contains && it.hasNext())
			contains = it.next().getPermission().equals(p);
		logger.debug(permissionType.toString() + " : " + contains);
		return contains;
	}

	/**
	 * Only the entry owner has all rights (create, read, list, update, delete,
	 * download and thumb nail download.
	 * 
	 * @param actor
	 * @param owner
	 * @param entry
	 * @param permission
	 * @return
	 */
	protected boolean defaultPermissionCheck(Account actor, Account owner,
			E entry, TechnicalAccountPermissionType permission) {
		if (actor.hasDelegationRole())
			return hasPermission(actor, permission);
		if (actor.isInternal() || actor.isGuest())
			return (owner != null && actor.equals(owner));
		return false;
	}

	@Override
	public void checkReadPermission(Account actor, O owner, E entry,
			BusinessErrorCode errCode, Object... opt) throws BusinessException {
		if (owner == null)
			owner = getOwner(entry);
		if (!isAuthorized(actor, owner, PermissionType.GET, entry, opt)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to get the entry ");
			sb.append(getEntryRepresentation(entry));
			appendOwner(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to get this entry.");
		}
	}

	@Override
	public void checkListPermission(Account actor, O owner, Class<?> clazz,
			BusinessErrorCode errCode, Object... opt) throws BusinessException {
		if (!isAuthorized(actor, owner, PermissionType.LIST, opt)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to list all entries ");
			sb.append(clazz.getSimpleName());
			appendOwner(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to list all entries.");
		}
	}

	@Override
	public void checkCreatePermission(Account actor, O owner, Class<?> clazz,
			BusinessErrorCode errCode, Object... opt) throws BusinessException {
		if (!isAuthorized(actor, owner, PermissionType.CREATE, opt)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to create entry ");
			sb.append(clazz.getSimpleName());
			appendForAccount(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to create entry.");
		}
	}

	@Override
	public void checkUpdatePermission(Account actor, E entry,
			BusinessErrorCode errCode, Object... opt) throws BusinessException {
		O owner = getOwner(entry);
		if (!isAuthorized(actor, owner, PermissionType.UPDATE, entry, opt)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to create entry ");
			sb.append(getEntryRepresentation(entry));
			appendForAccount(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to update this entry.");
		}
	}

	@Override
	public void checkDeletePermission(Account actor, O owner, E entry,
			BusinessErrorCode errCode, Object... opt) throws BusinessException {
		if (owner == null)
			owner = getOwner(entry);
		if (!isAuthorized(actor, owner, PermissionType.DELETE, entry, opt)) {
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
