package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.DomainType;
import org.linagora.linShare.core.domain.vo.AbstractDomainVo;
import org.linagora.linShare.core.domain.vo.TopDomainVo;

public class TopDomain extends AbstractDomain {

	public TopDomain() {
	}

	public TopDomain(String identifier, String label, RootDomain rootDomain) {
		super(identifier, label);
		this.defaultRole=Role.ADMIN;
		this.defaultLocale="en";
		this.parentDomain=rootDomain;
	}
	
	public TopDomain(String identifier, String label, LDAPConnection ldapConn, DomainPattern domainPattern, String baseDn) {
		this(identifier,label,null);
		this.userProvider = new LdapUserProvider(baseDn,ldapConn,domainPattern);
	}
	
	public TopDomain(TopDomainVo topDomain) {
		super(topDomain);
	}

	@Override
	public DomainType getDomainType() {
		return DomainType.TOPDOMAIN;
	}
}
