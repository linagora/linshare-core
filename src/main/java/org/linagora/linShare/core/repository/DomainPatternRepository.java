package org.linagora.linShare.core.repository;

import org.linagora.linShare.core.domain.entities.DomainPattern;

public interface DomainPatternRepository extends AbstractRepository<DomainPattern> {
	
	public DomainPattern findById(String identifier);

}
