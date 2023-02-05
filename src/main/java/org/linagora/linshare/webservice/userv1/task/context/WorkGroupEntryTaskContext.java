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

public class WorkGroupEntryTaskContext extends TaskContext {

	protected File file;

	protected final String fileName;

	protected final String metaData;

	protected final String description;

	protected final String threadUuid;

	protected final String workGroupFolderUuid;

	protected final Boolean strictModeActivated;

	/**
	 * uuid of the document entry to be copied.
	 */
	protected String docEntryUuid;

	public WorkGroupEntryTaskContext(AccountDto authUserDto, String actorUuid,
			String threadUuid, File file, String fileName, String metaData,
			String description, String workGroupFolderUuid) {
		super(authUserDto, actorUuid);
		this.file = file;
		this.fileName = fileName;
		this.metaData = metaData;
		this.description = description;
		this.threadUuid = threadUuid;
		this.workGroupFolderUuid = workGroupFolderUuid;
		this.strictModeActivated = false;
	}

	public WorkGroupEntryTaskContext(AccountDto authUserDto, String actorUuid, String threadUuid, File file,
			String fileName, String workGroupFolderUuid, Boolean strict) {
		super(authUserDto, actorUuid);
		this.file = file;
		this.fileName = fileName;
		this.metaData = null;
		this.description = null;
		this.threadUuid = threadUuid;
		this.workGroupFolderUuid = workGroupFolderUuid;
		this.strictModeActivated = strict;
	}

	public WorkGroupEntryTaskContext(AccountDto authUserDto, String actorUuid,
			String threadUuid, String docEntryUuid, String workGroupFolderUuid) {
		super(authUserDto, actorUuid);
		this.file = null;
		this.fileName = null;
		this.metaData = null;
		this.description = null;
		this.threadUuid = threadUuid;
		this.docEntryUuid = docEntryUuid;
		this.workGroupFolderUuid = workGroupFolderUuid;
		this.strictModeActivated = false;
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

	public String getThreadUuid() {
		return threadUuid;
	}

	public String getDocEntryUuid() {
		return docEntryUuid;
	}

	public String getWorkGroupFolderUuid() {
		return workGroupFolderUuid;
	}

	public Boolean getStrictModeActivated() {
		return strictModeActivated;
	}

	@Override
	public String toString() {
		return "WorkGroupEntryTaskContext [fileName=" + fileName + ", threadUuid="
				+ threadUuid + ", actorUuid=" + actorUuid + "]";
	}

}
