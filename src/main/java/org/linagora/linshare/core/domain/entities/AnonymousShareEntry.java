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
	
	
	public AnonymousShareEntry() {
		super();
	}

	public AnonymousShareEntry(Account entryOwner, String name, String comment, DocumentEntry documentEntry, AnonymousUrl anonymousUrl, Calendar expirationDate) {
		super(entryOwner, name, comment);
		this.documentEntry = documentEntry;
		this.anonymousUrl = anonymousUrl;
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
	
	public void incrementDownload() {
		downloaded+=1;
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

	/* usefull getters */
	public long getSize() {
		return documentEntry.getSize();
	}
	
	public String getType() {
		return documentEntry.getType();
	}
	
}
