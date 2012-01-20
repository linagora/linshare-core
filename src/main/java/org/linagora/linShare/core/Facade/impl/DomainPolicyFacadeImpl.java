package org.linagora.linShare.core.Facade.impl;

import java.util.List;

import org.linagora.linShare.core.Facade.DomainPolicyFacade;
import org.linagora.linShare.core.domain.entities.DomainPolicy;
import org.linagora.linShare.core.service.DomainPolicyService;

public class DomainPolicyFacadeImpl implements DomainPolicyFacade {

	private final DomainPolicyService domainPolicyService;
	
	public DomainPolicyFacadeImpl(DomainPolicyService domainPolicyService) {
		super();
		this.domainPolicyService = domainPolicyService;
	}


	@Override
	public List<String> getAllDomainPolicyIdentifiers() {
		return domainPolicyService.getAllDomainPolicyIdentifiers();
	}


	@Override
	public List<DomainPolicy> getAllDomainPolicy() {
		return domainPolicyService.getAllDomainPolicy();
	}

}
