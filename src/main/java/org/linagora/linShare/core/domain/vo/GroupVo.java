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
import java.util.HashSet;
import java.util.Set;

import org.linagora.linShare.core.domain.entities.GroupMember;
import org.linagora.linShare.core.domain.entities.GroupMemberType;
import org.linagora.linShare.core.utils.HashCodeUtil;

public class GroupVo implements Serializable {

	private static final long serialVersionUID = -496314671154416831L;

	private String name;
	private String description;
	private String functionalEmail;
	private String ownerLogin;
	private Set<GroupMemberVo> members;
	private UserVo groupUser;

	public GroupVo() {
		this.name = null;
		this.description = null;
		this.functionalEmail = null;
		this.ownerLogin = null;
		this.members = new HashSet<GroupMemberVo>();
		this.groupUser = null;
	}

	public boolean equals(Object obj) {
		if (obj instanceof GroupVo) {
			return ((GroupVo) obj).getGroupUser().getLogin().equals(this.groupUser.getLogin());
		}
		return super.equals(obj);
	}

	private int fHashCode;

	@Override
	public int hashCode() {
		// this style of lazy initialization is
		// suitable only if the object is immutable
		if (fHashCode == 0) {
			int result = HashCodeUtil.SEED;
			result = HashCodeUtil.hash(result, groupUser.getLogin());
			fHashCode = result;
		}
		return fHashCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<GroupMemberVo> getMembers() {
		return members;
	}

	public void setMembers(Set<GroupMemberVo> members) {
		this.members = members;
	}

	public void addMember(GroupMemberVo member) {
		this.members.add(member);
	}

	public void removeMember(GroupMember member) {
		this.members.remove(member);
	}

	public GroupMemberType getMemberType(String login) {
		for (GroupMemberVo member : this.members) {
			if (member.getUserVo().getLogin().equals(login)) {
				return member.getType();
			}
		}
		return null;
	}

	public GroupMemberVo findMember(String login) {
		for (GroupMemberVo member : this.members) {
			if (member.getUserVo().getLogin().equals(login)) {
				return member;
			}
		}
		return null;
	}

	public void setOwnerLogin(String ownerLogin) {
		this.ownerLogin = ownerLogin;
	}

	public String getOwnerLogin() {
		return ownerLogin;
	}

	public String getGroupLogin() {
		return groupUser.getLogin();
	}

	public void setGroupUser(UserVo groupUser) {
		this.groupUser = groupUser;
	}

	public UserVo getGroupUser() {
		return groupUser;
	}

	public void setFunctionalEmail(String functionalEmail) {
		this.functionalEmail = functionalEmail;
	}

	public String getFunctionalEmail() {
		return functionalEmail;
	}

}
