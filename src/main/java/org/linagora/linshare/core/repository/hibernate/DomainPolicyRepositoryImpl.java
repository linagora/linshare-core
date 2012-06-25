package org.linagora.linshare.core.repository.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainPolicyRepositoryImpl extends AbstractRepositoryImpl<DomainPolicy> implements DomainPolicyRepository {

	public DomainPolicyRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}


	@Override
	public DomainPolicy findById(String identifier) {
		List<DomainPolicy> domainPolicy = findByCriteria(Restrictions.eq("identifier", identifier));
		if (domainPolicy == null || domainPolicy.isEmpty()) {
			return null;
		} else if (domainPolicy.size() == 1) {
			return domainPolicy.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DomainPolicy entity) {
		DetachedCriteria det = DetachedCriteria.forClass(DomainPolicy.class).add(
				Restrictions.eq("identifier", entity.getIdentifier()));
		return det;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllIdentifiers() {

		return (List<String>) getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d.identifier from DomainPolicy d ");
						return query.setCacheable(true).list();
					}
				});
	}
}
