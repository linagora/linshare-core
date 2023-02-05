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
