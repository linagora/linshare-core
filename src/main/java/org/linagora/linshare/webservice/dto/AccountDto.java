package org.linagora.linshare.webservice.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Account;

@XmlRootElement(name = "Account")
public class AccountDto {

	protected String uuid;
	
	protected Date creationDate;
	
	protected Date modificationDate;
	
	protected String locale;
	
	protected String domain;
	
	protected String owner;

	public AccountDto() {
	}
	
	public AccountDto(Account a) {
		this.uuid = a.getLsUuid();
		this.creationDate = a.getCreationDate();
		this.modificationDate = a.getModificationDate();
		this.locale = a.getLocale();
		this.domain = a.getDomainId();
		if(a.getOwner() != null) {
			this.owner = a.getOwner().getLsUuid();
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
