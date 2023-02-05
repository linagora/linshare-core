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

import org.linagora.linshare.core.domain.entities.UploadRequestEntry;

public class UploadRequestEntryMto {

	protected String name;

	protected Long size;

	protected String type;

	protected String sha256sum;

	protected Boolean copied;

	protected Boolean ciphered;

	protected String uploadRequestUuid;

	protected String uploadRequestGroupUuid;

	public UploadRequestEntryMto() {
		super();
	}

	public UploadRequestEntryMto(UploadRequestEntry reqEntry) {
		this.name = reqEntry.getName();
		this.type = reqEntry.getType();
		this.size = reqEntry.getSize();
		this.sha256sum = reqEntry.getSha256sum();
		this.ciphered = reqEntry.getCiphered();
		this.copied = reqEntry.getCopied();
		this.uploadRequestUuid = reqEntry.getUploadRequestUrl().getUploadRequest().getUuid();
		this.uploadRequestGroupUuid = reqEntry.getUploadRequestUrl().getUploadRequest().getUploadRequestGroup()
				.getUuid();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getSha256sum() {
		return sha256sum;
	}

	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public Boolean getCopied() {
		return copied;
	}

	public void setCopied(Boolean copied) {
		this.copied = copied;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public String getUploadRequestUuid() {
		return uploadRequestUuid;
	}

	public void setUploadRequestUuid(String uploadRequestUuid) {
		this.uploadRequestUuid = uploadRequestUuid;
	}

	public String getUploadRequestGroupUuid() {
		return uploadRequestGroupUuid;
	}

	public void setUploadRequestGroupUuid(String uploadRequestGroupUuid) {
		this.uploadRequestGroupUuid = uploadRequestGroupUuid;
	}
}