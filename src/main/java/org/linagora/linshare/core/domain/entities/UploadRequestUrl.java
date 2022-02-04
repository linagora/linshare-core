/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Language;

import com.google.common.collect.Sets;

public class UploadRequestUrl {

	private long id;

	private Contact contact;

	private UploadRequest uploadRequest;

	private String uuid;

	private String path;

	private String password;

	private String temporaryPlainTextPassword;

	private Date creationDate;

	private Date modificationDate;

	private Set<UploadRequestEntry> uploadRequestEntries = Sets.newHashSet();

	private Boolean defaultPassword;

	private String originalPassword;

	public UploadRequestUrl() {
		super();
	}

	public UploadRequestUrl(UploadRequest uploadRequest, String path, Contact contact) {
		super();
		this.uploadRequest = uploadRequest;
		this.path = path;
		this.password = null;
		this.temporaryPlainTextPassword = "";
		this.contact = contact;
		this.defaultPassword = true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public UploadRequest getUploadRequest() {
		return uploadRequest;
	}

	public void setUploadRequest(UploadRequest uploadRequest) {
		this.uploadRequest = uploadRequest;
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

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public boolean isProtectedByPassword() {
		return password != null;
	}

	public String getTemporaryPlainTextPassword() {
		return temporaryPlainTextPassword;
	}

	public Set<UploadRequestEntry> getUploadRequestEntries() {
		return uploadRequestEntries;
	}

	public void setUploadRequestEntries(
			Set<UploadRequestEntry> uploadRequestEntries) {
		this.uploadRequestEntries = uploadRequestEntries;
	}

	public void setTemporaryPlainTextPassword(String temporaryPlainTextPassword) {
		this.temporaryPlainTextPassword = temporaryPlainTextPassword;
	}

	public Boolean isDefaultPassword() {
		return defaultPassword;
	}

	public void setDefaultPassword(Boolean defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	public String getOriginalPassword() {
		return originalPassword;
	}

	public void setOriginalPassword(String originalPassword) {
		this.originalPassword = originalPassword;
	}

	public String getFullUrl(String baseUrl) {
		// compose the protected by password url to give in mail
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

	/**
	 * Helpers
	 */

	public Language getLocale() {
		return this.getUploadRequest().getLocale();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadRequestUrl other = (UploadRequestUrl) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UploadRequestUrl [contact=" + contact + ", uuid=" + uuid + ", path=" + path + ", creationDate="
				+ creationDate + ", modificationDate=" + modificationDate + "]";
	}
}
