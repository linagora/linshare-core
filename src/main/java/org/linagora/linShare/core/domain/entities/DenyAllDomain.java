package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.DomainAccessRuleType;

public class DenyAllDomain extends DomainAccessRule {

	public DenyAllDomain() {
		super();
	}
	
	@Override
	public String toString() {
		return "My type is : " + String.valueOf(DenyAllDomain.class);
	}

	@Override
	public DomainAccessRuleType getDomainAccessRuleType() {
		return DomainAccessRuleType.DENY_ALL;
	}
}
