package org.linagora.linShare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.DomainAccessRule;
import org.linagora.linShare.core.repository.DomainAccessRuleRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainAccessRuleRepositoryImpl extends AbstractRepositoryImpl<DomainAccessRule> implements DomainAccessRuleRepository {

	public DomainAccessRuleRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public DomainAccessRule findById(long id) {
		List<DomainAccessRule> domainAccessRule = findByCriteria(Restrictions.eq("id", id));
		if (domainAccessRule == null || domainAccessRule.isEmpty()) {
			return null;
		} else if (domainAccessRule.size() == 1) {
			return domainAccessRule.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainAccessRule entity) {
		DetachedCriteria det = DetachedCriteria.forClass(DomainAccessRule.class).add(
				Restrictions.eq("id", entity.getPersistenceId()));
		return det;
	}
}
