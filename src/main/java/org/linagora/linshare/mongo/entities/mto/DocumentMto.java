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

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;

public class DocumentMto extends EntryMto {

	protected String type;

	protected String humanMimeType;

	protected Long size;

	protected String sha256Sum;

	public DocumentMto() {
		super();
	}

	public DocumentMto(DocumentEntry entry) {
		super(entry);
		this.type = entry.getType();
		this.humanMimeType = entry.getHumanMimeType();
		this.sha256Sum = entry.getSha256sum();
		this.size = entry.getSize();
	}

	public DocumentMto(UploadRequestEntry entry) {
		super(entry);
		this.type = entry.getType();
		this.humanMimeType = entry.getHumanMimeType();
		this.sha256Sum = entry.getSha256sum();
		this.size = entry.getSize();
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public void setSha256Sum(String sha256Sum) {
		this.sha256Sum = sha256Sum;
	}

	public String getType() {
		return type;
	}

	public Long getSize() {
		return size;
	}

	public String getSha256Sum() {
		return sha256Sum;
	}

	public String getHumanMimeType() {
		return humanMimeType;
	}

	public void setHumanMimeType(String humanMimeType) {
		this.humanMimeType = humanMimeType;
	}
}
