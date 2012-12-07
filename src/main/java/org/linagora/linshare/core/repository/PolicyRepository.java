package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.Policy;

public interface PolicyRepository extends AbstractRepository<Policy> {
	public Policy findById(long id);
}
