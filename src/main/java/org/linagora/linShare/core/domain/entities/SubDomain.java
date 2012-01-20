package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.DomainType;
import org.linagora.linShare.core.domain.vo.SubDomainVo;

public class SubDomain extends AbstractDomain {

	public SubDomain() {
	}

	public SubDomain(String identifier, String label,TopDomain topDomain) {
		super(identifier, label);
		this.defaultRole=Role.SIMPLE;
		this.defaultLocale="en";
		this.parentDomain=topDomain;
	}
	
	public SubDomain(SubDomainVo subDomain) {
		super(subDomain);
	}

	@Override
	public DomainType getDomainType() {
		return DomainType.SUBDOMAIN;
	}
}
