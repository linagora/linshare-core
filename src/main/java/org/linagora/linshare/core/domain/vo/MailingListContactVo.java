package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.MailingListContact;

public class MailingListContactVo {

	private String mail;
	
	private String uuid;
	
	private String firstName;
	
	private String lastName;
	
	private String mailingListUuid;

	public MailingListContactVo() {
	}

	public MailingListContactVo(MailingListContactVo mailingListContact) {
		this.mail = mailingListContact.getMail();
		this.uuid = mailingListContact.getUuid();
		this.lastName = mailingListContact.getLastName();
		this.firstName = mailingListContact.getFirstName();
		this.mailingListUuid = mailingListContact.getMailingListUuid();
	}

	public MailingListContactVo(MailingListContact mailingListContact) {
		this.mail = mailingListContact.getMail();
		this.uuid = mailingListContact.getUuid();
		this.lastName = mailingListContact.getLastName();
		this.firstName = mailingListContact.getFirstName();
		this.mailingListUuid = mailingListContact.getUuid();
	}

	public MailingListContactVo(String mail, String firstName, String lastName) {
		this.mail = mail;
		this.lastName = lastName;
		this.firstName = firstName;
	}

	public MailingListContactVo(String mail) {
		this.mail = mail;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMailingListUuid() {
		return mailingListUuid;
	}

	public void setMailingListUuid(String mailingListUuid) {
		this.mailingListUuid = mailingListUuid;
	}
}
