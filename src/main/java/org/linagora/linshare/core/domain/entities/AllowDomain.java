package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;



public class AllowDomain extends DomainAccessRule {

	private AbstractDomain domain;
	
	/*
	 * A default constructor is needed for hibernate for loading entities, 
	 * but you can not persist this entity without setting up a domain.
	 * That is why this contructor is private.
	 */
	@SuppressWarnings("unused")
	private AllowDomain() {
		super();
	}
	
	public AllowDomain(AbstractDomain domain) {
		super();
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "My type is : " + String.valueOf(AllowDomain.class) + "(" + domain.getIdentifier() + ")";
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}
	
	@Override
	public DomainAccessRuleType getDomainAccessRuleType() {
		return DomainAccessRuleType.ALLOW;
	}
}
