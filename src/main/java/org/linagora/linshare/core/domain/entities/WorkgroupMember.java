/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
