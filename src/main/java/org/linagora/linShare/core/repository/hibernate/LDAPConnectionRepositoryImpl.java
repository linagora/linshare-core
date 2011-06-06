package org.linagora.linShare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.repository.LDAPConnectionRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class LDAPConnectionRepositoryImpl extends
		AbstractRepositoryImpl<LDAPConnection> implements
		LDAPConnectionRepository {

	public LDAPConnectionRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(LDAPConnection entity) {
		DetachedCriteria det = DetachedCriteria.forClass( LDAPConnection.class )
		.add(Restrictions.eq( "identifier", entity.getIdentifier() ) );
		return det;
	}

	public LDAPConnection findById(String identifier) {
		List<LDAPConnection> conns = findByCriteria(Restrictions.eq("identifier", identifier));
		
		if (conns == null || conns.isEmpty()) {
			return null;
		} else if (conns.size() == 1) {
			return conns.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

}
