package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.Unit;

public interface UnitRepository extends AbstractRepository<Unit> {

	public Unit findById(long id);
}
