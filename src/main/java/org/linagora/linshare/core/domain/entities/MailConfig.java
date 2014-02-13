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

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MailConfig {
	public MailConfig() {
	}
	
	private long id;
	
	private MailLayout mailLayoutHtml;
	
	private AbstractDomain domain;
	
	private String name;
	
	private boolean visible;
	
	private org.linagora.linshare.core.domain.entities.MailLayout mailLayoutText;
	
	private String uuid;
	
	private Map<Integer,MailFooterLang> mailFooters = Maps.newHashMap();
	
	private Set<MailContentLang> mailContents = Sets.newHashSet();
	
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
	
	public void setVisible(boolean value) {
		this.visible = value;
	}
	
	public boolean getVisible() {
		return visible;
	}
	
	public void setUuid(String value) {
		this.uuid = value;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setDomain(org.linagora.linshare.core.domain.entities.AbstractDomain value) {
		this.domain = value;
	}
	
	public org.linagora.linshare.core.domain.entities.AbstractDomain getDomain() {
		return domain;
	}
	
	public void setMailFooters(Map<Integer,MailFooterLang> value) {
		this.mailFooters = value;
	}
	
	public Map<Integer,MailFooterLang> getMailFooters() {
		return mailFooters;
	}
	
	
	public void setMailLayoutHtml(org.linagora.linshare.core.domain.entities.MailLayout value) {
		this.mailLayoutHtml = value;
	}
	
	public org.linagora.linshare.core.domain.entities.MailLayout getMailLayoutHtml() {
		return mailLayoutHtml;
	}
	
	public void setMailLayoutText(org.linagora.linshare.core.domain.entities.MailLayout value) {
		this.mailLayoutText = value;
	}
	
	public org.linagora.linshare.core.domain.entities.MailLayout getMailLayoutText() {
		return mailLayoutText;
	}
	
	public String toString() {
		return String.valueOf(getId());
	}

	public java.util.Set<MailContentLang> getMailContents() {
		return mailContents;
	}

	public void setMailContents(java.util.Set<MailContentLang> mailContents) {
		this.mailContents = mailContents;
	}
	
	
	
}
