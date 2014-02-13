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

import java.util.Set;

import com.google.common.collect.Sets;

public class MailContent {
	public MailContent() {
	}

	private long id;

	private String name;

	private AbstractDomain domain;

	private boolean visible;

	private int mailContentType;

	private int language;

	private String subject;

	private String greetings;

	private String body;

	private String uuid;

	private boolean plaintext;

	private Set<MailContentLang> mailConfiguration = Sets.newHashSet();

	private void setId(long value) {
		this.id = value;
	}

	public long getId() {
		return id;
	}

	public long getORMID() {
		return getId();
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

	public void setMailContentType(int value) {
		this.mailContentType = value;
	}

	public int getMailContentType() {
		return mailContentType;
	}

	public void setLanguage(int value) {
		this.language = value;
	}

	public int getLanguage() {
		return language;
	}

	public void setSubject(String value) {
		this.subject = value;
	}

	public String getSubject() {
		return subject;
	}

	public void setGreetings(String value) {
		this.greetings = value;
	}

	public String getGreetings() {
		return greetings;
	}

	public void setBody(String value) {
		this.body = value;
	}

	public String getBody() {
		return body;
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

	public void setMailConfiguration(Set<MailContentLang> value) {
		this.mailConfiguration = value;
	}

	public Set<MailContentLang> getMailConfiguration() {
		return mailConfiguration;
	}

	public String toString() {
		return String.valueOf(getId());
	}
}
