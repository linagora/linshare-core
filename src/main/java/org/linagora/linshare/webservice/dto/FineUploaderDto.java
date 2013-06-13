package org.linagora.linshare.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FineUploader")
public class FineUploaderDto {

	private boolean success;

	private String newUuid;

	public FineUploaderDto(boolean success, String newUuid) {
		super();
		this.success = success;
		this.newUuid = newUuid;
	}

	public FineUploaderDto(boolean success) {
		this(success, "");
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getNewUuid() {
		return newUuid;
	}

	public void setNewUuid(String newUuid) {
		this.newUuid = newUuid;
	}

}
