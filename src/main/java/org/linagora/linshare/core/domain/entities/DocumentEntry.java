package org.linagora.linshare.core.domain.entities;

import java.util.HashSet;
import java.util.Set;

public class DocumentEntry extends Entry {

	protected Document document;
	
	protected Boolean ciphered;
	
	protected Set<ShareEntry> shareEntries = new HashSet<ShareEntry>();
	
	protected Set<AnonymousShareEntry> anonymousShareEntries = new HashSet<AnonymousShareEntry>();
	
	public DocumentEntry() {
	}
	
	public DocumentEntry(Account entryOwner, String name, String comment, Document document) {
		super(entryOwner, name, comment);
		this.document = document;
		this.ciphered = false;
	}
	
	public DocumentEntry(Account entryOwner, String name, Document document) {
		super(entryOwner, name, "");
		this.document = document;
		this.ciphered = false;
	}
	
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Set<ShareEntry> getShareEntries() {
		return shareEntries;
	}

	public void setShareEntries(Set<ShareEntry> shareEntries) {
		this.shareEntries = shareEntries;
	}

	public Set<AnonymousShareEntry> getAnonymousShareEntries() {
		return anonymousShareEntries;
	}

	public void setAnonymousShareEntries(
			Set<AnonymousShareEntry> anonymousShareEntries) {
		this.anonymousShareEntries = anonymousShareEntries;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}
	
	public Boolean isShared() {
		if(getAnonymousShareEntries().size() > 0 || getShareEntries().size() > 0) {
			return true;
		}
		return false;
	}
	
}
