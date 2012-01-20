package org.linagora.linShare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.Unit;
import org.linagora.linShare.core.repository.UnitRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class UnitRepositoryImpl extends AbstractRepositoryImpl<Unit> implements UnitRepository {

	public UnitRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Unit findById(long id) {
		List<Unit> unit= findByCriteria(Restrictions.eq("id", id));
		if (unit == null || unit.isEmpty()) {
			return null;
		} else if (unit.size() == 1) {
			return unit.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Unit entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Unit.class).add(Restrictions.eq("id",entity.getPersistenceId()));
		return det;
	}

	
}
