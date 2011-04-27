package org.linagora.linShare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.repository.DomainRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainRepositoryImpl extends
		AbstractRepositoryImpl<Domain> implements
		DomainRepository {

	public DomainRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Domain entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Document.class).add(
				Restrictions.eq("identifier", entity.getIdentifier()));
		return det;
	}

	public Domain findById(String identifier) {
		List<Domain> domains = findByCriteria(Restrictions.eq("identifier", identifier));
		
		if (domains == null || domains.isEmpty()) {
			return null;
		} else if (domains.size() == 1) {
			return domains.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

}
