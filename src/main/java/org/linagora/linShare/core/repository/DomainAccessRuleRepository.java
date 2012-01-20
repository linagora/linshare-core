package org.linagora.linShare.core.repository;

import org.linagora.linShare.core.domain.entities.DomainAccessRule;

public interface DomainAccessRuleRepository extends AbstractRepository<DomainAccessRule> {
	
	public DomainAccessRule findById(long id);
}
