package org.linagora.linshare.core.domain.entities;

import java.util.ArrayList;
import java.util.List;

public class DomainAccessPolicy {

	/**
	 * Database persistence identifier
	 */
	private long persistenceId;
	
	private List<DomainAccessRule> rules;

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public DomainAccessPolicy() {
		super();
	}

	public List<DomainAccessRule> getRules() {
		return rules;
	}

	public void setRules(List<DomainAccessRule> rules) {
		this.rules = rules;
	}
	
	public void addRule(DomainAccessRule rule) {
		if(this.rules == null) {
			this.rules = new ArrayList<DomainAccessRule>();
		}
		this.rules.add(rule);
	}
}
