package org.linagora.linShare.core.repository;

import org.linagora.linShare.core.domain.entities.Unit;

public interface UnitRepository extends AbstractRepository<Unit> {

	public Unit findById(long id);
}
