package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.vo.MailingListContactVo;

public class MailingListContact {

	private long persistenceId;

	private String uuid;

	private String mail;

	private String display;

	/**
	 * Constructors
	 */
	
	/**
	 * Hibernate constructor.
	 */
	@SuppressWarnings("unused")
	private MailingListContact() {
	}

	public MailingListContact(String mail, String display) {
		this.mail = mail;
		this.display = display;
	}
	
	public MailingListContact(MailingListContactVo mailingListContact) {
		this.mail = mailingListContact.getMail();
		this.display = mailingListContact.getDisplay();
		this.uuid = mailingListContact.getUuid();
	}

	/**
	 * Getter(s) / Setter(s)
	 */

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailingListContact other = (MailingListContact) obj;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		return true;
	}

	/**
	 * Helpers
	 */
	
}
