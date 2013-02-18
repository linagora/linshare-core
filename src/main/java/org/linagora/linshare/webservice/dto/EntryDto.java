package org.linagora.linshare.webservice.dto;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Entry;

@XmlRootElement(name = "Entry")
public class EntryDto {

	protected String uuid;
	
	protected String name;
	
	protected String description;
	
	protected String owner;
	
	protected Calendar creationDate;
	
	protected Calendar modificationDate;
	
	protected Calendar expirationDate;
	
	public EntryDto() {
	}
	
	public EntryDto(Entry entry) {
		if(entry==null) return;
		this.uuid = entry.getUuid();
		this.name = entry.getName();
		this.creationDate = entry.getCreationDate();
		this.modificationDate = entry.getModificationDate();
		this.expirationDate = entry.getExpirationDate();
		this.description = entry.getComment();
		this.owner = entry.getEntryOwner().getLsUuid();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Calendar getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Calendar modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}
	
}
