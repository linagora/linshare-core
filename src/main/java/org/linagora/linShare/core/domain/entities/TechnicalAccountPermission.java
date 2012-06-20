package org.linagora.linShare.core.domain.entities;

public class TechnicalAccountPermission {

	private long id;
	
	private boolean write;
	
	private java.util.Set<User> users = new java.util.HashSet<User>();
	
	private java.util.Set<AbstractDomain> domains = new java.util.HashSet<AbstractDomain>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isWrite() {
		return write;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}

	public java.util.Set<User> getUsers() {
		return users;
	}

	public void setUsers(java.util.Set<User> users) {
		this.users = users;
	}

	public java.util.Set<AbstractDomain> getDomains() {
		return domains;
	}

	public void setDomains(java.util.Set<AbstractDomain> domains) {
		this.domains = domains;
	}
}
