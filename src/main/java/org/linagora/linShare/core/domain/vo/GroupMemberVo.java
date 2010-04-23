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

import java.io.Serializable;
import java.util.Calendar;

import org.linagora.linShare.core.domain.entities.GroupMemberType;

public class GroupMemberVo implements Serializable {
	private static final long serialVersionUID = -8759783214949446159L;

	private UserVo userVo;
	private GroupMemberType type;
	private Calendar membershipDate;

	public UserVo getUserVo() {
		return userVo;
	}

	public void setUserVo(UserVo userVo) {
		this.userVo = userVo;
	}

	public GroupMemberType getType() {
		return type;
	}

	public void setType(GroupMemberType type) {
		this.type = type;
	}
	
	public boolean getIsManager() {
		return (this.type.equals(GroupMemberType.MANAGER)||this.type.equals(GroupMemberType.OWNER));
	}
	
	public boolean getIsOwner() {
		return (this.type.equals(GroupMemberType.OWNER));
	}
	
	public boolean isWaitingForApproval() {
		return(this.type.equals(GroupMemberType.WAITING_APPROVAL));
	}

	public String getFirstName() {
		return this.userVo.getFirstName();
	}

	public String getLastName() {
		return this.userVo.getLastName();
	}

	public String getMail() {
		return this.userVo.getMail();
	}

	public Calendar getMembershipDate() {
		return membershipDate;
	}

	public void setMembershipDate(Calendar membershipDate) {
		this.membershipDate = membershipDate;
	}

	public boolean isAllowedToManageGroup() {
		return (this.type.equals(GroupMemberType.OWNER));
	}

	public boolean isAllowedToManageManager() {
		return (this.type.equals(GroupMemberType.OWNER));
	}

	public boolean isAllowedToManageUser() {
		return (this.type.equals(GroupMemberType.OWNER)
				||this.type.equals(GroupMemberType.MANAGER));
	}

	public boolean isAllowedToAddFile() {
		return (this.type.equals(GroupMemberType.OWNER)
				||this.type.equals(GroupMemberType.MANAGER)
				||this.type.equals(GroupMemberType.MEMBER));
	}

	public boolean isAllowedToDeleteFile() {
		return (this.type.equals(GroupMemberType.OWNER)
				||this.type.equals(GroupMemberType.MANAGER));
	}

	public boolean isAllowedToUpdateFile() {
		return (this.type.equals(GroupMemberType.OWNER)
				||this.type.equals(GroupMemberType.MANAGER)
				||this.type.equals(GroupMemberType.MEMBER));
	}
}
