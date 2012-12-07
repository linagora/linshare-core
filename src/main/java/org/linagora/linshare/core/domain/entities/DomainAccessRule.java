package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;


public abstract class DomainAccessRule {
	/**
	 * Database persistence identifier
	 */
	private long persistenceId;
	
	private String regexp;
	
	public DomainAccessRule() {
	}

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}
	
	public abstract DomainAccessRuleType getDomainAccessRuleType();
}
