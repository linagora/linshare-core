package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.MailingListContact;

public class MailingListContactVo {

	private String mail;
	
	private String display;
	
	private String uuid;
	
	private String firstName;
	
	private String lastName;

	public MailingListContactVo() {
	}

	public MailingListContactVo(MailingListContactVo mailingListContact) {
		this.mail = mailingListContact.getMail();
		this.display = mailingListContact.getDisplay();
		this.uuid = mailingListContact.getUuid();
		this.lastName = mail;
		this.firstName = mail;
	}

	public MailingListContactVo(MailingListContact mail) {
		this.mail = mail.getMail();
		this.display = mail.getDisplay();
	}

	public MailingListContactVo(String mail, String display) {
		this.mail = mail;
		this.display = display;
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

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
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
}
