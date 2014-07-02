package org.linagora.linshare.core.facade.webservice.uploadrequest.dto;

import org.linagora.linshare.core.domain.entities.UploadRequestEntry;

public class EntryDto {

	private String uuid;

	private String name;

	private long size;

	public EntryDto(String uuid, String name, long size) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.size = size;
	}

	public EntryDto(UploadRequestEntry entry) {
		super();
		this.uuid = entry.getUuid();
		this.name = entry.getName();
		this.size = entry.getSize();
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

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
