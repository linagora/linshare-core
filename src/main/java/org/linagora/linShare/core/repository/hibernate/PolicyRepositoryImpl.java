package org.linagora.linShare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.Policy;
import org.linagora.linShare.core.repository.PolicyRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class PolicyRepositoryImpl extends AbstractRepositoryImpl<Policy> implements PolicyRepository {

	
	public PolicyRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Policy entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Policy.class).add(Restrictions.eq("id",entity.getId()));
		return det;
	}

	@Override
	public Policy findById(long id) {
		List<Policy> policy= findByCriteria(Restrictions.eq("id", id));
		if (policy == null || policy.isEmpty()) {
			return null;
		} else if (policy.size() == 1) {
			return policy.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}
}
