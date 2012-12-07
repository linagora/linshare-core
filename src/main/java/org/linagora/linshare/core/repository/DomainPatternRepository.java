package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.DomainPattern;

public interface DomainPatternRepository extends AbstractRepository<DomainPattern> {

	public DomainPattern findById(String identifier);

	List<DomainPattern> findAllUserDomainPattern();

	List<DomainPattern> findAllSystemDomainPattern();

}
