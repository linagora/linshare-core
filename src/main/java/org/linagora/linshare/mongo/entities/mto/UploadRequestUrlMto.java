/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.mongo.entities.mto;

import java.util.Date;

import org.linagora.linshare.core.domain.entities.UploadRequestUrl;

public class UploadRequestUrlMto {

	private String contactMail;

	private String uploadRequestUuid;

	private Date creationDate;

	private Date modificationDate;

	private String uploadRequestGroupUuid;

	public UploadRequestUrlMto() {
		super();
	}

	public UploadRequestUrlMto(UploadRequestUrl url) {
		this.contactMail = url.getContact().getMail();
		this.uploadRequestUuid = url.getUploadRequest().getUuid();
		this.uploadRequestGroupUuid = url.getUploadRequest().getUploadRequestGroup().getUuid();
		this.creationDate = url.getCreationDate();
		this.modificationDate = url.getModificationDate();
	}

	public String getContactMail() {
		return contactMail;
	}

	public void setContactMail(String contactMail) {
		this.contactMail = contactMail;
	}

	public String getUploadRequestUuid() {
		return uploadRequestUuid;
	}

	public void setUploadRequestUuid(String uploadRequestUuid) {
		this.uploadRequestUuid = uploadRequestUuid;
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

	public String getUploadRequestGroupUuid() {
		return uploadRequestGroupUuid;
	}

	public void setUploadRequestGroupUuid(String uploadRequestGroupUuid) {
		this.uploadRequestGroupUuid = uploadRequestGroupUuid;
	}
}
