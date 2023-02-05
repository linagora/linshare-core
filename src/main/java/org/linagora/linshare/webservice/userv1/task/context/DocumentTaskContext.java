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
package org.linagora.linshare.webservice.userv1.task.context;

import java.io.File;

import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;

public class DocumentTaskContext extends TaskContext {

	protected File file;

	protected final String fileName;

	protected final String metaData;

	protected final String description;

	/**
	 * uuid of the updated document entry.
	 */
	protected String docEntryUuid;

	public DocumentTaskContext(AccountDto authUserDto, String actorUuid,
			File file, String fileName, String metaData, String description) {
		super(authUserDto, actorUuid);
		this.file = file;
		this.fileName = fileName;
		this.metaData = metaData;
		this.description = description;
	}

	public DocumentTaskContext(AccountDto authUserDto, String actorUuid,
			File file, String fileName) {
		super(authUserDto, actorUuid);
		this.file = file;
		this.fileName = fileName;
		this.metaData = null;
		this.description = null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMetaData() {
		return metaData;
	}

	public String getDescription() {
		return description;
	}

	public String getDocEntryUuid() {
		return docEntryUuid;
	}

	public void setDocEntryUuid(String docEntryUuid) {
		this.docEntryUuid = docEntryUuid;
	}

	@Override
	public String toString() {
		return "DocumentTaskContext [fileName=" + fileName + ", docEntryUuid="
				+ docEntryUuid + ", actorUuid=" + actorUuid + "]";
	}

}
