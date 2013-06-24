package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.vo.MailingListContactVo;

public class MailingListContact {

	private long persistenceId;
	private String mail;
	
	public MailingListContact(){
		
	}
	
	public MailingListContact(MailingListContact mailingListContact) {
		this.mail = mailingListContact.getMails();
		this.persistenceId = mailingListContact.getPersistenceId();
	}
	
	public MailingListContact(MailingListContactVo mailingListContact){
		this.mail = mailingListContact.getMail();
		this.persistenceId = mailingListContact.getPersistenceId();
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

	public String getMails() {
		return mail;
	}

	public void setMails(String mail) {
		this.mail = mail;
	}
}
