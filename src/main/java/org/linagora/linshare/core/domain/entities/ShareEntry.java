package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.EntryType;

/**
 * @author fred
 *
 */
public class ShareEntry extends Entry{

	protected User recipient;
	
	protected DocumentEntry documentEntry;
	
	protected Long downloaded;

	
	public ShareEntry() {
		super();
	}

	public ShareEntry(Account entryOwner, String name, String comment, User recipient, DocumentEntry documentEntry) {
		super(entryOwner, name, comment);
		this.recipient = recipient;
		this.documentEntry = documentEntry;
		this.downloaded = new Long(0);
	}

	@Override
	public EntryType getEntryType() {
		return EntryType.SHARE;
	}

	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	public DocumentEntry getDocumentEntry() {
		return documentEntry;
	}

	public void setDocumentEntry(DocumentEntry documentEntry) {
		this.documentEntry = documentEntry;
	}

	public Long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Long downloaded) {
		this.downloaded = downloaded;
	}
	
	
}
