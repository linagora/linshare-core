/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.facade.webservice.uploadrequest.dto;

import java.util.Date;

import org.linagora.linshare.core.domain.entities.UploadRequestUrl;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class UploadRequestUrlDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Path")
	private String path;

	@ApiModelProperty(value = "Password")
	private String password;

	@ApiModelProperty(value = "Contact")
	private ContactDto contact;

	@ApiModelProperty(value = "CreationDate")
	protected Date creationDate;

	@ApiModelProperty(value = "ModificationDate")
	protected Date modificationDate;

	public UploadRequestUrlDto() {
		super();
	}

	public UploadRequestUrlDto(String uuid, String path, String password, ContactDto contact, Date creationDate,
			Date modificationDate) {
		super();
		this.uuid = uuid;
		this.path = path;
		this.password = password;
		this.contact = contact;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
	}

	public UploadRequestUrlDto(UploadRequestUrl uploadRequestUrl) {
		super();
		this.uuid = uploadRequestUrl.getUuid();
		this.path = uploadRequestUrl.getPath();
		this.password = uploadRequestUrl.getPassword();
		this.contact = new ContactDto(uploadRequestUrl.getContact());
		this.creationDate = uploadRequestUrl.getCreationDate();
		this.modificationDate = uploadRequestUrl.getModificationDate();
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

	public ContactDto getContact() {
		return contact;
	}

	public void setContact(ContactDto contact) {
		this.contact = contact;
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
}
