package org.linagora.linShare.core.repository;

import org.linagora.linShare.core.domain.entities.DomainAccessPolicy;

public interface DomainAccessPolicyRepository extends AbstractRepository<DomainAccessPolicy> {
	
	public DomainAccessPolicy findById(long id);
}
