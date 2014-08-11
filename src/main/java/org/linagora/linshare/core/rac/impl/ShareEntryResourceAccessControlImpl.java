package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.rac.ShareEntryResourceAccessControl;

public class ShareEntryResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, ShareEntry> implements
		ShareEntryResourceAccessControl {

	public ShareEntryResourceAccessControlImpl() {
		super();
	}

	@Override
	protected String getEntryRepresentation(ShareEntry entry) {
		StringBuilder sb = new StringBuilder(entry.getEntryType().toString());
		sb.append(" (");
		sb.append(entry.getUuid());
		sb.append(") ");
		return sb.toString();
	}

	@Override
	protected Account getOwner(ShareEntry entry) {
		Account owner = entry.getEntryOwner();
		return owner;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountReprentation();
	}

	@Override
	protected Account getRecipient(ShareEntry entry) {
		if (entry != null) {
			return entry.getRecipient();
		}
		return null;
	}

	@Override
	protected String getRecipientRepresentation(ShareEntry entry) {
		User recipient = entry.getRecipient();
		return recipient.getAccountReprentation();
	}

	@Override
	protected boolean hasReadPermission(Account actor) {
		return hasPermission(actor, TechnicalAccountPermissionType.SHARES_GET);
	}

	@Override
	protected boolean hasListPermission(Account actor) {
		return this.hasPermission(actor,
				TechnicalAccountPermissionType.SHARES_LIST);
	}

	@Override
	protected boolean hasDeletePermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.SHARES_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.SHARES_CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.SHARES_UPDATE);
	}
}
