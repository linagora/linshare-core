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

public class GroupVo implements Serializable {

	private static final long serialVersionUID = -496314671154416831L;

	private String name;
	private String description;
	private String ownerLogin;
	private Set<GroupMemberVo> members;

	public GroupVo() {
		this.name = null;
		this.description = null;
		this.ownerLogin = null;
		this.members = new HashSet<GroupMemberVo>();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof GroupVo) {
			return ((GroupVo)obj).getGroupLogin().equals(this.getGroupLogin());
		}
		return super.equals(obj);
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
		return (this.name.toLowerCase() + "@linshare.groups");
	}

}
