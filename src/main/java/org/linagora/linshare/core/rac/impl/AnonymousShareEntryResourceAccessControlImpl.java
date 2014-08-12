package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.rac.AnonymousShareEntryResourceAccessControl;

public class AnonymousShareEntryResourceAccessControlImpl extends
		EntryResourceAccessControlImpl<AnonymousShareEntry> implements
		AnonymousShareEntryResourceAccessControl {

	public AnonymousShareEntryResourceAccessControlImpl() {
		super();
	}

	@Override
	protected String getEntryRepresentation(AnonymousShareEntry entry) {
		StringBuilder sb = new StringBuilder(entry.getEntryType().toString());
		sb.append(" (");
		sb.append(entry.getUuid());
		sb.append(") ");
		return sb.toString();
	}

	@Override
	protected Account getOwner(AnonymousShareEntry entry) {
		Account owner = entry.getEntryOwner();
		return owner;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountReprentation();
	}

//	@Override
//	protected Account getRecipient(AnonymousShareEntry entry) {
//		if (entry != null) {
//			return entry.getRecipient();
//		}
//		return null;
//	}
//
//	@Override
//	protected String getRecipientRepresentation(AnonymousShareEntry entry) {
//		User recipient = entry.getRecipient();
//		return recipient.getAccountReprentation();
//	}

	@Override
	protected boolean hasReadPermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.SHARES_GET);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * The owner has the right to read his own shareEntry, and the
			 * recipient has the right to read his received shareEntry because
			 * it is same shared entity.
			 */
			if (owner != null && actor.equals(owner)) {
				return true;
			}
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.SHARES_LIST);
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.SHARES_DELETE);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * The owner has the right to delete his own shareEntry, and the
			 * recipient has the right to delete his received shareEntry because
			 * it is same shared entity.
			 */
			if (owner != null && actor.equals(owner)) {
				return true;
			}
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.SHARES_CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.SHARES_UPDATE);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * The owner has the right to update his own shareEntry, and the
			 * recipient has the right to update his received shareEntry because
			 * it is same shared entity.
			 */
			if (owner != null && actor.equals(owner)) {
				return true;
			}
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDownloadPermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.SHARES_DOWNLOAD);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * Only the recipient has the right to download his received
			 * shareEntry. It makes no sense for the owner.
			 */
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDownloadTumbnailPermission(Account actor,
			Account owner, AnonymousShareEntry entry) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.SHARES_DOWNLOAD_THUMBNAIL);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * Only the recipient has the right to download the thumb nail of
			 * his received shareEntry. It makes no sense for the owner.
			 */
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}
}
