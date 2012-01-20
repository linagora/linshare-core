package org.linagora.linShare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linShare.core.repository.DomainAccessPolicyRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainAccessPolicyRepositoryImpl extends AbstractRepositoryImpl<DomainAccessPolicy> implements DomainAccessPolicyRepository {

	public DomainAccessPolicyRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public DomainAccessPolicy findById(long id) {
		List<DomainAccessPolicy> domainAccessPolicy = findByCriteria(Restrictions.eq("id", id));
		if (domainAccessPolicy == null || domainAccessPolicy.isEmpty()) {
			return null;
		} else if (domainAccessPolicy.size() == 1) {
			return domainAccessPolicy.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainAccessPolicy entity) {
		DetachedCriteria det = DetachedCriteria.forClass(DomainAccessPolicy.class).add(
				Restrictions.eq("id", entity.getPersistenceId()));
		return det;
	}
}
