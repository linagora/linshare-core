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
package org.linagora.linshare.core.domain.entities;

/**
 * Log class for uploading/deleting/expiring files
 * 
 * @author ncharles
 * 
 */
public class FileLogEntry extends LogEntry {

	private static final long serialVersionUID = -7747367540741943254L;

	protected String fileName;

	protected Long fileSize;

	protected String fileType;

	protected FileLogEntry() {
		super();
		this.fileName = null;
		this.fileSize = null;
		this.fileType = null;
	}

	public String getFileName() {
		return fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public String getFileType() {
		return fileType;
	}
	
	/**
	 * Format:
	 * 	USER_ACTIVITY:logAction:actorDomain:actorMail:description:file=fileName,size=fileSize
	 */
	@Override
	public String toString() {
		return super.toString() + ":file=" + fileName + ",size=" + fileSize;
	}

}
