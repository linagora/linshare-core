package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.EntryType;

public class ThreadEntry extends Entry{

	protected Document document;
	
	protected Boolean ciphered;

	
	public ThreadEntry() {
		super();
	}
	
	public ThreadEntry(Account entryOwner, String name, Document document) {
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

	@Override
	public EntryType getEntryType() {
		return EntryType.THREAD;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}
}
