package org.linagora.linshare.core.Facade;

import java.util.List;

import org.linagora.linshare.core.domain.entities.DomainPolicy;

public interface DomainPolicyFacade {

	public List<String> getAllDomainPolicyIdentifiers();
	public List<DomainPolicy> getAllDomainPolicy();
	
}
