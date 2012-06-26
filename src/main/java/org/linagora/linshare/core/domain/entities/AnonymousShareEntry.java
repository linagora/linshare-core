package org.linagora.linshare.core.domain.entities;

public class AnonymousShareEntry extends Entry{

	private String mail;
	
	private long downloaded;
	
	private DocumentEntry documentEntry;
	
	private SecuredUrl securedUrl;

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(long downloaded) {
		this.downloaded = downloaded;
	}

	public DocumentEntry getDocumentEntry() {
		return documentEntry;
	}

	public void setDocumentEntry(DocumentEntry documentEntry) {
		this.documentEntry = documentEntry;
	}

	public SecuredUrl getSecuredUrl() {
		return securedUrl;
	}

	public void setSecuredUrl(SecuredUrl securedUrl) {
		this.securedUrl = securedUrl;
	}
	
}
