package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.rac.AnonymousShareEntryResourceAccessControl;

public class AnonymousShareEntryResourceAccessControlImpl extends
		EntryResourceAccessControlImpl<Contact, AnonymousShareEntry> implements
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

	@Override
	protected boolean hasReadPermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_GET);
	}

	@Override
	protected boolean hasListPermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_LIST);
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_UPDATE);
	}

	@Override
	protected boolean hasDownloadPermission(Account actor, Account owner,
			AnonymousShareEntry entry) {
		/*
		 * The owner has not the right to download his own anonymousShareEntry,
		 * neither account with delegation role.
		 */
		return false;
	}

	@Override
	protected boolean hasDownloadTumbnailPermission(Account actor,
			Account owner, AnonymousShareEntry entry) {
		/*
		 * The owner has not the right to download his own anonymousShareEntry,
		 * neither account with delegation role.
		 */
		return false;
	}
}
