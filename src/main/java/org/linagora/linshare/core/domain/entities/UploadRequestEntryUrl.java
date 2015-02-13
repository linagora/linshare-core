package org.linagora.linshare.core.domain.entities;

import java.util.Date;

public class UploadRequestEntryUrl {

	private long id;

	private String uuid;

	private String path;

	private String password;

	private Date creationDate;

	private Date modificationDate;

	private Date expiryDate;

	private UploadRequestEntry uploadRequestEntry;

	private String temporaryPlainTextPassword;

	public UploadRequestEntryUrl() {
		super();
	}

	public UploadRequestEntryUrl(UploadRequestEntry uploadRequestEntry, String path) {
		super();
		this.uploadRequestEntry = uploadRequestEntry;
		this.path = path;
		this.password = null;
		this.expiryDate = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creation_date) {
		this.creationDate = creation_date;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modification_date) {
		this.modificationDate = modification_date;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiration_date) {
		this.expiryDate = expiration_date;
	}

	public UploadRequestEntry getUploadRequestEntry() {
		return uploadRequestEntry;
	}

	public void setUploadRequestEntry(UploadRequestEntry uploadRequestEntry) {
		this.uploadRequestEntry = uploadRequestEntry;
	}

	public String getTemporaryPlainTextPassword() {
		return temporaryPlainTextPassword;
	}

	public void setTemporaryPlainTextPassword(String temporaryPlainTextPassword) {
		this.temporaryPlainTextPassword = temporaryPlainTextPassword;
	}

	public boolean isProtectedByPassword() {
		return password != null;
	}

	public String getFullUrl(String baseUrl) {
		// compose the secured url to give in mail
		StringBuffer httpUrlBase = new StringBuffer();
		httpUrlBase.append(baseUrl);
		if (!baseUrl.endsWith("/")) {
			httpUrlBase.append('/');
		}
		httpUrlBase.append(getPath());
		if (!getPath().endsWith("/")) {
			httpUrlBase.append('/');
		}
		httpUrlBase.append(getUuid());
		return httpUrlBase.toString();
	}
}
