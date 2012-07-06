package org.linagora.linshare.core.domain.entities;

import java.util.Calendar;

import org.linagora.linshare.core.domain.constants.EntryType;

/**
 * @author fred
 */
public class AnonymousShareEntry extends Entry{

	private Long downloaded;
	
	private DocumentEntry documentEntry;
	
	private AnonymousUrl anonymousUrl;
	
	private Contact contact;
	
	
	public AnonymousShareEntry() {
		super();
	}

	public AnonymousShareEntry(Account entryOwner, String name, String comment, DocumentEntry documentEntry, AnonymousUrl anonymousUrl, Contact contact , Calendar expirationDate) {
		super(entryOwner, name, comment);
		this.documentEntry = documentEntry;
		this.anonymousUrl = anonymousUrl;
		this.contact = contact;
		this.downloaded = new Long(0);
		this.expirationDate = expirationDate;
	}

	@Override
	public EntryType getEntryType() {
		return EntryType.ANONYMOUS_SHARE;
	}

	public Long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Long downloaded) {
		this.downloaded = downloaded;
	}

	public DocumentEntry getDocumentEntry() {
		return documentEntry;
	}

	public void setDocumentEntry(DocumentEntry documentEntry) {
		this.documentEntry = documentEntry;
	}

	public AnonymousUrl getAnonymousUrl() {
		return anonymousUrl;
	}

	public void setAnonymousUrl(AnonymousUrl anonymousUrl) {
		this.anonymousUrl = anonymousUrl;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
}
