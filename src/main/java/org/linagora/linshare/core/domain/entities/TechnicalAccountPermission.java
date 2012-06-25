package org.linagora.linshare.core.domain.entities;

import java.util.Set;

public class TechnicalAccountPermission {

	private long id;
	
	private boolean write;
	
	private boolean all;
	
	private Set<Account> accounts = new java.util.HashSet<Account>();
	
	private Set<AbstractDomain> domains = new java.util.HashSet<AbstractDomain>();

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

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

	public java.util.Set<AbstractDomain> getDomains() {
		return domains;
	}

	public void setDomains(Set<AbstractDomain> domains) {
		this.domains = domains;
	}
}
