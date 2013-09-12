package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.vo.MailingListContactVo;

public class MailingListContact {

	private long persistenceId;
	private String mail;
	private String display;
	
	public MailingListContact(){
		
	}
	
	public MailingListContact(MailingListContact mailingListContact) {
		this.mail = mailingListContact.getMail();
		this.persistenceId = mailingListContact.getPersistenceId();
		this.display = mailingListContact.getDisplay();
	}
	
	public MailingListContact(MailingListContactVo mailingListContact){
		this.mail = mailingListContact.getMail();
		this.display = mailingListContact.getDisplay();
	}
	
	public MailingListContact(String mail) {
		this.mail = mail;
	}
	
	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public String getMail() {
		return mail;
	}

	public void setMails(String mail) {
		this.mail = mail;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
}
