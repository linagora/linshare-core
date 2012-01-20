package org.linagora.linShare.core.Facade;

import java.util.List;

import org.linagora.linShare.core.domain.entities.DomainPolicy;

public interface DomainPolicyFacade {

	public List<String> getAllDomainPolicyIdentifiers();
	public List<DomainPolicy> getAllDomainPolicy();
	
}
