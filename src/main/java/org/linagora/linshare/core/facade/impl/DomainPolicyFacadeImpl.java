package org.linagora.linshare.core.facade.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.facade.DomainPolicyFacade;
import org.linagora.linshare.core.service.DomainPolicyService;

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
