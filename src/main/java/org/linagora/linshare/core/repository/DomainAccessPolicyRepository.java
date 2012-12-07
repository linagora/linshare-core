package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;

public interface DomainAccessPolicyRepository extends AbstractRepository<DomainAccessPolicy> {
	
	public DomainAccessPolicy findById(long id);
}
