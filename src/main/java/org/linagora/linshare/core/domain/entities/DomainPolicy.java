package org.linagora.linshare.core.domain.entities;

public class DomainPolicy {
	
	/**
	 * Database persistence identifier
	 */
	private long persistenceId;
	
	private String identifier;
	
	private DomainAccessPolicy domainAccessPolicy;
	
	private String description;

	public DomainPolicy() {
		super();
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DomainAccessPolicy getDomainAccessPolicy() {
		return domainAccessPolicy;
	}

	public void setDomainAccessPolicy(DomainAccessPolicy domainAccessPolicy) {
		this.domainAccessPolicy = domainAccessPolicy;
	}

	public DomainPolicy(String identifier, DomainAccessPolicy policy) {
		super();
		this.identifier = identifier;
		this.domainAccessPolicy = policy;
	}

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}
}
