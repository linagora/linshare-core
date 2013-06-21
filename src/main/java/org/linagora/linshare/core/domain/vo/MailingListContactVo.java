package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.MailingListContact;

public class MailingListContactVo {

	private long persistenceId;
	private String mail;
	
	public MailingListContactVo(){
	}
	
	public MailingListContactVo(MailingListContactVo MailingListContact) {
		this.mail = MailingListContact.getMail();
		this.persistenceId = MailingListContact.getPersistenceId();
	}
	
	public MailingListContactVo(MailingListContact mail) {
		this.persistenceId = mail.getPersistenceId();
		this.mail = mail.getMails();
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

	public void setMail(String mail) {
		this.mail = mail;
	}
}
