package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;


public class DenyDomain extends DomainAccessRule {

	private AbstractDomain domain;
	
	/*
	 * A default constructor is needed for hibernate for loading entities, 
	 * but you can not persist this entity without setting up a domain.
	 * That is why this contructor is private.
	 */
	@SuppressWarnings("unused")
	private DenyDomain() {
		super();
	}

	public DenyDomain(AbstractDomain domain) {
		super();
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "My type is : " + String.valueOf(DenyDomain.class) + "(" + domain.getIdentifier() + ")";
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}
	
	@Override
	public DomainAccessRuleType getDomainAccessRuleType() {
		return DomainAccessRuleType.DENY;
	}
	
}
