package org.linagora.linShare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.repository.DomainPatternRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainPatternRepositoryImpl extends
		AbstractRepositoryImpl<DomainPattern> implements
		DomainPatternRepository {

	public DomainPatternRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainPattern entity) {
		DetachedCriteria det = DetachedCriteria.forClass( Document.class )
		.add(Restrictions.eq( "identifier", entity.getIdentifier() ) );
		return det;
	}

	public DomainPattern findById(String identifier) {
		List<DomainPattern> patterns = findByCriteria(Restrictions.eq("identifier", identifier));
		
		if (patterns == null || patterns.isEmpty()) {
			return null;
		} else if (patterns.size() == 1) {
			return patterns.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

}
