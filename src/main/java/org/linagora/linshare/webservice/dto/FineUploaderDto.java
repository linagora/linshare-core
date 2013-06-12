package org.linagora.linshare.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FineUploader")
public class FineUploaderDto {

	private boolean success;

	private String error;

	private String newUuid;

	public FineUploaderDto(boolean success, String reason, String newUuid) {
		super();
		this.success = success;
		this.error = reason;
		this.newUuid = newUuid;
	}

	public FineUploaderDto(boolean success) {
		new FineUploaderDto(success, "", "");
	}

	public FineUploaderDto(boolean success, String error) {
		new FineUploaderDto(success, error, "");
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getNewUuid() {
		return newUuid;
	}

	public void setNewUuid(String newUuid) {
		this.newUuid = newUuid;
	}

}
