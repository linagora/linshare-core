/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
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
package org.linagora.linshare.core.facade.webservice.external.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.DocumentEntry;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "AnonymousShareEntry")
@Schema(name = "AnonymousShareEntry", description = "An AnonymousShareEntry")
public class ShareEntryDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Name")
	private String name;

	@Schema(description = "Size")
	private Long size;

	@Schema(description = "Type")
	private String type;

	public ShareEntryDto() {
		super();
	}

	public ShareEntryDto(String uuid, String name, Long size, String type) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.size = size;
		this.type = type;
	}

	public ShareEntryDto(String anonymousShareEntryUuid, DocumentEntry entry) {
		super();
		this.uuid = anonymousShareEntryUuid;
		this.name = entry.getName();
		this.size = entry.getSize();
		this.type = entry.getType();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ShareEntryDto [uuid=" + uuid + ", name=" + name + ", size=" + size + ", type=" + type + "]";
	}
}
