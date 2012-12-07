package org.linagora.linshare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class UserProviderRepositoryImpl extends AbstractRepositoryImpl<LdapUserProvider> implements UserProviderRepository {

	public UserProviderRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public LdapUserProvider findById(long id) {
		List<LdapUserProvider> provider = findByCriteria(Restrictions.eq("id", id));
		
		if (provider == null || provider.isEmpty()) {
			return null;
		} else if (provider.size() == 1) {
			return provider.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(LdapUserProvider entity) {
		DetachedCriteria det = DetachedCriteria.forClass(LdapUserProvider.class).add(
				Restrictions.eq("id", entity.getPersistenceId()));
		return det;
	}

}
