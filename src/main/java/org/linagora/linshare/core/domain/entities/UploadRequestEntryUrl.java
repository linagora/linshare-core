/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

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
