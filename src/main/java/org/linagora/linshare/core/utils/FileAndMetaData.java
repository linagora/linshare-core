/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
