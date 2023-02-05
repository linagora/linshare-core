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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;

@XmlRootElement(name = "NodeDetailsMto")
public class NodeMetadataMto {

	private String uuid;

	private WorkGroupNodeType type;

	private Long size;

	private Long count;

	private Long storageSize;

	public NodeMetadataMto() {
		super();
	}

	public NodeMetadataMto(String uuid, WorkGroupNodeType type) {
		this.uuid = uuid;
		this.type = type;
	}

	public NodeMetadataMto(String uuid, WorkGroupNodeType type, Long size, Long count) {
		super();
		this.uuid = uuid;
		this.type = type;
		this.size = size;
		this.count = count;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public WorkGroupNodeType getType() {
		return type;
	}

	public void setType(WorkGroupNodeType type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getStorageSize() {
		return storageSize;
	}

	public void setStorageSize(Long storageSize) {
		this.storageSize = storageSize;
	}

	@Override
	public String toString() {
		return "NodeMetadataMto [uuid=" + uuid + ", type=" + type + ", size=" + size + ", count=" + count
				+ ", storageSize=" + storageSize + "]";
	}

}
