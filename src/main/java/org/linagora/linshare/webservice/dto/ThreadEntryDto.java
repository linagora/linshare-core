package org.linagora.linshare.webservice.dto;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ThreadEntry;

@XmlRootElement(name = "ThreadEntry")
public class ThreadEntryDto {

	protected String uuid;
	protected String name;
	protected String description;
	protected Calendar creationDate;
	protected Calendar modificationDate;
	protected Boolean ciphered;
	protected String type;
	protected Long size;

	public ThreadEntryDto(ThreadEntry te) {
		super();
		if (te == null) {
			return;
		}
		this.uuid = te.getUuid();
		this.name = te.getName();
		this.creationDate = te.getCreationDate();
		this.modificationDate = te.getModificationDate();
		this.description = te.getComment();
		this.ciphered = te.getCiphered();
		this.type = te.getDocument().getType();
		this.size = te.getDocument().getSize();
	}

	public ThreadEntryDto() {
		super();
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

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "ThreadEntry [id=" + uuid + ", name=" + name + ", creation=" + creationDate + "]";
	}
}
