package org.linagora.linShare.core.repository;

import org.linagora.linShare.core.domain.entities.Policy;

public interface PolicyRepository extends AbstractRepository<Policy> {
	public Policy findById(long id);
}
