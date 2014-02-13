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

public class MailFooterLang {
	public MailFooterLang() {
	}
	
	private long id;
	
	private int language;
	
	private org.linagora.linshare.core.domain.entities.MailFooter footer;
	
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
	
	public void setFooter(org.linagora.linshare.core.domain.entities.MailFooter value) {
		this.footer = value;
	}
	
	public org.linagora.linshare.core.domain.entities.MailFooter getFooter() {
		return footer;
	}
	
	public String toString() {
		return String.valueOf(getId());
	}
}
