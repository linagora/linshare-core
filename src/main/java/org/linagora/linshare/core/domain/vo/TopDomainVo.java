package org.linagora.linshare.core.domain.vo;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Role;

public class TopDomainVo extends AbstractDomainVo {
	
	private List<String> subdomainIdentifiers;

	public List<String> getSubdomainIdentifiers() {
		return subdomainIdentifiers;
	}

	public void setSubdomainIdentifiers(List<String> subdomainIdentifiers) {
		this.subdomainIdentifiers = subdomainIdentifiers;
	}

	public TopDomainVo(AbstractDomain entity) {
		super(entity);
		
		this.setSubdomainIdentifiers(new ArrayList<String>());
		for (AbstractDomain domain : entity.getSubdomain()) {
			this.getSubdomainIdentifiers().add(domain.getIdentifier());
		}
	}

	
	public TopDomainVo() {
		super();
		this.defaultRole = Role.ADMIN;
	}

	/**
	 * @param identifier
	 * @param differentialKey
	 * @param patternIdentifier
	 * @param ldapIdentifier
	 */
	public TopDomainVo(String identifier, String differentialKey,
			String patternIdentifier, String ldapIdentifier) {
		super(identifier, differentialKey, patternIdentifier, ldapIdentifier);
		this.defaultRole = Role.ADMIN;
	}
}
