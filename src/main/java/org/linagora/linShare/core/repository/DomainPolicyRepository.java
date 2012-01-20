package org.linagora.linShare.core.repository;

import java.util.List;

import org.linagora.linShare.core.domain.entities.DomainPolicy;

public interface DomainPolicyRepository extends AbstractRepository<DomainPolicy> {
	
	public DomainPolicy findById(String identifier);
	public List<String> findAllIdentifiers();

}
