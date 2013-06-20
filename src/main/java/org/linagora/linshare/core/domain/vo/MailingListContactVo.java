package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.MailingListContact;

public class MailingListContactVo {

	private long persistenceId;
	private String mail;
	
	public MailingListContactVo(){
	}
	
	public MailingListContactVo(MailingListContactVo MailingListContact) {
		this.mail = MailingListContact.getMails();
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

	public String getMails() {
		return mail;
	}

	public void setMails(String mail) {
		this.mail = mail;
	}
}
