package org.linagora.linShare.core.domain.entities;

import java.util.Set;

import org.linagora.linShare.core.domain.constants.UserType;

public class Thread extends Account {

	protected String name;
	
	protected Set<ThreadMember> threadMembers = new java.util.HashSet<ThreadMember>();
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Set<ThreadMember> getThreadMembers() {
		return threadMembers;
	}

	public void setThreadMembers(Set<ThreadMember> threadMembers) {
		this.threadMembers = threadMembers;
	}

	@Override
	public UserType getAccountType() {
		return UserType.THREAD;
	}

}
