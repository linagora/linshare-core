/**
 * "Visual Paradigm: DO NOT MODIFY THIS FILE!"
 * 
 * This is an automatic generated file. It will be regenerated every time 
 * you generate persistence class.
 * 
 * Modifying its content may cause the program not work, or your work may lost.
 */

/**
 * Licensee: MAUDET Michel-Marie (LINAGORA)
 * License Type: Purchased
 */
package org.linagora.linshare.core.domain.entities;

public class MailContentLang {
	public MailContentLang() {
	}
	
	private long id;
	
	private int language;
	
	private org.linagora.linshare.core.domain.entities.MailContent mailContent;
	
	private int mailContentType;
	
	private MailConfig mailConfig;
	
	private void setId(long value) {
		this.id = value;
	}
	
	public long getId() {
		return id;
	}
	
	public long getORMID() {
		return getId();
	}
	
	public void setLanguage(int value) {
		this.language = value;
	}
	
	public int getLanguage() {
		return language;
	}
	
	public void setMailContentType(int value) {
		this.mailContentType = value;
	}
	
	public int getMailContentType() {
		return mailContentType;
	}
	
	public void setMailContent(org.linagora.linshare.core.domain.entities.MailContent value) {
		this.mailContent = value;
	}
	
	public org.linagora.linshare.core.domain.entities.MailContent getMailContent() {
		return mailContent;
	}
	
	public String toString() {
		return String.valueOf(getId());
	}

	public MailConfig getMailConfig() {
		return mailConfig;
	}

	public void setMailConfig(MailConfig mailConfig) {
		this.mailConfig = mailConfig;
	}
	
}
