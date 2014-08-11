package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.DocumentEntryResourceAccessControl;

public class DocumentEntryResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, DocumentEntry> implements
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
	protected boolean hasReadPermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_GET);
	}

	@Override
	protected boolean hasListPermission(Account actor) {
		return this.hasPermission(actor,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_LIST);
	}

	@Override
	protected boolean hasDeletePermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account actor) {
		return hasPermission(actor,
				TechnicalAccountPermissionType.DOCUMENT_ENTRIES_UPDATE);
	}

	@Override
	public void checkDownloadPermission(Account actor, DocumentEntry entry)
			throws BusinessException {
		if (actor.hasDelegationRole()) {
			if (!hasPermission(actor,
					TechnicalAccountPermissionType.DOCUMENTS_GET)) {
				Account owner = entry.getEntryOwner();
				logger.error("Current actor " + actor.getAccountReprentation()
						+ " is trying to download document entry ("
						+ entry.getUuid() + ") owned by : "
						+ owner.getAccountReprentation());
				throw new BusinessException(
						BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN,
						"You are not authorized to download this document.");
			}
		}
	}

	@Override
	public void checkThumbNailDownloadPermission(Account actor,
			DocumentEntry entry) throws BusinessException {
		if (actor.hasDelegationRole()) {
			if (!hasPermission(actor,
					TechnicalAccountPermissionType.DOCUMENTS_GET)) {
				Account owner = entry.getEntryOwner();
				logger.error("Current actor " + actor.getAccountReprentation()
						+ " is trying to download document entry ("
						+ entry.getUuid() + ") owned by : "
						+ owner.getAccountReprentation());
				throw new BusinessException(
						BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN,
						"You are not authorized to download the thumbnail of this document.");
			}
		}
	}
}
