/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.core.domain.objects;

import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Signature;

/**
 * @author fred
 *
 */
public class FileMetaData {

	private String uuid;

	private final FileMetaDataKind kind;

	private final String mimeType;

	private String bucketUuid;

	private final Long size;

	// optional meta data
	private String fileName;

	public FileMetaData(FileMetaDataKind kind, String mimeType, Long size, String fileName) {
		super();
		this.uuid = null;
		this.kind = kind;
		this.mimeType = mimeType;
		this.size = size;
		this.fileName = fileName;
		this.bucketUuid = null;
	}

	public FileMetaData(FileMetaDataKind kind, String mimeType, Long size) {
		super();
		this.uuid = null;
		this.kind = kind;
		this.mimeType = mimeType;
		this.size = size;
		this.bucketUuid = null;
	}

	public FileMetaData(FileMetaDataKind kind, Document document) {
		super();
		this.uuid = document.getUuid();
		if (kind.equals(FileMetaDataKind.THUMBNAIL)) {
			this.uuid = document.getThmbUuid();
		}
		this.kind = kind;
		this.mimeType = document.getType();
		this.size = document.getSize();
		this.bucketUuid = document.getBucketUuid();
	}

	public FileMetaData(FileMetaDataKind kind, Document document, String mimeType) {
		super();
		this.uuid = document.getUuid();
		if (kind.equals(FileMetaDataKind.THUMBNAIL)) {
			this.uuid = document.getThmbUuid();
		}
		this.kind = kind;
		this.mimeType = mimeType;
		this.size = null;
		this.bucketUuid = document.getBucketUuid();
	}

	public FileMetaData(Signature signature) {
		super();
		this.uuid = null;
		this.kind = FileMetaDataKind.SIGNATURE;
		this.bucketUuid = signature.getDocument().getBucketUuid();
		this.size = signature.getSize();
		this.mimeType = signature.getType();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public FileMetaDataKind getKind() {
		return kind;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getBucketUuid() {
		return bucketUuid;
	}

	public void setBucketUuid(String bucketUuid) {
		this.bucketUuid = bucketUuid;
	}

	public Long getSize() {
		return size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "FileMetaData [uuid=" + uuid + ", fileName=" + fileName + ", kind=" + kind + ", mimeType=" + mimeType
				+ ", bucketUuid=" + bucketUuid + ", size=" + size + "]";
	}
}
