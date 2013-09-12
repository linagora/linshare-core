package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.MailingListContact;

public class MailingListContactVo {

	private String mail;
	private String display;

	public MailingListContactVo() {
	}

	public MailingListContactVo(MailingListContactVo mailingListContact) {
		this.mail = mailingListContact.getMail();
		this.display = mailingListContact.getDisplay();
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
}
