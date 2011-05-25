package org.linagora.linShare.core.repository.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.repository.DomainRepository;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainRepositoryImpl extends
		AbstractRepositoryImpl<Domain> implements
		DomainRepository {

	public DomainRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Domain entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Domain.class).add(
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
	
	@SuppressWarnings("unchecked")
	public List<String> findAllIdentifiers() {

		return (List<String>) getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(final Session session)
							throws HibernateException, SQLException {
						final Query query = session.createQuery("select d.identifier from Domain d");
						return query.setCacheable(true).list();
					}
				});

	}
}
