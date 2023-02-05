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
package org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "UploadRequestUrlTest")
public class UploadRequestUrlDto {

	private ContactDto contact;

	private String uuid;

	private String path;

	private String password;

	private String temporaryPlainTextPassword;

	private Date creationDate;

	private Date modificationDate;

	public UploadRequestUrlDto() {
		super();
	}

	public UploadRequestUrlDto(UploadRequestUrl uploadRequestUrl) {
		super();
		this.uuid = uploadRequestUrl.getUuid();
		this.contact = new ContactDto(uploadRequestUrl.getContact());
		this.path = uploadRequestUrl.getPath();
		this.creationDate = uploadRequestUrl.getCreationDate();
		this.modificationDate = uploadRequestUrl.getModificationDate();
	}

	public ContactDto getContact() {
		return contact;
	}

	public void setContact(ContactDto contact) {
		this.contact = contact;
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

	public void setTemporaryPlainTextPassword(String temporaryPlainTextPassword) {
		this.temporaryPlainTextPassword = temporaryPlainTextPassword;
	}
}
