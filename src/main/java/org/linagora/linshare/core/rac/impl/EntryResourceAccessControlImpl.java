package org.linagora.linshare.core.rac.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.EntryResourceAccessControl;

public abstract class EntryResourceAccessControlImpl<R, E extends Entry> extends
		AbstractResourceAccessControlImpl<Account, R, E> implements
		EntryResourceAccessControl<R, E> {

	protected abstract boolean hasDownloadPermission(Account actor,
			Account owner, E entry);

	protected abstract boolean hasDownloadTumbnailPermission(Account actor,
			Account owner, E entry);

	@Override
	protected String getEntryRepresentation(E entry) {
		StringBuilder sb = new StringBuilder(entry.getEntryType().toString());
		sb.append(" (");
		sb.append(entry.getUuid());
		sb.append(") ");
		return sb.toString();
	}

	@Override
	protected Account getOwner(Entry entry) {
		Account owner = entry.getEntryOwner();
		return owner;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountReprentation();
	}

	@Override
	protected boolean isAuthorized(Account actor, Account owner,
			PermissionType permission, E entry, String resourceName) {
		Validate.notNull(permission);
		if (actor.hasSuperAdminRole()) {
			return true;
		} else if (actor.hasSystemAccountRole()) {
			return true;
		} else {
			if (permission.equals(PermissionType.GET)) {
				if (hasReadPermission(actor, owner, entry))
					return true;
			} else if (permission.equals(PermissionType.LIST)) {
				if (hasListPermission(actor, owner, entry))
					return true;
			} else if (permission.equals(PermissionType.CREATE)) {
				if (hasCreatePermission(actor, owner, entry))
					return true;
			} else if (permission.equals(PermissionType.UPDATE)) {
				if (hasUpdatePermission(actor, owner, entry))
					return true;
			} else if (permission.equals(PermissionType.DELETE)) {
				if (hasDeletePermission(actor, owner, entry))
					return true;
			} else if (permission.equals(PermissionType.DOWNLOAD)) {
				if (hasDownloadPermission(actor, owner, entry))
					return true;
			} else if (permission.equals(PermissionType.DOWNLOAD_THUMBNAIL)) {
				if (hasDownloadTumbnailPermission(actor, owner, entry))
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

	@Override
	public void checkDownloadPermission(Account actor, E entry,
			BusinessErrorCode errCode) throws BusinessException {
		Account owner = getOwner(entry);
		if (!isAuthorized(actor, owner, PermissionType.DOWNLOAD, entry)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to download the entry ");
			sb.append(getEntryRepresentation(entry));
			appendOwner(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to download this entry.");
		}
	}

	@Override
	public void checkThumbNailDownloadPermission(Account actor, E entry,
			BusinessErrorCode errCode) throws BusinessException {
		Account owner = getOwner(entry);
		if (!isAuthorized(actor, owner, PermissionType.DOWNLOAD_THUMBNAIL,
				entry)) {
			StringBuilder sb = getActorStringBuilder(actor);
			sb.append(" is not authorized to get the thumbnail of the entry ");
			sb.append(getEntryRepresentation(entry));
			appendOwner(owner, sb);
			logger.error(sb.toString());
			throw new BusinessException(errCode,
					"You are not authorized to get the thumbnail of this entry.");
		}
	}
}
