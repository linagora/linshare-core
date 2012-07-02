package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.EntryType;

public class ThreadEntry extends Entry{

	private Document document;

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
	
	
}
