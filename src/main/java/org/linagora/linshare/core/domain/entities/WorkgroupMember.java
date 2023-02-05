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

import java.util.Date;

public class WorkgroupMember {

	private long id;

	private boolean canUpload;

	private boolean admin;

	private Date creationDate;

	private Date modificationDate;

	private User user;

	private WorkGroup thread;

	public WorkgroupMember() {
		super();
	}

	public WorkgroupMember(boolean canUpload, boolean admin, User user,
			WorkGroup thread) {
		super();
		this.canUpload = canUpload;
		this.admin = admin;
		this.user = user;
		this.thread = thread;
		setCreationDate(new Date());
		setModificationDate(new Date());
	}

	public void setId(long value) {
		this.id = value;
	}

	public long getId() {
		return id;
	}

	public void setCanUpload(boolean value) {
		this.canUpload = value;
	}

	public boolean getCanUpload() {
		return canUpload;
	}

	public void setAdmin(boolean value) {
		this.admin = value;
	}

	public boolean getAdmin() {
		return admin;
	}

	public void setCreationDate(Date value) {
		this.creationDate = value;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setModificationDate(Date value) {
		this.modificationDate = value;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public WorkGroup getThread() {
		return thread;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setThread(WorkGroup thread) {
		this.thread = thread;
	}

}
