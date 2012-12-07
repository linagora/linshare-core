package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.DomainPolicy;

public interface DomainPolicyRepository extends AbstractRepository<DomainPolicy> {
	
	public DomainPolicy findById(String identifier);
	public List<String> findAllIdentifiers();

}
