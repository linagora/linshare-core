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
package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.ThreadMember;

public class ThreadMemberVo implements Comparable<ThreadMemberVo> {
	
	private UserVo user;
	
	private boolean canUpload;
	
	private boolean admin;

	public ThreadMemberVo(ThreadMember threadMember) {
		super();
		this.user = new UserVo(threadMember.getUser());
		this.admin = threadMember.getAdmin();
		this.canUpload = this.admin || threadMember.getCanUpload();
	}

	public ThreadMemberVo(UserVo user, boolean canUpload, boolean admin) {
		super();
		this.user = user;
		this.canUpload = canUpload;
		this.admin = admin;
	}

	public ThreadMemberVo() {
	}

	public UserVo getUser() {
		return user;
	}

	public void setUser(UserVo user) {
		this.user = user;
	}

	public boolean isCanUpload() {
		return canUpload;
	}

	public void setCanUpload(boolean canUpload) {
		this.canUpload = canUpload;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public String getMail() {
		return user.getMail();
	}
	
	public String getFullName() {
		return user.getFullName();
	}
	
	public String getLsUuid() {
		return user.getLsUid();
	}

	@Override
	public int compareTo(ThreadMemberVo o) {
		if (this.admin) {
			return o.admin ? this.user.compareTo(o.getUser()) : -1;
		}
		if (this.canUpload)
			return o.admin ? 1 : o.canUpload ? this.user.compareTo(o.getUser()) : -1;
		return o.admin || o.canUpload ? 1 : this.user.compareTo(o.getUser());
	}
}
