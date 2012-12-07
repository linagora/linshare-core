package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.DomainAccessRule;

public interface DomainAccessRuleRepository extends AbstractRepository<DomainAccessRule> {
	
	public DomainAccessRule findById(long id);
}
