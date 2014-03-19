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

import java.util.Date;

public class MailFooter {

	private long id;

	private String name;

	private AbstractDomain domain;

	private boolean visible;

	private int language;

	private String footer;

	private Date creationDate;

	private Date modificationDate;

	private String uuid;

	private boolean plaintext;

	public MailFooter() {
	}

	private void setId(long value) {
		this.id = value;
	}

	public long getId() {
		return id;
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getName() {
		return name;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public void setVisible(boolean value) {
		this.visible = value;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setLanguage(int value) {
		this.language = value;
	}

	public int getLanguage() {
		return language;
	}

	public void setFooter(String value) {
		this.footer = value;
	}

	public String getFooter() {
		return footer;
	}

	public void setCreationDate(Date value) {
		this.creationDate = value;
	}

	public java.util.Date getCreationDate() {
		return creationDate;
	}

	public void setModificationDate(Date value) {
		this.modificationDate = value;
	}

	public java.util.Date getModificationDate() {
		return modificationDate;
	}

	public void setUuid(String value) {
		this.uuid = value;
	}

	public String getUuid() {
		return uuid;
	}

	public void setPlaintext(boolean value) {
		this.plaintext = value;
	}

	public boolean getPlaintext() {
		return plaintext;
	}

	public String toString() {
		return String.valueOf(getId());
	}

}
