/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.domain.vo;

import java.util.Calendar;

import org.linagora.linShare.core.domain.LogAction;

public class FileLogEntryVo extends LogEntryVo {

	private final String fileName;
	
	private final Long fileSize;
	
	private final String fileType;

	public FileLogEntryVo(Calendar actionDate, String actorMail,
			String actorFirstname, String actorLastname, String actorDomain,
			LogAction logAction,
			String description, String fileName, Long fileSize, String fileType) {
		super(actionDate, actorMail, actorFirstname, actorLastname, actorDomain,
				logAction,
				description);
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileType = fileType;
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
	
	
}
