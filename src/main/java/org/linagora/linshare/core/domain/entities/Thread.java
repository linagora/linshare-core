package org.linagora.linshare.core.domain.entities;

import java.util.Set;

import org.linagora.linshare.core.domain.constants.UserType;

public class Thread extends Account {

	protected String name;
	
	protected Set<ThreadMember> myMembers = new java.util.HashSet<ThreadMember>();

	public Thread() {
		super();
	}

	
	public Thread(AbstractDomain domain, Account owner , String name) {
		super();
		this.name = name;
		this.domain = domain;
		this.enable = true;
		this.destroyed = false;
		this.owner = owner;
	}

	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public UserType getAccountType() {
		return UserType.THREAD;
	}

	public Set<ThreadMember> getMyMembers() {
		return myMembers;
	}

	public void setMyMembers(Set<ThreadMember> myMembers) {
		this.myMembers = myMembers;
	}

}
