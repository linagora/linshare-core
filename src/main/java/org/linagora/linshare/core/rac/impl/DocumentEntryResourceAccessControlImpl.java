package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.rac.DocumentEntryResourceAccessControl;

public class DocumentEntryResourceAccessControlImpl extends
		EntryResourceAccessControlImpl<Account, DocumentEntry> implements
		DocumentEntryResourceAccessControl {

	public DocumentEntryResourceAccessControlImpl() {
		super();
	}

	@Override
	protected String getEntryRepresentation(DocumentEntry entry) {
		StringBuilder sb = new StringBuilder(entry.getEntryType().toString());
		sb.append(" (");
		sb.append(entry.getUuid());
		sb.append(") ");
		return sb.toString();
	}

	@Override
	protected Account getOwner(DocumentEntry entry) {
		Account owner = entry.getEntryOwner();
		return owner;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountReprentation();
	}


	@Override
	protected boolean hasReadPermission(Account actor, Account owner, DocumentEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_GET);
	}

	@Override
	protected boolean hasListPermission(Account actor, Account owner, DocumentEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_LIST);
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account owner, DocumentEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_CREATE);
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account owner, DocumentEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account owner, DocumentEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_UPDATE);
	}

	@Override
	protected boolean hasDownloadPermission(Account actor, Account owner,
			DocumentEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_DOWNLOAD);
	}

	@Override
	protected boolean hasDownloadTumbnailPermission(Account actor,
			Account owner, DocumentEntry entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_DOWNLOAD_THUMBNAIL);
	}
}
