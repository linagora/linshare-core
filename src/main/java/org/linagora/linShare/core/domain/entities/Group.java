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
package org.linagora.linShare.core.domain.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.linagora.linShare.core.domain.constants.GroupMemberType;

public class Group implements Serializable {
	private static final long serialVersionUID = -1126555393767681768L;

	private long persistenceId;
	
	private String name;
	private String description;
	private String functionalEmail;
	private Set<GroupMember> members;
    private User groupUser;
    
    public Group() {
		this.name = null;
		this.description = null;
		this.functionalEmail = null;
    	this.members=new HashSet<GroupMember>();
    	this.groupUser=null;
	}
    
	public long getPersistenceId() {
		return persistenceId;
	}
	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
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
	public Set<GroupMember> getMembers() {
		return members;
	}
	public void setMembers(Set<GroupMember> members) {
		this.members = members;
	}
	public void addMember(GroupMember member) {
		this.members.add(member);
	}
	public void removeMember(GroupMember member) {
		this.members.remove(member);
	}
    public String getOwnerLogin() {
    	for (GroupMember member : this.members) {
			if (member.getType().equals(GroupMemberType.OWNER)) {
				return member.getUser().getLogin();
			}
		}
    	return null;
    }
    public GroupMemberType getMemberType(String login) {
    	for (GroupMember member : this.members) {
			if (member.getUser().getLogin().equals(login)) {
				return member.getType();
			}
		}
    	return null;    	
    }
	public String getGroupLogin() {
		return (this.name.toLowerCase() + "@linshare.groups");
	}

	public void setGroupUser(User groupUser) {
		this.groupUser = groupUser;
	}

	public User getGroupUser() {
		return groupUser;
	}

	public void setFunctionalEmail(String functionalEmail) {
		this.functionalEmail = functionalEmail;
	}

	public String getFunctionalEmail() {
		return functionalEmail;
	}
    
}
