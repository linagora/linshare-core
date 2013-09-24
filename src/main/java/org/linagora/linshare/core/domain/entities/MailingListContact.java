package org.linagora.linshare.core.domain.entities;

import java.util.Date;

import org.linagora.linshare.core.domain.vo.MailingListContactVo;

public class MailingListContact {

	private long persistenceId;

	private String uuid;

	private String mail;

	private String firstName;
	
	private String lastName;

	protected Date creationDate;

	protected Date modificationDate;

	/**
	 * Constructors
	 */
	
	/**
	 * Hibernate constructor.
	 */
	@SuppressWarnings("unused")
	private MailingListContact() {
	}

	public MailingListContact(String mail, String firstName, String lastName) {
		this.mail = mail;
		this.setLastName(lastName);
		this.setFirstName(firstName);
	}
	
	public MailingListContact(MailingListContactVo mailingListContact)  {
		this.mail = mailingListContact.getMail();
		this.uuid = mailingListContact.getUuid();
		this.setLastName(mailingListContact.getLastName());
		this.setFirstName(mailingListContact.getFirstName());
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/**
	 * Helpers
	 */
	
}
