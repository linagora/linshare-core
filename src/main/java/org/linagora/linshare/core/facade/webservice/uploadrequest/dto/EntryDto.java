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
package org.linagora.linshare.core.facade.webservice.uploadrequest.dto;

import org.linagora.linshare.core.domain.entities.UploadRequestEntry;

public class EntryDto {

	private String uuid;

	private String name;

	private long size;

	public EntryDto() {
		super();
	}

	public EntryDto(String uuid, String name, long size) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.size = size;
	}

	public EntryDto(UploadRequestEntry entry) {
		super();
		this.uuid = entry.getUuid();
		this.name = entry.getName();
		this.size = entry.getSize();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
