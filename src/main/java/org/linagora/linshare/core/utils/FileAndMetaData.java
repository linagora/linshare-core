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
package org.linagora.linshare.core.utils;

import java.io.File;

import org.linagora.linshare.mongo.entities.WorkGroupDocument;

import com.google.common.io.ByteSource;

public class FileAndMetaData {

	protected ByteSource byteSource;

	protected Long size;

	protected String name;

	protected String mimeType;

	protected Boolean tempFileDeleted;

	protected File file;

	public FileAndMetaData(WorkGroupDocument document, ByteSource byteSource) {
		super();
		this.size = document.getSize();
		this.name = document.getName();
		this.mimeType = document.getMimeType();
		this.byteSource = byteSource;
		this.tempFileDeleted = false;
	}

	public FileAndMetaData(ByteSource byteSource, Long size, String name, String mimeType) {
		super();
		this.byteSource = byteSource;
		this.size = size;
		this.name = name;
		this.mimeType = mimeType;
		this.tempFileDeleted = false;
	}

	public FileAndMetaData(ByteSource byteSource, String name, String mimeType) {
		super();
		this.byteSource = byteSource;
		this.name = name;
		this.mimeType = mimeType;
		this.tempFileDeleted = false;
	}

	public ByteSource getByteSource() {
		return byteSource;
	}

	public void setByteSource(ByteSource byteSource) {
		this.byteSource = byteSource;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Boolean isTempFileDeleted() {
		return tempFileDeleted;
	}

	public void setTempFileDeleted(Boolean tempFileDeleted) {
		this.tempFileDeleted = tempFileDeleted;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
